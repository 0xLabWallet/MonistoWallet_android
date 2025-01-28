package com.monistoWallet.modules.nft.collection.assets

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.slideFromBottom
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.coin.overview.ui.Loading
import com.monistoWallet.modules.nft.asset.NftAssetModule
import com.monistoWallet.modules.nft.holdings.NftAssetViewItem
import com.monistoWallet.modules.nft.ui.NftAssetPreview
import com.monistoWallet.ui.compose.HSSwipeRefresh
import com.monistoWallet.ui.compose.OnBottomReached
import com.monistoWallet.ui.compose.components.HSCircularProgressIndicator
import com.monistoWallet.ui.compose.components.ListErrorView
import com.wallet0x.marketkit.models.BlockchainType

@Composable
fun NftCollectionAssetsScreen(navController: NavController, blockchainType: BlockchainType, collectionUid: String) {
    val viewModel = viewModel<NftCollectionAssetsViewModel>(factory = NftCollectionAssetsModule.Factory(blockchainType, collectionUid))

    HSSwipeRefresh(
        refreshing = viewModel.isRefreshing,
        onRefresh = {
            viewModel.refresh()
        }
    ) {
        Crossfade(viewModel.viewState) { viewState ->
            when (viewState) {
                ViewState.Loading -> {
                    Loading()
                }
                is ViewState.Error -> {
                    ListErrorView(stringResource(R.string.SyncError), viewModel::onErrorClick)
                }
                ViewState.Success -> {
                    NftAssets(navController, viewModel.assets, viewModel::onBottomReached, viewModel.loadingMore)
                }
            }
        }
    }
}

@Composable
private fun NftAssets(
    navController: NavController,
    assets: List<NftAssetViewItem>?,
    onBottomReached: () -> Unit, loadingMore: Boolean
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        assets?.chunked(2)?.forEachIndexed { index, assets ->
            item(key = "content-row-$index") {
                Row(
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    assets.forEach { asset ->
                        Box(modifier = Modifier.weight(1f)) {
                            NftAssetPreview(
                                name = asset.name,
                                imageUrl = asset.imageUrl,
                                onSale = asset.onSale,
                                coinPrice = asset.price,
                                currencyPrice = asset.priceInFiat
                            ) {
                                navController.slideFromBottom(
                                    R.id.nftAssetFragment,
                                    NftAssetModule.prepareParams(
                                        asset.collectionUid,
                                        asset.nftUid
                                    )
                                )
                            }
                        }
                    }

                    if (assets.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(26.dp))
        }

        if (loadingMore) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    HSCircularProgressIndicator()
                }
            }
        }
    }

    listState.OnBottomReached(buffer = 6) {
        onBottomReached()
    }
}
