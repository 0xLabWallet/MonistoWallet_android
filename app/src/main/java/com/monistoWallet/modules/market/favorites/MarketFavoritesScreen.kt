package com.monistoWallet.modules.market.favorites

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.coin.CoinFragment
import com.monistoWallet.modules.coin.overview.ui.Loading
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.HSSwipeRefresh
import com.monistoWallet.ui.compose.Select
import com.monistoWallet.ui.compose.components.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarketFavoritesScreen(
    navController: NavController,
    viewModel: MarketFavoritesViewModel = viewModel(factory = MarketFavoritesModule.Factory())
) {
    val viewState by viewModel.viewStateLiveData.observeAsState()
    val isRefreshing by viewModel.isRefreshingLiveData.observeAsState(false)
    val marketFavoritesData by viewModel.viewItemLiveData.observeAsState()
    val sortingFieldDialogState by viewModel.sortingFieldSelectorStateLiveData.observeAsState()
    var scrollToTopAfterUpdate by rememberSaveable { mutableStateOf(false) }

    HSSwipeRefresh(
        refreshing = isRefreshing,
        onRefresh = {
            viewModel.refresh()
        }
    ) {
        Crossfade(
            targetState = viewState,
            modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)
        ) { viewState ->
            when (viewState) {
                ViewState.Loading -> {
                    Loading()
                }
                is ViewState.Error -> {
                    ListErrorView(stringResource(R.string.SyncError), viewModel::onErrorClick)
                }
                ViewState.Success -> {
                    marketFavoritesData?.let { data ->
                        if (data.marketItems.isEmpty()) {
                            ListEmptyView(
                                text = stringResource(R.string.Market_Tab_Watchlist_EmptyList),
                                icon = R.drawable.ic_rate_24
                            )
                        } else {
                            CoinList(
                                items = data.marketItems,
                                scrollToTop = scrollToTopAfterUpdate,
                                onAddFavorite = { /*not used */ },
                                onRemoveFavorite = { uid -> viewModel.removeFromFavorites(uid) },
                                onCoinClick = { coinUid ->
                                    val arguments = CoinFragment.prepareParams(coinUid, "market_watchlist")
                                    navController.slideFromRight(R.id.coinFragment, arguments)
                                },
                                preItems = {
                                    stickyHeader {
                                        MarketFavoritesMenu(
                                            data.sortingFieldSelect,
                                            data.marketFieldSelect,
                                            viewModel::onClickSortingField,
                                            viewModel::onSelectMarketField
                                        )
                                    }
                                }
                            )
                            if (scrollToTopAfterUpdate) {
                                scrollToTopAfterUpdate = false
                            }
                        }
                    }
                }
                null -> {}
            }
        }
    }

    when (val option = sortingFieldDialogState) {
        is MarketFavoritesModule.SelectorDialogState.Opened -> {
            AlertGroup(
                R.string.Market_Sort_PopupTitle,
                option.select,
                { selected ->
                    scrollToTopAfterUpdate = true
                    viewModel.onSelectSortingField(selected)
                },
                { viewModel.onSortingFieldDialogDismiss() }
            )
        }
        MarketFavoritesModule.SelectorDialogState.Closed,
        null -> {}
    }
}

@Composable
fun MarketFavoritesMenu(
    sortingFieldSelect: Select<com.monistoWallet.modules.market.SortingField>,
    marketFieldSelect: Select<com.monistoWallet.modules.market.MarketField>,
    onClickSortingField: () -> Unit,
    onSelectMarketField: (com.monistoWallet.modules.market.MarketField) -> Unit
) {

    HeaderSorting(borderTop = true, borderBottom = true) {
        Box(modifier = Modifier.weight(1f)) {
            SortMenu(sortingFieldSelect.selected.title, onClickSortingField)
        }
        ButtonSecondaryToggle(
            modifier = Modifier.padding(end = 16.dp),
            select = marketFieldSelect,
            onSelect = onSelectMarketField
        )
    }
}
