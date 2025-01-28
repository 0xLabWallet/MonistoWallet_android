package com.monistoWallet.core.adapters

import com.monistoWallet.R
import com.monistoWallet.core.AdapterState
import com.monistoWallet.core.AppLogger
import com.monistoWallet.core.BalanceData
import com.monistoWallet.core.IAdapter
import com.monistoWallet.core.IBalanceAdapter
import com.monistoWallet.core.IReceiveAdapter
import com.monistoWallet.core.ISendBinanceAdapter
import com.monistoWallet.core.ITransactionsAdapter
import com.monistoWallet.core.LocalizedException
import com.monistoWallet.core.UnsupportedFilterException
import com.monistoWallet.entities.LastBlockInfo
import com.monistoWallet.entities.Wallet
import com.monistoWallet.entities.transactionrecords.TransactionRecord
import com.monistoWallet.entities.transactionrecords.binancechain.BinanceChainIncomingTransactionRecord
import com.monistoWallet.entities.transactionrecords.binancechain.BinanceChainOutgoingTransactionRecord
import com.monistoWallet.modules.transactions.FilterTransactionType
import com.wallet0x.binancechainkit.BinanceChainKit
import com.wallet0x.binancechainkit.core.api.BinanceError
import com.wallet0x.binancechainkit.models.TransactionFilterType
import com.wallet0x.binancechainkit.models.TransactionInfo
import com.wallet0x.marketkit.models.Token
import io.reactivex.Flowable
import io.reactivex.Single
import java.math.BigDecimal

class BinanceAdapter(
    private val binanceKit: BinanceChainKit,
    private val symbol: String,
    private val feeToken: Token,
    private val wallet: Wallet,
) : IAdapter, ITransactionsAdapter, IBalanceAdapter, IReceiveAdapter, ISendBinanceAdapter {

    private val asset = binanceKit.register(symbol)
    private val token = wallet.token

    private val syncState: AdapterState
        get() = when (val kitSyncState = binanceKit.syncState) {
            BinanceChainKit.SyncState.Synced -> AdapterState.Synced
            BinanceChainKit.SyncState.Syncing -> AdapterState.Syncing()
            is BinanceChainKit.SyncState.NotSynced -> AdapterState.NotSynced(kitSyncState.error)
        }

    // IAdapter

    override fun start() {
        // handled by BinanceKitManager
    }

    override fun stop() {
        binanceKit.unregister(asset)
    }

    override fun refresh() {
        // handled by BinanceKitManager
    }

    override val debugInfo: String
        get() = ""


    // IBalanceAdapter

    override val balanceState: AdapterState
        get() = syncState

    override val balanceStateUpdatedFlowable: Flowable<Unit>
        get() = binanceKit.syncStateFlowable.map { }

    override val balanceData: BalanceData
        get() = BalanceData(asset.balance)

    override val balanceUpdatedFlowable: Flowable<Unit>
        get() = asset.balanceFlowable.map { }

    // ITransactionsAdapter

    override val explorerTitle: String =
        "binance.org"

    override val transactionsState: AdapterState
        get() = syncState

    override val transactionsStateUpdatedFlowable: Flowable<Unit>
        get() = binanceKit.syncStateFlowable.map { }

    override val lastBlockInfo: LastBlockInfo?
        get() = binanceKit.latestBlock?.height?.let { LastBlockInfo(it) }

    override val lastBlockUpdatedFlowable: Flowable<Unit>
        get() = binanceKit.latestBlockFlowable.map { }

    override fun getTransactionRecordsFlowable(token: Token?, transactionType: FilterTransactionType): Flowable<List<TransactionRecord>> {
        return try {
            val filter = getBinanceTransactionTypeFilter(transactionType)
            asset.getTransactionsFlowable(filter).map { it.map { transactionRecord(it) } }
        } catch (e: UnsupportedFilterException) {
            Flowable.empty()
        }
    }

    override fun getTransactionsAsync(from: TransactionRecord?, token: Token?, limit: Int, transactionType: FilterTransactionType): Single<List<TransactionRecord>> {
        return try {
            val filter = getBinanceTransactionTypeFilter(transactionType)
            binanceKit
                .transactions(asset, filter, from?.transactionHash, limit)
                .map { it.map { transactionRecord(it) } }
        } catch (e: UnsupportedFilterException) {
            Single.just(listOf())
        }
    }

    private fun getBinanceTransactionTypeFilter(transactionType: FilterTransactionType): TransactionFilterType? {
        return when (transactionType) {
            FilterTransactionType.All -> null
            FilterTransactionType.Incoming -> TransactionFilterType.Incoming
            FilterTransactionType.Outgoing -> TransactionFilterType.Outgoing
            else -> throw UnsupportedFilterException()
        }
    }

    private fun transactionRecord(transaction: TransactionInfo): TransactionRecord {
        val myAddress = binanceKit.receiveAddress()
        val fromMine = transaction.from == myAddress
        val toMine = transaction.to == myAddress

        return when {
            fromMine && !toMine -> BinanceChainOutgoingTransactionRecord(
                transaction,
                feeToken,
                token,
                false,
                wallet.transactionSource
            )
            !fromMine && toMine -> BinanceChainIncomingTransactionRecord(
                transaction,
                feeToken,
                token,
                wallet.transactionSource
            )
            else -> BinanceChainOutgoingTransactionRecord(
                transaction,
                feeToken,
                token,
                true,
                wallet.transactionSource
            )
        }
    }

    override fun getTransactionUrl(transactionHash: String): String =
        "https://explorer.binance.org/tx/$transactionHash"

    // ISendBinanceAdapter

    override val availableBalance: BigDecimal
        get() {
            var availableBalance = asset.balance
            if (asset.symbol == "BNB") {
                availableBalance -= transferFee
            }
            return if (availableBalance < BigDecimal.ZERO) BigDecimal.ZERO else availableBalance
        }

    override val availableBinanceBalance: BigDecimal
        get() = binanceKit.binanceBalance

    override val fee: BigDecimal
        get() = transferFee

    override fun send(
        amount: BigDecimal,
        address: String,
        memo: String?,
        logger: AppLogger
    ): Single<Unit> {
        return binanceKit.send(symbol, address, amount, memo ?: "")
            .doOnSubscribe {
                logger.info("call binanceKit.send")
            }
            .onErrorResumeNext { Single.error(getException(it)) }
            .map { Unit }
    }

    private fun getException(error: Throwable): Exception {
        when (error) {
            is BinanceError -> {
                if (error.message.contains("receiver requires non-empty memo in transfer transaction")) {
                    return LocalizedException(R.string.Binance_Backend_Error_MemoRequired)
                } else if (error.message.contains("requires the memo contains only digits")) {
                    return LocalizedException(R.string.Binance_Backend_Error_RequiresDigits)
                }
            }
        }
        return Exception(error.message)
    }

    override fun validate(address: String) {
        binanceKit.validateAddress(address)
    }

    // IReceiveAdapter

    override val receiveAddress: String
        get() = binanceKit.receiveAddress()

    override val isMainNet: Boolean = true

    companion object {
        const val confirmationsThreshold = 1
        val transferFee = BigDecimal.valueOf(0.000075)

        fun clear(walletId: String) {
            val networkType = BinanceChainKit.NetworkType.MainNet
            BinanceChainKit.clear(com.monistoWallet.core.App.instance, networkType, walletId)
        }

    }
}
