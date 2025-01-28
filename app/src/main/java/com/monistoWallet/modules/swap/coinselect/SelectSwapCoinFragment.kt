package com.monistoWallet.modules.swap.coinselect

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.*
import com.monistoWallet.modules.swap.SwapMainModule
import com.monistoWallet.modules.swap.SwapMainModule.CoinBalanceItem
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.*
import com.monistoWallet.core.parcelable
import com.monistoWallet.core.setNavigationResult

class SelectSwapCoinFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val dex = arguments?.parcelable<SwapMainModule.Dex>(dexKey)
        val requestId = arguments?.getLong(requestIdKey)
        if (dex == null || requestId == null) {
            navController.popBackStack()
        } else {
            SelectSwapCoinDialogScreen(
                navController = navController,
                dex = dex,
                onClickItem = {
                    closeWithResult(it, requestId, navController)
                }
            )
        }
    }

    private fun closeWithResult(coinBalanceItem: CoinBalanceItem, requestId: Long, navController: NavController) {
        setNavigationResult(
            resultBundleKey, bundleOf(
                requestIdKey to requestId,
                coinBalanceItemResultKey to coinBalanceItem
            )
        )
        Handler(Looper.getMainLooper()).postDelayed({
            navController.popBackStack()
        }, 100)
    }

    companion object {
        const val resultBundleKey = "selectSwapCoinResultKey"
        const val dexKey = "dexKey"
        const val requestIdKey = "requestIdKey"
        const val coinBalanceItemResultKey = "coinBalanceItemResultKey"

        fun prepareParams(requestId: Long, dex: SwapMainModule.Dex) = bundleOf(requestIdKey to requestId, dexKey to dex)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SelectSwapCoinDialogScreen(
    navController: NavController,
    dex: SwapMainModule.Dex,
    onClickItem: (CoinBalanceItem) -> Unit
) {
    val viewModel = viewModel<SelectSwapCoinViewModel>(factory = SelectSwapCoinModule.Factory(dex))
    val coinItems = viewModel.coinItems

    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        SearchBar(
            title = stringResource(R.string.Select_Coins),
            searchHintText = stringResource(R.string.ManageCoins_Search),
            onClose = { navController.popBackStack() },
            onSearchTextChanged = {
                viewModel.onEnterQuery(it)
            }
        )

        LazyColumn {
            items(coinItems) { coinItem ->
                SectionUniversalItem(borderTop = true) {
                    RowUniversal(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = {
                            onClickItem.invoke(coinItem)
                        }
                    ) {
                        CoinImage(
                            iconUrl = coinIconUrl(coinItem),
                            placeholder = coinItem.token.iconPlaceholder,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        MultitextM1(
                            title = { B2(text = coinItem.token.coin.name) },
                            subtitle = { D1(text = coinItem.token.coin.code) }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        MultitextM1(
                            title = {
                                coinItem.balance?.let {
                                    com.monistoWallet.core.App.numberFormatter.formatCoinFull(it, coinItem.token.coin.code, 8)
                                }?.let {
                                    B2(text = it)
                                }
                            },
                            subtitle = {
                                coinItem.fiatBalanceValue?.let { fiatBalanceValue ->
                                    com.monistoWallet.core.App.numberFormatter.formatFiatFull(
                                        fiatBalanceValue.value,
                                        fiatBalanceValue.currency.symbol
                                    )
                                }?.let {
                                    D1(
                                        modifier = Modifier.align(Alignment.End),
                                        text = it
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

fun coinIconUrl( coinItem: SwapMainModule.CoinBalanceItem): String {
    if (coinItem.token.coin.code != "DEXNET") {
        return coinItem.token.coin.imageUrl
    } else {
        return "https://s2.coinmarketcap.com/static/img/coins/64x64/28538.png"
    }
}