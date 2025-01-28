package com.monistoWallet.modules.transactionInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.ITransactionsAdapter
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.entities.CurrencyValue
import com.monistoWallet.entities.LastBlockInfo
import com.monistoWallet.entities.nft.NftAssetBriefMetadata
import com.monistoWallet.entities.nft.NftUid
import com.monistoWallet.entities.transactionrecords.TransactionRecord
import com.monistoWallet.modules.transactions.NftMetadataService
import com.monistoWallet.modules.transactions.TransactionItem
import com.monistoWallet.core.helpers.DateHelper
import com.wallet0x.marketkit.models.BlockchainType

object TransactionInfoModule {

    class Factory(private val transactionItem: TransactionItem) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val transactionSource = transactionItem.record.source
            val adapter: ITransactionsAdapter = com.monistoWallet.core.App.transactionAdapterManager.getAdapter(transactionSource)!!
            val service = TransactionInfoService(
                transactionItem.record,
                adapter,
                com.monistoWallet.core.App.marketKit,
                com.monistoWallet.core.App.currencyManager,
                NftMetadataService(com.monistoWallet.core.App.nftMetadataManager),
                com.monistoWallet.core.App.balanceHiddenManager.balanceHidden,
            )
            val factory = TransactionInfoViewItemFactory(
                com.monistoWallet.core.App.numberFormatter,
                Translator,
                DateHelper,
                com.monistoWallet.core.App.evmLabelManager,
                transactionSource.blockchain.type.resendable,
                com.monistoWallet.core.App.contactsRepository,
                transactionSource.blockchain.type
            )

            return TransactionInfoViewModel(service, factory, com.monistoWallet.core.App.contactsRepository) as T
        }

    }

    data class ExplorerData(val title: String, val url: String?)
}

sealed class TransactionStatusViewItem(val name: Int) {
    object Pending : TransactionStatusViewItem(R.string.Transactions_Pending)

    //progress in 0.0 .. 1.0
    class Processing(val progress: Float) : TransactionStatusViewItem(R.string.Transactions_Processing)
    object Completed : TransactionStatusViewItem(R.string.Transactions_Completed)
    object Failed : TransactionStatusViewItem(R.string.Transactions_Failed)
}

data class TransactionInfoItem(
    val record: TransactionRecord,
    val lastBlockInfo: LastBlockInfo?,
    val explorerData: TransactionInfoModule.ExplorerData,
    val rates: Map<String, CurrencyValue>,
    val nftMetadata: Map<NftUid, NftAssetBriefMetadata>,
    val hideAmount: Boolean,
)

val BlockchainType.resendable: Boolean
    get() =
        when (this) {
            BlockchainType.Optimism, BlockchainType.ArbitrumOne -> false
            else -> true
        }
