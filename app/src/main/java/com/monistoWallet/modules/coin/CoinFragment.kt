package com.monistoWallet.modules.coin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.modules.coin.coinmarkets.CoinMarketsScreen
import com.monistoWallet.modules.coin.overview.ui.CoinOverviewScreen
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.*
import com.monistoWallet.core.helpers.HudHelper
import kotlinx.coroutines.launch

class CoinFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val coinUid = requireArguments().getString(COIN_UID_KEY, "")
        val apiTag = requireArguments().getString(API_TAG_KEY, "")

        CoinScreen(
            coinUid,
            apiTag,
            coinViewModel(coinUid),
            navController,
            childFragmentManager
        )
    }

    private fun coinViewModel(coinUid: String): CoinViewModel? = try {
        val viewModel by navGraphViewModels<CoinViewModel>(R.id.coinFragment) {
            CoinModule.Factory(coinUid)
        }
        viewModel
    } catch (e: Exception) {
        null
    }

    companion object {
        private const val COIN_UID_KEY = "coin_uid_key"
        private const val API_TAG_KEY = "api_tag_key"

        fun prepareParams(coinUid: String, apiTag: String) = bundleOf(COIN_UID_KEY to coinUid, API_TAG_KEY to apiTag)
    }
}

@Composable
fun CoinScreen(
    coinUid: String,
    apiTag: String,
    coinViewModel: CoinViewModel?,
    navController: NavController,
    fragmentManager: FragmentManager
) {
    if (coinViewModel != null) {
        CoinTabs(apiTag, coinViewModel, navController, fragmentManager)
    } else {
        CoinNotFound(coinUid, navController)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoinTabs(
    apiTag: String,
    viewModel: CoinViewModel,
    navController: NavController,
    fragmentManager: FragmentManager
) {
    val tabs = viewModel.tabs
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current

    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = viewModel.fullCoin.coin.code,
            navigationIcon = {
                HsBackButton(onClick = { navController.popBackStack() })
            },
            menuItems = buildList {
                if (viewModel.isWatchlistEnabled) {
                    if (viewModel.isFavorite) {
                        add(
                            MenuItem(
                                title = TranslatableString.ResString(R.string.CoinPage_Unfavorite),
                                icon = R.drawable.ic_filled_star_24,
                                tint = ComposeAppTheme.colors.jacob,
                                onClick = { viewModel.onUnfavoriteClick() }
                            )
                        )
                    } else {
                        add(
                            MenuItem(
                                title = TranslatableString.ResString(R.string.CoinPage_Favorite),
                                icon = R.drawable.ic_star_24,
                                onClick = { viewModel.onFavoriteClick() }
                            )
                        )
                    }
                }
            }
        )

        val selectedTab = tabs[pagerState.currentPage]
        val tabItems = tabs.map {
            TabItem(stringResource(id = it.titleResId), it == selectedTab, it)
        }
        Tabs(tabItems, onClick = { tab ->
            coroutineScope.launch {
                pagerState.scrollToPage(tab.ordinal)

//                if (tab == CoinModule.Tab.Details && viewModel.shouldShowSubscriptionInfo()) {
//                    viewModel.subscriptionInfoShown()
//
//                    delay(1000)
//                    navController.slideFromBottom(R.id.subscriptionInfoFragment)
//                }
            }
        })

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            when (tabs[page]) {
                CoinModule.Tab.Overview -> {
                    CoinOverviewScreen(
                        apiTag = apiTag,
                        fullCoin = viewModel.fullCoin,
                        navController = navController
                    )
                }

                CoinModule.Tab.Market -> {
                    CoinMarketsScreen(fullCoin = viewModel.fullCoin)
                }

//                CoinModule.Tab.Details -> {
//                    CoinAnalyticsScreen(
//                        apiTag = apiTag,
//                        fullCoin = viewModel.fullCoin,
//                        navController = navController,
//                        fragmentManager = fragmentManager
//                    )
//                }
//                CoinModule.Tab.Tweets -> {
//                    CoinTweetsScreen(fullCoin = viewModel.fullCoin)
//                }
            }
        }

        viewModel.successMessage?.let {
            HudHelper.showSuccessMessage(view, it)

            viewModel.onSuccessMessageShown()
        }
    }
}

@Composable
fun CoinNotFound(coinUid: String, navController: NavController) {
    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = coinUid,
            navigationIcon = {
                HsBackButton(onClick = { navController.popBackStack() })
            }
        )

        ListEmptyView(
            text = stringResource(R.string.CoinPage_CoinNotFound, coinUid),
            icon = R.drawable.ic_not_available
        )

    }
}
