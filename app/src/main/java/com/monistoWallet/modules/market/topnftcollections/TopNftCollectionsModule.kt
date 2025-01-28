package com.monistoWallet.modules.market.topnftcollections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.TimeDuration
import com.monistoWallet.ui.compose.Select
import com.wallet0x.marketkit.models.BlockchainType
import java.math.BigDecimal

object TopNftCollectionsModule {

    class Factory(val sortingField: com.monistoWallet.modules.market.SortingField, val timeDuration: com.monistoWallet.modules.market.TimeDuration) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val topNftCollectionsRepository = TopNftCollectionsRepository(com.monistoWallet.core.App.marketKit)
            val service = TopNftCollectionsService(sortingField, timeDuration, topNftCollectionsRepository)
            val topNftCollectionsViewItemFactory = TopNftCollectionsViewItemFactory(com.monistoWallet.core.App.numberFormatter)
            return TopNftCollectionsViewModel(service, topNftCollectionsViewItemFactory) as T
        }
    }

}

data class Menu(
    val sortingFieldSelect: Select<com.monistoWallet.modules.market.SortingField>,
    val timeDurationSelect: Select<com.monistoWallet.modules.market.TimeDuration>
)

data class TopNftCollectionViewItem(
    val blockchainType: BlockchainType,
    val uid: String,
    val name: String,
    val imageUrl: String?,
    val volume: String,
    val volumeDiff: BigDecimal,
    val order: Int,
    val floorPrice: String
)
