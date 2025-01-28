package com.monistoWallet.modules.market.topnftcollections

import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.TimeDuration
import com.monistoWallet.modules.market.sortedByDescendingNullLast
import com.monistoWallet.modules.market.sortedByNullLast
import com.monistoWallet.modules.nft.NftCollectionItem
import com.monistoWallet.modules.nft.nftCollectionItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TopNftCollectionsRepository(
    private val marketKit: MarketKitWrapper
) {
    private var itemsCache: List<com.monistoWallet.modules.nft.NftCollectionItem>? = null


    suspend fun get(
        sortingField: com.monistoWallet.modules.market.SortingField,
        timeDuration: com.monistoWallet.modules.market.TimeDuration,
        forceRefresh: Boolean,
        limit: Int? = null,
    ) = withContext(Dispatchers.IO) {
        val currentCache = itemsCache

        val items = if (forceRefresh || currentCache == null) {
            marketKit.nftCollections().map { it.nftCollectionItem }
        } else {
            currentCache
        }

        itemsCache = items

         items.sort(sortingField, timeDuration).let { sortedList ->
            limit?.let { sortedList.take(it) } ?: sortedList
        }
    }

    private fun List<com.monistoWallet.modules.nft.NftCollectionItem>.sort(sortingField: com.monistoWallet.modules.market.SortingField, timeDuration: com.monistoWallet.modules.market.TimeDuration) =
        when (sortingField) {
            com.monistoWallet.modules.market.SortingField.HighestCap,
            com.monistoWallet.modules.market.SortingField.LowestCap -> this
            com.monistoWallet.modules.market.SortingField.HighestVolume -> sortedByDescendingNullLast { it.volume(timeDuration)?.value }
            com.monistoWallet.modules.market.SortingField.LowestVolume -> sortedByNullLast { it.volume(timeDuration)?.value }
            com.monistoWallet.modules.market.SortingField.TopGainers -> sortedByDescendingNullLast { it.volumeDiff(timeDuration) }
            com.monistoWallet.modules.market.SortingField.TopLosers -> sortedByNullLast { it.volumeDiff(timeDuration) }
        }

    private fun com.monistoWallet.modules.nft.NftCollectionItem.volume(timeDuration: com.monistoWallet.modules.market.TimeDuration) =
        when (timeDuration) {
            com.monistoWallet.modules.market.TimeDuration.OneDay -> oneDayVolume
            com.monistoWallet.modules.market.TimeDuration.SevenDay -> sevenDayVolume
            com.monistoWallet.modules.market.TimeDuration.ThirtyDay -> thirtyDayVolume
            com.monistoWallet.modules.market.TimeDuration.ThreeMonths -> null
        }

    private fun com.monistoWallet.modules.nft.NftCollectionItem.volumeDiff(timeDuration: com.monistoWallet.modules.market.TimeDuration) =
        when (timeDuration) {
            com.monistoWallet.modules.market.TimeDuration.OneDay -> oneDayVolumeDiff
            com.monistoWallet.modules.market.TimeDuration.SevenDay -> sevenDayVolumeDiff
            com.monistoWallet.modules.market.TimeDuration.ThirtyDay -> thirtyDayVolumeDiff
            com.monistoWallet.modules.market.TimeDuration.ThreeMonths -> null
        }

}
