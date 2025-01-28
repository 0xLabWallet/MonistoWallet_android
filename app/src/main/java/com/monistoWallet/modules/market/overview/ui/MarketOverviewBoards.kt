package com.monistoWallet.modules.market.overview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.monistoWallet.modules.market.MarketModule
import com.monistoWallet.modules.market.MarketViewItem
import com.monistoWallet.modules.market.overview.MarketOverviewModule
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.Select
import com.monistoWallet.ui.compose.WithTranslatableTitle
import com.monistoWallet.ui.compose.components.ButtonSecondaryToggle
import com.monistoWallet.ui.compose.components.MarketCoinClear

@Composable
fun BoardsView(
    boards: List<MarketOverviewModule.Board>,
    navController: NavController,
    onClickSeeAll: (com.monistoWallet.modules.market.MarketModule.ListType) -> Unit,
    onSelectTopMarket: (com.monistoWallet.modules.market.TopMarket, com.monistoWallet.modules.market.MarketModule.ListType) -> Unit
) {
    boards.forEach { boardItem ->
        TopBoardHeader(
            title = boardItem.boardHeader.title,
            iconRes = boardItem.boardHeader.iconRes,
            select = boardItem.boardHeader.topMarketSelect,
            onSelect = { topMarket -> onSelectTopMarket(topMarket, boardItem.type) },
            onClickSeeAll = { onClickSeeAll(boardItem.type) }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ComposeAppTheme.colors.lawrence)
        ){
            boardItem.marketViewItems.forEach { coin ->
                MarketCoinWithBackground(coin, navController)
            }

            SeeAllButton { onClickSeeAll(boardItem.type) }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : WithTranslatableTitle> TopBoardHeader(
    title: Int,
    iconRes: Int,
    select: Select<T>,
    onSelect: (T) -> Unit,
    onClickSeeAll: () -> Unit
) {
    MarketsSectionHeader(
        title = title,
        onClick = onClickSeeAll,
        icon = painterResource(iconRes)
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            ButtonSecondaryToggle(
                select = select,
                onSelect = onSelect
            )
        }
    }
}

@Composable
private fun MarketCoinWithBackground(
    marketViewItem: MarketViewItem,
    navController: NavController
) {
    MarketCoinClear(
        marketViewItem.coinName,
        marketViewItem.coinCode,
        marketViewItem.iconUrl,
        marketViewItem.iconPlaceHolder,
        marketViewItem.coinRate,
        marketViewItem.marketDataValue,
        marketViewItem.rank
    ) {
        onItemClick(marketViewItem, navController)
    }
}
