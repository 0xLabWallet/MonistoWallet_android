package com.monistoWallet.modules.market.overview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.monistoWallet.modules.market.TimeDuration
import com.monistoWallet.modules.market.overview.MarketOverviewModule
import com.monistoWallet.modules.market.topplatforms.Platform
import com.monistoWallet.modules.market.topplatforms.TopPlatformItem
import com.monistoWallet.ui.compose.ComposeAppTheme

@Composable
fun TopPlatformsBoardView(
    board: MarketOverviewModule.TopPlatformsBoard,
    onSelectTimeDuration: (com.monistoWallet.modules.market.TimeDuration) -> Unit,
    onItemClick: (Platform) -> Unit,
    onClickSeeAll: () -> Unit
) {
    TopBoardHeader(
        title = board.title,
        iconRes = board.iconRes,
        select = board.timeDurationSelect,
        onSelect = onSelectTimeDuration,
        onClickSeeAll = onClickSeeAll
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ComposeAppTheme.colors.lawrence)
    ) {
        board.items.forEach {
            TopPlatformItem(it, onItemClick)
        }

        SeeAllButton(onClickSeeAll)
    }

    Spacer(modifier = Modifier.height(24.dp))
}
