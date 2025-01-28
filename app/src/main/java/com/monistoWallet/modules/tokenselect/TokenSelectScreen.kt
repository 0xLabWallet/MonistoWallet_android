package com.monistoWallet.modules.tokenselect

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.modules.balance.BalanceViewItem2
import com.monistoWallet.modules.balance.ui.BalanceCardInner
import com.monistoWallet.modules.balance.ui.BalanceCardSubtitleType
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.ListEmptyView
import com.monistoWallet.ui.compose.components.SearchBar
import com.monistoWallet.ui.compose.components.SectionUniversalItem
import com.monistoWallet.ui.compose.components.VSpacer
import com.wallet0x.marketkit.models.FullCoin

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TokenSelectScreen(
    navController: NavController,
    title: String,
    onClickItem: (BalanceViewItem2) -> Unit,
    viewModel: TokenSelectViewModel,
    emptyItemsText: String,
    header: @Composable (() -> Unit)? = null
) {
    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            SearchBar(
                title = title,
                searchHintText = "",
                menuItems = listOf(),
                onClose = { navController.popBackStack() },
                onSearchTextChanged = { text ->
                    viewModel.updateFilter(text)
                }
            )
        }
    ) { paddingValues ->
        val uiState = viewModel.uiState
        if (uiState.noItems) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                header?.invoke()
                ListEmptyView(
                    text = emptyItemsText,
                    icon = R.drawable.ic_empty_wallet
                )
            }
        } else {
            LazyColumn(contentPadding = paddingValues) {
                item {
                    if (header == null) {
                        VSpacer(12.dp)
                    }
                    header?.invoke()
                }
                val _balanceViewItems = uiState.items
                val balanceViewItemsWithoutDuplicates = mutableListOf<BalanceViewItem2>()
                _balanceViewItems.forEach {
                    if (
                        it.wallet.token.coin.marketCapRank == null &&
                        it.wallet.token.coin.coinGeckoId == null
                    ) {
                        //hide old DEXNET token without analytics
                    } else {
                        balanceViewItemsWithoutDuplicates.add(it)
                    }
                }
                val balanceViewItems = balanceViewItemsWithoutDuplicates

                itemsIndexed(balanceViewItems) { index, item ->
                    val lastItem = index == balanceViewItems.size - 1

                    Box(
                        modifier = Modifier.clickable {
                            onClickItem.invoke(item)
                        }
                    ) {
                        SectionUniversalItem(
                            borderTop = true,
                            borderBottom = lastItem
                        ) {
                            BalanceCardInner(
                                viewItem = item,
                                type = BalanceCardSubtitleType.CoinName
                            )
                        }
                    }
                }
                item {
                    VSpacer(32.dp)
                }
            }
        }
    }
}