package com.monistoWallet.core.adapters

import com.monistoWallet.core.AdapterState
import com.monistoWallet.core.ITransactionsAdapter
import com.monistoWallet.core.managers.TronKitWrapper
import com.monistoWallet.entities.LastBlockInfo
import com.monistoWallet.entities.transactionrecords.TransactionRecord
import com.monistoWallet.modules.transactions.FilterTransactionType
import com.wallet0x.marketkit.models.Token
import com.wallet0x.marketkit.models.TokenType
import com.wallet0x.tronkit.TronKit
import com.wallet0x.tronkit.hexStringToByteArray
import com.wallet0x.tronkit.models.TransactionTag
import com.wallet0x.tronkit.network.Network
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.rx2.asFlowable
import kotlinx.coroutines.rx2.rxSingle

class TronTransactionsAdapter(
    val tronKitWrapper: TronKitWrapper,
    private val transactionConverter: TronTransactionConverter
) : ITransactionsAdapter {

    private val tronKit = tronKitWrapper.tronKit

    override val explorerTitle: String
        get() = "Tronscan"

    override fun getTransactionUrl(transactionHash: String): String = when (tronKit.network) {
        Network.Mainnet -> "https://tronscan.io/#/transaction/$transactionHash"
        Network.ShastaTestnet -> "https://shasta.tronscan.org/#/transaction/$transactionHash"
        Network.NileTestnet -> "https://nile.tronscan.org/#/transaction/$transactionHash"
    }

    override val lastBlockInfo: LastBlockInfo
        get() = tronKit.lastBlockHeight.toInt().let { LastBlockInfo(it) }

    override val lastBlockUpdatedFlowable: Flowable<Unit>
        get() = tronKit.lastBlockHeightFlow.asFlowable().map { }

    override val transactionsState: AdapterState
        get() = convertToAdapterState(tronKit.syncState)

    override val transactionsStateUpdatedFlowable: Flowable<Unit>
        get() = tronKit.syncStateFlow.asFlowable().map {}

    override fun getTransactionsAsync(
        from: TransactionRecord?,
        token: Token?,
        limit: Int,
        transactionType: FilterTransactionType
    ): Single<List<TransactionRecord>> {
        return rxSingle {
            tronKit.getFullTransactions(
                getFilters(token, transactionType),
                from?.transactionHash?.hexStringToByteArray(),
                limit
            )
        }.map {
            it.map { tx -> transactionConverter.transactionRecord(tx) }
        }
    }

    override fun getTransactionRecordsFlowable(
        token: Token?,
        transactionType: FilterTransactionType
    ): Flowable<List<TransactionRecord>> {
        return tronKit.getFullTransactionsFlow(getFilters(token, transactionType)).asFlowable().map {
            it.map { tx -> transactionConverter.transactionRecord(tx) }
        }
    }

    private fun convertToAdapterState(syncState: TronKit.SyncState): AdapterState =
        when (syncState) {
            is TronKit.SyncState.Synced -> AdapterState.Synced
            is TronKit.SyncState.NotSynced -> AdapterState.NotSynced(syncState.error)
            is TronKit.SyncState.Syncing -> AdapterState.Syncing()
        }

    private fun coinTagName(token: Token) = when (val type = token.type) {
        TokenType.Native -> TransactionTag.TRX_COIN
        is TokenType.Eip20 -> type.address
        else -> ""
    }

    private fun incomingTag(token: Token) = when (val type = token.type) {
        TokenType.Native -> TransactionTag.TRX_COIN_INCOMING
        is TokenType.Eip20 -> TransactionTag.trc20Incoming(type.address)
        else -> ""
    }

    private fun outgoingTag(token: Token) = when (val type = token.type) {
        TokenType.Native -> TransactionTag.TRX_COIN_OUTGOING
        is TokenType.Eip20 -> TransactionTag.trc20Outgoing(type.address)
        else -> ""
    }

    private fun getFilters(token: Token?, filter: FilterTransactionType): List<List<String>> {
        val filterCoin = token?.let {
            coinTagName(it)
        }

        val filterTag = when (filter) {
            FilterTransactionType.All -> null
            FilterTransactionType.Incoming -> when {
                token != null -> incomingTag(token)
                else -> TransactionTag.INCOMING
            }

            FilterTransactionType.Outgoing -> when {
                token != null -> outgoingTag(token)
                else -> TransactionTag.OUTGOING
            }

            FilterTransactionType.Swap -> TransactionTag.SWAP
            FilterTransactionType.Approve -> TransactionTag.TRC20_APPROVE
        }

        return listOfNotNull(filterCoin, filterTag).map { listOf(it) }
    }
}
