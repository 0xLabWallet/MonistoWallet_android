package com.monistoWallet.modules.nft.asset

import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.entities.nft.NftUid
import com.monistoWallet.modules.balance.BalanceXRateRepository

object NftAssetModule {

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val collectionUid: String,
        private val nftUid: NftUid
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = NftAssetService(
                collectionUid,
                nftUid,
                com.monistoWallet.core.App.accountManager,
                com.monistoWallet.core.App.nftAdapterManager,
                com.monistoWallet.core.App.nftMetadataManager.provider(nftUid.blockchainType),
                BalanceXRateRepository("nft-asset", com.monistoWallet.core.App.currencyManager, com.monistoWallet.core.App.marketKit)
            )
            return NftAssetViewModel(service) as T
        }
    }

    const val collectionUidKey = "collectionUidKey"
    const val nftUidKey = "nftUidKey"

    fun prepareParams(collectionUid: String?, nftUid: NftUid) = bundleOf(
        collectionUidKey to collectionUid,
        nftUidKey to nftUid.uid
    )

    enum class Tab(@StringRes val titleResId: Int) {
        Overview(R.string.NftAsset_Overview),
        Activity(R.string.NftAsset_Activity);
    }

    enum class NftAssetAction(@StringRes val title: Int) {
        Share(R.string.NftAsset_Action_Share),
        Save(R.string.NftAsset_Action_Save)
    }
}