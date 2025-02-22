package com.monistoWallet.modules.balance.ui

import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.slideFromBottom
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.modules.balance.BalanceViewItem2
import com.monistoWallet.modules.balance.BalanceViewModel
import com.monistoWallet.modules.balance.token.TokenBalanceFragment
import com.monistoWallet.modules.syncerror.SyncErrorDialog
import com.monistoWallet.modules.walletconnect.list.ui.DraggableCardSimple
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.CellMultilineClear
import com.monistoWallet.ui.compose.components.CoinImage
import com.monistoWallet.ui.compose.components.HsIconButton
import com.monistoWallet.ui.compose.components.RateColor
import com.monistoWallet.ui.compose.components.RateText
import com.monistoWallet.ui.compose.components.body_leah
import com.monistoWallet.ui.compose.components.subhead2_grey
import com.monistoWallet.ui.extensions.RotatingCircleProgressView
import com.monistoWallet.core.helpers.HudHelper

@Composable
fun BalanceCardSwipable(
    viewItem: BalanceViewItem2,
    viewModel: BalanceViewModel,
    navController: NavController,
    revealed: Boolean,
    onReveal: (Int) -> Unit,
    onConceal: () -> Unit,
) {

    var canShowRemove by remember { mutableStateOf(false)}
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        HsIconButton(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .width(88.dp),
            onClick = { viewModel.disable(viewItem) },
            content = {
                if (canShowRemove) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_circle_minus_24),
                        tint = Color.Gray,
                        contentDescription = "delete",
                    )
                }
            }
        )

        DraggableCardSimple(
            key = viewItem.wallet,
            isRevealed = revealed,
            cardOffset = 72f,
            onReveal = {
                canShowRemove = true
                onReveal(viewItem.wallet.hashCode())
            },
            onConceal = {
                onConceal.invoke()
                canShowRemove = false
            },
            content = {
                BalanceCard(viewItem, viewModel, navController)
            }
        )
    }
}


@Composable
fun BalanceCard(
    viewItem: BalanceViewItem2,
    viewModel: BalanceViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x1A252933))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                navController.slideFromRight(
                    R.id.tokenBalanceFragment,
                    TokenBalanceFragment.prepareParams(viewItem.wallet)
                )
            }
    ) {
        val view = LocalView.current

        BalanceCardInner(
            viewItem = viewItem,
            type = BalanceCardSubtitleType.Rate
        ) {
            onSyncErrorClicked(viewItem, viewModel, navController, view)
        }
    }
}

enum class BalanceCardSubtitleType {
    Rate, CoinName
}

@Composable
fun BalanceCardInner(
    viewItem: BalanceViewItem2,
    type: BalanceCardSubtitleType,
    onClickSyncError: (() -> Unit)? = null
) {
    CellMultilineClear(height = 64.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            WalletIcon(viewItem, onClickSyncError)
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
                    Text(
                        text = if (viewItem.primaryValue.visible) viewItem.primaryValue.value else "*****",
                        color = if (viewItem.primaryValue.dimmed) ComposeAppTheme.colors.grey else ComposeAppTheme.colors.leah,
                        style = ComposeAppTheme.typography.headline2,
                        maxLines = 1,
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        if (viewItem.syncingTextValue != null) {
                            subhead2_grey(
                                text = viewItem.syncingTextValue,
                                maxLines = 1,
                            )
                        } else {
                            when (type) {
                                BalanceCardSubtitleType.Rate -> {
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

                                BalanceCardSubtitleType.CoinName -> {
                                    subhead2_grey(text = viewItem.coinTitle)
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.padding(start = 16.dp),
                    ) {
                        if (viewItem.syncedUntilTextValue != null) {
                            subhead2_grey(
                                text = viewItem.syncedUntilTextValue,
                                maxLines = 1,
                            )
                        } else {
                            Text(
                                text = if (viewItem.secondaryValue.visible) viewItem.secondaryValue.value else "*****",
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

@Composable
private fun WalletIcon(
    viewItem: BalanceViewItem2,
    onClickSyncError: (() -> Unit)?
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
            val clickableModifier = if (onClickSyncError != null) {
                Modifier.clickable(onClick = onClickSyncError)
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
                    .clip(RoundedCornerShape(16.dp))
            )
        }
    }
}

fun coinIconUrl(viewItem: BalanceViewItem2): String {
    if (viewItem.coinCode != "DEXNET" && viewItem.coinCode != "DNC") {
        return viewItem.coinIconUrl
    } else {
        return "https://s2.coinmarketcap.com/static/img/coins/64x64/28538.png"
    }
}

private fun onSyncErrorClicked(viewItem: BalanceViewItem2, viewModel: BalanceViewModel, navController: NavController, view: View) {
    when (val syncErrorDetails = viewModel.getSyncErrorDetails(viewItem)) {
        is BalanceViewModel.SyncError.Dialog -> {
            val wallet = syncErrorDetails.wallet
            val errorMessage = syncErrorDetails.errorMessage

            navController.slideFromBottom(
                R.id.syncErrorDialog,
                SyncErrorDialog.prepareParams(wallet, errorMessage)
            )
        }
        is BalanceViewModel.SyncError.NetworkNotAvailable -> {
            HudHelper.showErrorMessage(view, R.string.Hud_Text_NoInternet)
        }
    }
}
