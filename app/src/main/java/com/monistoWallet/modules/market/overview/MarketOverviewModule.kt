package com.monistoWallet.modules.market.overview

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.market.MarketModule
import com.monistoWallet.modules.market.MarketViewItem
import com.monistoWallet.modules.market.search.MarketSearchModule.DiscoveryItem.Category
import com.monistoWallet.modules.market.topcoins.MarketTopMoversRepository
import com.monistoWallet.modules.market.topnftcollections.TopNftCollectionViewItem
import com.monistoWallet.modules.market.topnftcollections.TopNftCollectionsViewItemFactory
import com.monistoWallet.modules.market.topplatforms.TopPlatformViewItem
import com.monistoWallet.ui.compose.Select
import com.monistoWallet.ui.extensions.MetricData
import java.math.BigDecimal

object MarketOverviewModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val topMarketsRepository = MarketTopMoversRepository(com.monistoWallet.core.App.marketKit)
            val service = MarketOverviewService(
                topMarketsRepository,
                com.monistoWallet.core.App.marketKit,
                com.monistoWallet.core.App.backgroundManager,
                com.monistoWallet.core.App.currencyManager
            )
            val topNftCollectionsViewItemFactory = TopNftCollectionsViewItemFactory(com.monistoWallet.core.App.numberFormatter)
            return MarketOverviewViewModel(service, topNftCollectionsViewItemFactory, com.monistoWallet.core.App.currencyManager) as T
        }
    }

    @Immutable
    data class ViewItem(
        val marketMetrics: MarketMetrics,
        val boards: List<Board>,
        val topNftCollectionsBoard: TopNftCollectionsBoard,
//        val topSectorsBoard: TopSectorsBoard,
        val topPlatformsBoard: TopPlatformsBoard,
    )

    data class MarketMetrics(
        val totalMarketCap: MetricData,
        val volume24h: MetricData,
        val defiCap: MetricData,
        val defiTvl: MetricData,
    ) {
        operator fun get(page: Int) = when (page) {
            0 -> totalMarketCap
            1 -> volume24h
            2 -> defiCap
            3 -> defiTvl
            else -> throw  IndexOutOfBoundsException()
        }
    }

    data class MarketMetricsPoint(
        val value: BigDecimal,
        val timestamp: Long
    )

    data class Board(
        val boardHeader: BoardHeader,
        val marketViewItems: List<MarketViewItem>,
        val type: com.monistoWallet.modules.market.MarketModule.ListType
    )

    data class BoardHeader(
        val title: Int,
        val iconRes: Int,
        val topMarketSelect: Select<com.monistoWallet.modules.market.TopMarket>
    )

    data class TopNftCollectionsBoard(
        val title: Int,
        val iconRes: Int,
        val timeDurationSelect: Select<com.monistoWallet.modules.market.TimeDuration>,
        val collections: List<TopNftCollectionViewItem>
    )

    data class TopSectorsBoard(
        val title: Int,
        val iconRes: Int,
        val items: List<Category>
    )

    data class TopPlatformsBoard(
        val title: Int,
        val iconRes: Int,
        val timeDurationSelect: Select<com.monistoWallet.modules.market.TimeDuration>,
        val items: List<TopPlatformViewItem>
    )

}
