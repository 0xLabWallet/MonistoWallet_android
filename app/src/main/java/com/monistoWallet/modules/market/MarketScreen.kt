package com.monistoWallet.modules.market

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.modules.market.favorites.MarketFavoritesScreen
import com.monistoWallet.modules.market.overview.MarketOverviewScreen
import com.monistoWallet.modules.market.posts.MarketPostsScreen
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.MenuItem
import com.monistoWallet.ui.compose.components.TabItem
import com.monistoWallet.ui.compose.components.Tabs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarketScreen(navController: NavController) {
    val marketViewModel = viewModel<MarketViewModel>(factory = MarketModule.Factory())
    val tabs = marketViewModel.tabs
    val selectedTab = marketViewModel.selectedTab
    val pagerState = rememberPagerState(initialPage = selectedTab.ordinal) { tabs.size }

    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = stringResource(R.string.Market_Title),
            menuItems = listOf(
                MenuItem(
                    title = TranslatableString.ResString(R.string.Market_Search),
                    icon = R.drawable.icon_search,
                    onClick = {
                        navController.slideFromRight(R.id.marketSearchFragment)
                    }
                ),
                MenuItem(
                    title = TranslatableString.ResString(R.string.Market_Filters),
                    icon = R.drawable.ic_manage_2_24,
                    onClick = {
                        navController.slideFromRight(R.id.marketAdvancedSearchFragment)
                    }
                ),
            )
        )

        LaunchedEffect(key1 = selectedTab, block = {
            pagerState.scrollToPage(selectedTab.ordinal)
        })
        val tabItems = tabs.map {
            TabItem(stringResource(id = it.titleResId), it == selectedTab, it)
        }
        Tabs(tabItems, onClick = {
            marketViewModel.onSelect(it)
        })

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            when (tabs[page]) {
                com.monistoWallet.modules.market.MarketModule.Tab.Overview -> MarketOverviewScreen(navController)
                com.monistoWallet.modules.market.MarketModule.Tab.Posts -> MarketPostsScreen()
                com.monistoWallet.modules.market.MarketModule.Tab.Watchlist -> MarketFavoritesScreen(navController)
            }
        }
    }
}
