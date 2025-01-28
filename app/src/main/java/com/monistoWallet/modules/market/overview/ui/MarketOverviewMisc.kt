package com.monistoWallet.modules.market.overview.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.modules.coin.CoinFragment
import com.monistoWallet.modules.market.MarketViewItem
import com.monistoWallet.ui.compose.components.RowUniversal
import com.monistoWallet.ui.compose.components.body_leah

fun onItemClick(marketViewItem: MarketViewItem, navController: NavController) {
    val arguments = CoinFragment.prepareParams(marketViewItem.coinUid, "market_overview")
    navController.slideFromRight(R.id.coinFragment, arguments)
}

@Composable
fun SeeAllButton(onClick: () -> Unit) {
    RowUniversal(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = onClick
    ) {
        body_leah(
            text = stringResource(R.string.Market_SeeAll),
            maxLines = 1,
        )
        Spacer(Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = "right arrow icon",
        )
    }
}
