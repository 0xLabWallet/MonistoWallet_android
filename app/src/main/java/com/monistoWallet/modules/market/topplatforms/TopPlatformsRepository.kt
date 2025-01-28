package com.monistoWallet.modules.market.topplatforms

import com.monistoWallet.core.managers.CurrencyManager
import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.TimeDuration
import com.monistoWallet.modules.market.sortedByDescendingNullLast
import com.monistoWallet.modules.market.sortedByNullLast
import com.wallet0x.marketkit.models.TopPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class TopPlatformsRepository(
    private val marketKit: MarketKitWrapper,
    private val currencyManager: CurrencyManager,
    private val apiTag: String,
) {
    private var itemsCache: List<TopPlatform>? = null

    suspend fun get(
        sortingField: com.monistoWallet.modules.market.SortingField,
        timeDuration: com.monistoWallet.modules.market.TimeDuration,
        forceRefresh: Boolean,
        limit: Int? = null,
    ) = withContext(Dispatchers.IO) {
        val currentCache = itemsCache

        val items = if (forceRefresh || currentCache == null) {
            marketKit.topPlatformsSingle(currencyManager.baseCurrency.code, apiTag).await()
        } else {
            currentCache
        }

        itemsCache = items

        val topPlatformsByPeriod = getTopPlatformItems(items, timeDuration)

        topPlatformsByPeriod.sort(sortingField).let { sortedList ->
            limit?.let { sortedList.take(it) } ?: sortedList
        }
    }

    companion object {
        fun getTopPlatformItems(
                topPlatforms: List<TopPlatform>,
                timeDuration: com.monistoWallet.modules.market.TimeDuration
        ): List<TopPlatformItem> {
            return topPlatforms.map { platform ->
                val prevRank = when (timeDuration) {
                    com.monistoWallet.modules.market.TimeDuration.OneDay -> null
                    com.monistoWallet.modules.market.TimeDuration.SevenDay -> platform.rank1W
                    com.monistoWallet.modules.market.TimeDuration.ThirtyDay -> platform.rank1M
                    com.monistoWallet.modules.market.TimeDuration.ThreeMonths -> platform.rank3M
                }

                val rankDiff = if (prevRank == platform.rank || prevRank == null) {
                    null
                } else {
                    prevRank - platform.rank
                }

                val marketCapDiff = when (timeDuration) {
                    com.monistoWallet.modules.market.TimeDuration.OneDay -> null
                    com.monistoWallet.modules.market.TimeDuration.SevenDay -> platform.change1W
                    com.monistoWallet.modules.market.TimeDuration.ThirtyDay -> platform.change1M
                    com.monistoWallet.modules.market.TimeDuration.ThreeMonths -> platform.change3M
                }

                TopPlatformItem(
                        Platform(platform.blockchain.uid, platform.blockchain.name),
                        platform.rank,
                        platform.protocols,
                        platform.marketCap,
                        rankDiff,
                        marketCapDiff
                )
            }
        }
    }

    fun List<TopPlatformItem>.sort(sortingField: com.monistoWallet.modules.market.SortingField) = when (sortingField) {
        com.monistoWallet.modules.market.SortingField.HighestCap -> sortedByDescendingNullLast { it.marketCap }
        com.monistoWallet.modules.market.SortingField.LowestCap -> sortedByNullLast { it.marketCap }
        com.monistoWallet.modules.market.SortingField.TopGainers -> sortedByDescendingNullLast { it.changeDiff }
        com.monistoWallet.modules.market.SortingField.TopLosers -> sortedByNullLast { it.changeDiff }
        else -> this
    }

}
