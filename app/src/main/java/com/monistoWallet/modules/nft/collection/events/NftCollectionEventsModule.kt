package com.monistoWallet.modules.nft.collection.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.core.providers.nft.NftEventsProvider
import com.monistoWallet.entities.nft.NftEventMetadata
import com.monistoWallet.entities.nft.NftUid
import com.monistoWallet.modules.balance.BalanceXRateRepository
import com.monistoWallet.modules.coin.ContractInfo
import com.wallet0x.marketkit.models.BlockchainType

class NftCollectionEventsModule {

    class Factory(
        private val eventListType: NftEventListType,
        private val defaultEventType: NftEventMetadata.EventType = NftEventMetadata.EventType.Sale
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = NftCollectionEventsService(
                eventListType,
                defaultEventType,
                NftEventsProvider(com.monistoWallet.core.App.marketKit),
                BalanceXRateRepository("nft-collection-events", com.monistoWallet.core.App.currencyManager, com.monistoWallet.core.App.marketKit)
            )
            return NftCollectionEventsViewModel(service) as T
        }
    }
}

enum class SelectorDialogState {
     Closed, Opened
}

sealed class NftEventListType {
    data class Collection(
        val blockchainType: BlockchainType,
        val providerUid: String,
        val contracts: List<ContractInfo>
    ) : NftEventListType()

    data class Asset(val nftUid: NftUid) : NftEventListType()
}
