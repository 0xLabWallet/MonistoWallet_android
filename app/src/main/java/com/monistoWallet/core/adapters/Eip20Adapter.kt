package com.monistoWallet.core.adapters

import android.content.Context
import com.monistoWallet.core.AdapterState
import com.monistoWallet.core.BalanceData
import com.monistoWallet.core.ICoinManager
import com.monistoWallet.core.managers.EvmKitWrapper
import com.monistoWallet.core.managers.EvmLabelManager
import com.monistoWallet.entities.Wallet
import com.monistoWallet.entities.transactionrecords.TransactionRecord
import com.wallet0x.erc20kit.core.Erc20Kit
import com.wallet0x.ethereumkit.core.EthereumKit.SyncState
import com.wallet0x.ethereumkit.models.Address
import com.wallet0x.ethereumkit.models.Chain
import com.wallet0x.ethereumkit.models.DefaultBlockParameter
import com.wallet0x.ethereumkit.models.TransactionData
import com.wallet0x.marketkit.models.Token
import io.reactivex.Flowable
import io.reactivex.Single
import java.math.BigDecimal

class Eip20Adapter(
    context: Context,
    evmKitWrapper: EvmKitWrapper,
    contractAddress: String,
    baseToken: Token,
    coinManager: ICoinManager,
    wallet: Wallet,
    evmLabelManager: EvmLabelManager
) : BaseEvmAdapter(evmKitWrapper, wallet.decimal, coinManager) {

    private val transactionConverter = EvmTransactionConverter(coinManager, evmKitWrapper, wallet.transactionSource, com.monistoWallet.core.App.spamManager, baseToken, evmLabelManager)

    private val contractAddress: Address = Address(contractAddress)
    val eip20Kit: Erc20Kit = Erc20Kit.getInstance(context, this.evmKit, this.contractAddress)

    val pendingTransactions: List<TransactionRecord>
        get() = eip20Kit.getPendingTransactions().map { transactionConverter.transactionRecord(it) }

    // IAdapter

    override fun start() {
        // started via EthereumKitManager
    }

    override fun stop() {
        // stopped via EthereumKitManager
    }

    override fun refresh() {
        eip20Kit.refresh()
    }

    // IBalanceAdapter

    override val balanceState: AdapterState
        get() = convertToAdapterState(eip20Kit.syncState)

    override val balanceStateUpdatedFlowable: Flowable<Unit>
        get() = eip20Kit.syncStateFlowable.map { }

    override val balanceData: BalanceData
        get() = BalanceData(balanceInBigDecimal(eip20Kit.balance, decimal))

    override val balanceUpdatedFlowable: Flowable<Unit>
        get() = eip20Kit.balanceFlowable.map { Unit }

    // ISendEthereumAdapter

    override fun getTransactionData(amount: BigDecimal, address: Address): TransactionData {
        val amountBigInt = amount.movePointRight(decimal).toBigInteger()
        return eip20Kit.buildTransferTransactionData(address, amountBigInt)
    }

    private fun convertToAdapterState(syncState: SyncState): AdapterState = when (syncState) {
        is SyncState.Synced -> AdapterState.Synced
        is SyncState.NotSynced -> AdapterState.NotSynced(syncState.error)
        is SyncState.Syncing -> AdapterState.Syncing()
    }

    fun allowance(spenderAddress: Address, defaultBlockParameter: DefaultBlockParameter): Single<BigDecimal> {
        return eip20Kit.getAllowanceAsync(spenderAddress, defaultBlockParameter)
                .map {
                    scaleDown(it.toBigDecimal())
                }
    }

    companion object {
        fun clear(walletId: String) {
            val networkTypes = listOf(
                Chain.Ethereum,
                Chain.BinanceSmartChain,
                Chain.Polygon,
                Chain.Avalanche,
                Chain.Optimism,
                Chain.ArbitrumOne,
                Chain.Gnosis,
            )

            networkTypes.forEach {
                Erc20Kit.clear(com.monistoWallet.core.App.instance, it, walletId)
            }
        }
    }

}
