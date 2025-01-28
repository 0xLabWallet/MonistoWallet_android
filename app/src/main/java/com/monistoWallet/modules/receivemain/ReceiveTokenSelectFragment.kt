package com.monistoWallet.modules.receivemain

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.core.imagePlaceholder
import com.monistoWallet.core.imageUrl
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.entities.Account
import com.monistoWallet.modules.receive.address.ReceiveAddressFragment
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.CoinImage
import com.monistoWallet.ui.compose.components.RowUniversal
import com.monistoWallet.ui.compose.components.SearchBar
import com.monistoWallet.ui.compose.components.SectionUniversalItem
import com.monistoWallet.ui.compose.components.VSpacer
import com.monistoWallet.ui.compose.components.body_leah
import com.monistoWallet.ui.compose.components.subhead2_grey
import com.monistoWallet.core.helpers.HudHelper
import com.wallet0x.marketkit.models.FullCoin
import kotlinx.coroutines.launch

class ReceiveTokenSelectFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val viewModel = viewModel<ReceiveTokenSelectInitViewModel>()

        val activeAccount = viewModel.getActiveAccount()

        if (activeAccount == null) {
            HudHelper.showErrorMessage(LocalView.current, "No active account")
            navController.popBackStack()
        } else {
            ReceiveTokenSelectScreen(navController, activeAccount)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ReceiveTokenSelectScreen(navController: NavController, activeAccount: Account) {
    val viewModel = viewModel<ReceiveTokenSelectViewModel>(
        factory = ReceiveTokenSelectViewModel.Factory(activeAccount)
    )
    val _fullCoins = viewModel.uiState.fullCoins

    val balanceViewItemsWithoutDuplicates = mutableListOf<FullCoin>()
    _fullCoins.forEach {
        if (
            it.coin.marketCapRank == null &&
            it.coin.coinGeckoId == null
        ) {
            //hide old DEXNET token without analytics
        } else {
            balanceViewItemsWithoutDuplicates.add(it)
        }
    }
    val fullCoins = balanceViewItemsWithoutDuplicates

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            SearchBar(
                title = stringResource(R.string.Balance_Receive),
                searchHintText = "",
                menuItems = listOf(),
                onClose = { navController.popBackStack() },
                onSearchTextChanged = { text ->
                    viewModel.updateFilter(text)
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                VSpacer(12.dp)
            }
            itemsIndexed(fullCoins) { index, fullCoin ->
                val coin = fullCoin.coin
                val lastItem = index == fullCoins.size - 1
                SectionUniversalItem(borderTop = true, borderBottom = lastItem) {
                    ReceiveCoin(
                        coinName = coin.name,
                        coinCode = coin.code,
                        coinIconUrl = coin.imageUrl,
                        coinIconPlaceholder = coin.imagePlaceholder,
                        onClick = {
                            coroutineScope.launch {
                                when (val coinActiveWalletsType = viewModel.getCoinForReceiveType(fullCoin)) {
                                    CoinForReceiveType.MultipleAddressTypes -> {
                                        navController.slideFromRight(
                                            R.id.receiveBchAddressTypeSelectFragment,
                                            BchAddressTypeSelectFragment.prepareParams(coin.uid)
                                        )
                                    }

                                    CoinForReceiveType.MultipleDerivations -> {
                                        navController.slideFromRight(
                                            R.id.receiveDerivationSelectFragment,
                                            DerivationSelectFragment.prepareParams(coin.uid)
                                        )
                                    }

                                    CoinForReceiveType.MultipleBlockchains -> {
                                        navController.slideFromRight(
                                            R.id.receiveNetworkSelectFragment,
                                            NetworkSelectFragment.prepareParams(coin.uid)
                                        )
                                    }

                                    is CoinForReceiveType.Single -> {
                                        navController.slideFromRight(
                                            R.id.receiveFragment,
                                            bundleOf(ReceiveAddressFragment.WALLET_KEY to coinActiveWalletsType.wallet)
                                        )
                                    }

                                    null -> Unit
                                }
                            }
                        }
                    )
                }
            }
            item {
                VSpacer(32.dp)
            }
        }
    }
}

@Composable
fun ReceiveCoin(
    coinName: String,
    coinCode: String,
    coinIconUrl: String,
    coinIconPlaceholder: Int,
    onClick: (() -> Unit)? = null
) {
    fun fetchoinIconUrl(coinName: String): String {
        return coinIconUrl
    }

    RowUniversal(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = onClick
    ) {
        CoinImage(
            iconUrl = fetchoinIconUrl(coinName),
            placeholder = coinIconPlaceholder,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(32.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                body_leah(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                    text = coinCode,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            VSpacer(3.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                subhead2_grey(
                    text = coinName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
