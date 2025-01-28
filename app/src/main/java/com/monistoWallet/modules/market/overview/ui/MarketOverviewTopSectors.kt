package com.monistoWallet.modules.market.overview.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.monistoWallet.modules.market.overview.MarketOverviewModule
import com.monistoWallet.ui.compose.components.CategoryCard
import com.wallet0x.marketkit.models.CoinCategory

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopSectorsBoardView(
    board: MarketOverviewModule.TopSectorsBoard,
    onItemClick: (CoinCategory) -> Unit
) {
    MarketsSectionHeader(
        title = board.title,
        icon = painterResource(board.iconRes),
    )

    MarketsHorizontalCards(board.items.size) {
        val category = board.items[it]
        CategoryCard(category) {
            onItemClick(category.coinCategory)
        }
    }
}
