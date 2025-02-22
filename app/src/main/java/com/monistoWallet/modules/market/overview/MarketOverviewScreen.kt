package com.monistoWallet.modules.market.overview

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.slideFromBottom
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.coin.overview.ui.Loading
import com.monistoWallet.modules.market.category.MarketCategoryFragment
import com.monistoWallet.modules.market.overview.ui.BoardsView
import com.monistoWallet.modules.market.overview.ui.MetricChartsView
import com.monistoWallet.modules.market.overview.ui.TopPlatformsBoardView
import com.monistoWallet.modules.market.overview.ui.TopSectorsBoardView
import com.monistoWallet.modules.market.platform.MarketPlatformFragment
import com.monistoWallet.modules.market.topcoins.MarketTopCoinsFragment
import com.monistoWallet.modules.market.topplatforms.TopPlatformsFragment
import com.monistoWallet.ui.compose.HSSwipeRefresh
import com.monistoWallet.ui.compose.components.ListErrorView
import com.monistoWallet.ui.compose.components.VSpacer

@Composable
fun MarketOverviewScreen(
    navController: NavController,
    viewModel: MarketOverviewViewModel = viewModel(factory = MarketOverviewModule.Factory())
) {
    val isRefreshing by viewModel.isRefreshingLiveData.observeAsState(false)
    val viewState by viewModel.viewStateLiveData.observeAsState()
    val viewItem by viewModel.viewItem.observeAsState()

    val scrollState = rememberScrollState()

    HSSwipeRefresh(
        refreshing = isRefreshing,
        onRefresh = {
            viewModel.refresh()
        }
    ) {
        Crossfade(viewState) { viewState ->
            when (viewState) {
                ViewState.Loading -> {
                    Loading()
                }
                is ViewState.Error -> {
                    ListErrorView(stringResource(R.string.SyncError), viewModel::onErrorClick)
                }
                ViewState.Success -> {
                    viewItem?.let { viewItem ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            MetricChartsView(viewItem.marketMetrics, navController)
                            BoardsView(
                                boards = viewItem.boards,
                                navController = navController,
                                onClickSeeAll = { listType ->
                                    val (sortingField, topMarket, marketField) = viewModel.getTopCoinsParams(
                                        listType
                                    )
                                    val args = MarketTopCoinsFragment.prepareParams(
                                        sortingField,
                                        topMarket,
                                        marketField
                                    )

                                    navController.slideFromBottom(R.id.marketTopCoinsFragment, args)
                                },
                                onSelectTopMarket = { topMarket, listType ->
                                    viewModel.onSelectTopMarket(topMarket, listType)
                                }
                            )

                            TopPlatformsBoardView(
                                viewItem.topPlatformsBoard,
                                onSelectTimeDuration = { timeDuration ->
                                    viewModel.onSelectTopPlatformsTimeDuration(timeDuration)
                                },
                                onItemClick = {
                                    val args = MarketPlatformFragment.prepareParams(it)
                                    navController.slideFromRight(R.id.marketPlatformFragment, args)
                                },
                                onClickSeeAll = {
                                    val timeDuration = viewModel.topPlatformsTimeDuration
                                    val args = TopPlatformsFragment.prepareParams(timeDuration)

                                    navController.slideFromBottom(R.id.marketTopPlatformsFragment, args)
                                }
                            )

//                            TopSectorsBoardView(
//                                board = viewItem.topSectorsBoard
//                            ) { coinCategory ->
//                                navController.slideFromBottom(
//                                    R.id.marketCategoryFragment,
//                                    bundleOf(MarketCategoryFragment.categoryKey to coinCategory)
//                                )
//                            }

                            VSpacer(height = 32.dp)
                        }
                    }
                }
                null -> {}
            }
        }
    }
}
