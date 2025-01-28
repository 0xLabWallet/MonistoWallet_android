package com.monistoWallet.modules.nft.collection

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.modules.nft.collection.overview.NftCollectionOverviewService
import com.monistoWallet.modules.nft.collection.overview.NftCollectionOverviewViewModel
import com.monistoWallet.modules.xrate.XRateService
import com.wallet0x.marketkit.models.BlockchainType

object NftCollectionModule {

    class Factory(
        private val blockchainType: BlockchainType,
        private val collectionUid: String
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = NftCollectionOverviewService(blockchainType, collectionUid, com.monistoWallet.core.App.nftMetadataManager.provider(blockchainType), com.monistoWallet.core.App.marketKit)
            return NftCollectionOverviewViewModel(
                service,
                com.monistoWallet.core.App.numberFormatter,
                XRateService(com.monistoWallet.core.App.marketKit, com.monistoWallet.core.App.currencyManager.baseCurrency),
                com.monistoWallet.core.App.marketKit
            ) as T
        }

    }

    enum class Tab(@StringRes val titleResId: Int) {
        Overview(R.string.NftCollection_Overview),
        Items(R.string.NftCollection_Items),
        Activity(R.string.NftCollection_Activity)
    }

}
