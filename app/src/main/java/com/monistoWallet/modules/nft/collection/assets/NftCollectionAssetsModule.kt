package com.monistoWallet.modules.nft.collection.assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.balance.BalanceXRateRepository
import com.wallet0x.marketkit.models.BlockchainType

object NftCollectionAssetsModule {

    class Factory(
        private val blockchainType: BlockchainType,
        private val collectionUid: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = NftCollectionAssetsService(
                blockchainType,
                collectionUid,
                com.monistoWallet.core.App.nftMetadataManager.provider(blockchainType),
                BalanceXRateRepository("nft-collection-assets", com.monistoWallet.core.App.currencyManager, com.monistoWallet.core.App.marketKit)
            )
            return NftCollectionAssetsViewModel(service) as T
        }
    }

}
