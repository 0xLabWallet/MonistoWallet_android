package com.monistoWallet.modules.market.overview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.monistoWallet.R
import com.monistoWallet.modules.market.MarketDataValue
import com.monistoWallet.modules.market.TimeDuration
import com.monistoWallet.modules.market.overview.MarketOverviewModule
import com.monistoWallet.modules.market.topnftcollections.TopNftCollectionViewItem
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.MarketCoinFirstRow
import com.monistoWallet.ui.compose.components.MarketCoinSecondRow
import com.monistoWallet.ui.compose.components.NftIcon
import com.monistoWallet.ui.compose.components.SectionItemBorderedRowUniversalClear
import com.wallet0x.marketkit.models.BlockchainType

@Composable
fun TopNftCollectionsBoardView(
    board: MarketOverviewModule.TopNftCollectionsBoard,
    onSelectTimeDuration: (com.monistoWallet.modules.market.TimeDuration) -> Unit,
    onClickCollection: (BlockchainType, String) -> Unit,
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
        board.collections.forEach { collection ->
            TopNftCollectionView(collection) {
                onClickCollection(collection.blockchainType, collection.uid)
            }
        }

        SeeAllButton(onClickSeeAll)
    }

    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun TopNftCollectionView(
    collection: TopNftCollectionViewItem,
    onClick: () -> Unit
) {
    SectionItemBorderedRowUniversalClear(
        onClick = onClick,
        borderBottom = true
    ) {
        NftIcon(
            iconUrl = collection.imageUrl ?: "",
            placeholder = R.drawable.coin_placeholder,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            MarketCoinFirstRow(collection.name, collection.volume)
            Spacer(modifier = Modifier.height(3.dp))
            MarketCoinSecondRow(
                collection.floorPrice,
                com.monistoWallet.modules.market.MarketDataValue.Diff(collection.volumeDiff),
                "${collection.order}"
            )
        }
    }
}
