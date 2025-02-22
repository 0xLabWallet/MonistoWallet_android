package com.monistoWallet.modules.balance.cex

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.modules.balance.BalanceModule
import com.monistoWallet.modules.balance.cex.asset.CexAssetFragment
import com.monistoWallet.modules.balance.ui.BalanceSortingSelector
import com.monistoWallet.modules.balance.ui.BalanceTitleRow
import com.monistoWallet.modules.balance.ui.TotalBalanceRow
import com.monistoWallet.modules.balance.ui.wallets
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.HSSwipeRefresh
import com.monistoWallet.ui.compose.components.*
import com.monistoWallet.ui.extensions.RotatingCircleProgressView
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.R
import com.monistoWallet.modules.balance.AccountViewItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BalanceForAccountCex(navController: NavController, accountViewItem: AccountViewItem) {
    val viewModel = viewModel<BalanceCexViewModel>(factory = BalanceModule.FactoryCex())
    val uiState = viewModel.uiState
    val totalState = viewModel.totalUiState

    val context = LocalContext.current

    val activeScreen = uiState.isActiveScreen
    if (activeScreen) {
        Scaffold(
            backgroundColor = ComposeAppTheme.colors.tyler,
            topBar = {
                AppBar(
                    title = {
                        BalanceTitleRow(navController, accountViewItem.name)
                    }
                )
            }
        ) { paddingValues ->
            Column(Modifier.padding(paddingValues)) {

                HSSwipeRefresh(
                    refreshing = uiState.isRefreshing,
                    onRefresh = viewModel::onRefresh
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = rememberSaveable(
                            accountViewItem.id,
                            uiState.sortType,
                            saver = LazyListState.Saver
                        ) {
                            LazyListState()
                        }
                    ) {
                        item {
                            TotalBalanceRow(
                                totalState = totalState,
                                onClickTitle = {
                                    viewModel.toggleBalanceVisibility()
                                    HudHelper.vibrate(context)
                                },
                                onClickSubtitle = {
                                    viewModel.toggleTotalType()
                                    HudHelper.vibrate(context)
                                }
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 24.dp, top = 4.dp, end = 24.dp, bottom = 16.dp)
                            ) {
                                ButtonPrimaryYellow(
                                    modifier = Modifier.weight(1f),
                                    title = stringResource(R.string.Balance_Withdraw),
                                    enabled = uiState.withdrawEnabled,
                                    onClick = {},
                                )

                                HSpacer(width = 8.dp)

                                ButtonPrimaryDefault(
                                    modifier = Modifier.weight(1f),
                                    title = stringResource(R.string.Balance_Deposit),
                                    onClick = {
                                        navController.slideFromRight(R.id.depositCexChooseAssetFragment)
                                    }
                                )
                            }
                        }

                        item {
                            Divider(
                                thickness = 1.dp,
                                color = ComposeAppTheme.colors.steel10,
                            )
                        }

                        if (uiState.viewItems.isNotEmpty()) {
                            stickyHeader {
                                HeaderSorting {
                                    BalanceSortingSelector(
                                        sortType = uiState.sortType,
                                        sortTypes = viewModel.sortTypes,
                                        onSelectSortType = viewModel::onSelectSortType
                                    )
                                }
                            }

                            wallets(
                                items = uiState.viewItems,
                                key = { it.assetId },
                            ) { item ->
                                BalanceCardCex(navController, item)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BalanceCardCex(
    navController: NavController,
    viewItem: BalanceCexViewItem
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ComposeAppTheme.colors.lawrence)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    navController.slideFromRight(
                        R.id.cexAssetFragment,
                        CexAssetFragment.prepareParams(viewItem.cexAsset)
                    )
                }
            )
    ) {
        CellMultilineClear(height = 64.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WalletIconCex(viewItem)
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(weight = 1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            body_leah(
                                text = viewItem.coinCode,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (!viewItem.badge.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ComposeAppTheme.colors.jeremy)
                                ) {
                                    Text(
                                        modifier = Modifier.padding(
                                            start = 4.dp,
                                            end = 4.dp,
                                            bottom = 1.dp
                                        ),
                                        text = viewItem.badge,
                                        color = ComposeAppTheme.colors.bran,
                                        style = ComposeAppTheme.typography.microSB,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.width(24.dp))
                        if (viewItem.primaryValue.visible) {
                            Text(
                                text = viewItem.primaryValue.value,
                                color = if (viewItem.primaryValue.dimmed) ComposeAppTheme.colors.grey else ComposeAppTheme.colors.leah,
                                style = ComposeAppTheme.typography.headline2,
                                maxLines = 1,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                        ) {
                            if (viewItem.exchangeValue.visible) {
                                Row {
                                    Text(
                                        text = viewItem.exchangeValue.value,
                                        color = if (viewItem.exchangeValue.dimmed) ComposeAppTheme.colors.grey50 else ComposeAppTheme.colors.grey,
                                        style = ComposeAppTheme.typography.subhead2,
                                        maxLines = 1,
                                    )
                                    Text(
                                        modifier = Modifier.padding(start = 4.dp),
                                        text = RateText(viewItem.diff),
                                        color = RateColor(viewItem.diff),
                                        style = ComposeAppTheme.typography.subhead2,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier.padding(start = 16.dp),
                        ) {
                            if (viewItem.secondaryValue.visible) {
                                Text(
                                    text = viewItem.secondaryValue.value,
                                    color = if (viewItem.secondaryValue.dimmed) ComposeAppTheme.colors.grey50 else ComposeAppTheme.colors.grey,
                                    style = ComposeAppTheme.typography.subhead2,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Composable
fun WalletIconCex(
    viewItem: BalanceCexViewItem
) {
    Box(
        modifier = Modifier
            .width(64.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        viewItem.syncingProgress.progress?.let { progress ->
            AndroidView(
                modifier = Modifier
                    .size(52.dp),
                factory = { context ->
                    RotatingCircleProgressView(context)
                },
                update = { view ->
                    val color = when (viewItem.syncingProgress.dimmed) {
                        true -> R.color.grey_50
                        false -> R.color.grey
                    }
                    view.setProgressColored(progress, view.context.getColor(color))
                }
            )
        }
        if (viewItem.failedIconVisible) {
            val view = LocalView.current
            val clickableModifier = if (viewItem.errorMessage != null) {
                Modifier.clickable(onClick = { HudHelper.showErrorMessage(view, viewItem.errorMessage) })
            } else {
                Modifier
            }

            Image(
                modifier = Modifier
                    .size(32.dp)
                    .then(clickableModifier),
                painter = painterResource(id = R.drawable.ic_attention_24),
                contentDescription = "coin icon",
                colorFilter = ColorFilter.tint(ComposeAppTheme.colors.lucian)
            )
        } else {
            CoinImage(
                iconUrl = coinIconUrl(viewItem),
                placeholder = viewItem.coinIconPlaceholder,
                modifier = Modifier
                    .size(32.dp)
            )
        }
    }
}

fun coinIconUrl(viewItem: BalanceCexViewItem): String? {
    if (viewItem.coinUid != "DEXNET") {
        return viewItem.coinIconUrl
    } else {
        return "https://s2.coinmarketcap.com/static/img/coins/64x64/28538.png"
    }
}