package com.monistoWallet.modules.balance.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.managers.FaqManager
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.core.slideFromBottom
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.modules.balance.BalanceSortType
import com.monistoWallet.modules.balance.BalanceUiState
import com.monistoWallet.modules.balance.BalanceViewItem2
import com.monistoWallet.modules.balance.BalanceViewModel
import com.monistoWallet.modules.balance.HeaderNote
import com.monistoWallet.modules.balance.ReceiveAllowedState
import com.monistoWallet.modules.balance.TotalUIState
import com.monistoWallet.modules.manageaccount.dialogs.BackupRequiredDialog
import com.monistoWallet.modules.rateapp.RateAppModule
import com.monistoWallet.modules.rateapp.RateAppViewModel
import com.monistoWallet.modules.sendtokenselect.SendTokenSelectFragment
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.HSSwipeRefresh
import com.monistoWallet.ui.compose.components.ButtonPrimaryVerticalWithIcon
import com.monistoWallet.ui.compose.components.ButtonSecondaryTransparent
import com.monistoWallet.ui.compose.components.DoubleText
import com.monistoWallet.ui.compose.components.HSpacer
import com.monistoWallet.ui.compose.components.HeaderSorting
import com.monistoWallet.ui.compose.components.HsIconButton
import com.monistoWallet.ui.compose.components.SelectorDialogCompose
import com.monistoWallet.ui.compose.components.SelectorItem
import com.monistoWallet.ui.compose.components.VSpacer
import com.monistoWallet.ui.compose.components.subhead2_leah
import com.monistoWallet.core.helpers.HudHelper

@Composable
fun NoteWarning(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit),
    onClose: (() -> Unit)?
) {
    Note(
        modifier = modifier.clickable(onClick = onClick),
        text = text,
        title = stringResource(id = R.string.AccountRecovery_Note),
        icon = R.drawable.ic_attention_20,
        borderColor = ComposeAppTheme.colors.jacob,
        backgroundColor = ComposeAppTheme.colors.yellow20,
        textColor = ComposeAppTheme.colors.jacob,
        iconColor = ComposeAppTheme.colors.jacob,
        onClose = onClose
    )
}

@Composable
fun NoteError(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit)
) {
    Note(
        modifier = modifier.clickable(onClick = onClick),
        text = text,
        title = stringResource(id = R.string.AccountRecovery_Note),
        icon = R.drawable.ic_attention_20,
        borderColor = ComposeAppTheme.colors.lucian,
        backgroundColor = ComposeAppTheme.colors.red20,
        textColor = ComposeAppTheme.colors.lucian,
        iconColor = ComposeAppTheme.colors.lucian
    )
}

@Composable
fun Note(
    modifier: Modifier = Modifier,
    text: String,
    title: String,
    @DrawableRes icon: Int,
    iconColor: Color,
    borderColor: Color,
    backgroundColor: Color,
    textColor: Color,
    onClose: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconColor
            )
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                color = textColor,
                style = ComposeAppTheme.typography.subhead1
            )
            onClose?.let {
                HsIconButton(
                    modifier = Modifier.size(20.dp),
                    onClick = onClose
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        tint = iconColor,
                        contentDescription = null,
                    )
                }
            }
        }
        if (text.isNotEmpty()) {
            subhead2_leah(text = text)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BalanceItems(
    balanceViewItems: List<BalanceViewItem2>,
    viewModel: BalanceViewModel,
    accountViewItem: com.monistoWallet.modules.balance.AccountViewItem,
    navController: NavController,
    uiState: BalanceUiState,
    totalState: TotalUIState
) {
    val rateAppViewModel = viewModel<RateAppViewModel>(factory = RateAppModule.Factory())
    DisposableEffect(true) {
        rateAppViewModel.onBalancePageActive()
        onDispose {
            rateAppViewModel.onBalancePageInactive()
        }
    }

    val context = LocalContext.current
    var revealedCardId by remember { mutableStateOf<Int?>(null) }

    HSSwipeRefresh(
        refreshing = uiState.isRefreshing,
        onRefresh = viewModel::onRefresh
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
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

            if (!accountViewItem.isWatchAccount) {
                Row(
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ButtonPrimaryVerticalWithIcon(
                        modifier = Modifier
                            .heightIn(90.dp)
                            .weight(1f),
                        title = stringResource(R.string.Balance_Send),
                        icon = R.drawable.icon_send,
                        onClick = {
                            navController.slideFromRight(R.id.sendTokenSelectFragment)
                        }
                    )
                    HSpacer(6.dp)
                    ButtonPrimaryVerticalWithIcon(
                        //                            modifier = Modifier.weight(1f),
                        modifier = Modifier
                            .heightIn(90.dp)
                            .weight(1f),
                        icon = R.drawable.icon_receive,
                        title = stringResource(R.string.Balance_Receive),
                        onClick = {
                            when (val receiveAllowedState = viewModel.getReceiveAllowedState()) {
                                ReceiveAllowedState.Allowed -> {
                                    navController.slideFromRight(R.id.receiveTokenSelectFragment)
                                }

                                is ReceiveAllowedState.BackupRequired -> {
                                    val account = receiveAllowedState.account
                                    val text = Translator.getString(
                                        R.string.Balance_Receive_BackupRequired_Description,
                                        account.name
                                    )
                                    navController.slideFromBottom(
                                        R.id.backupRequiredDialog,
                                        BackupRequiredDialog.prepareParams(account, text)
                                    )
                                }

                                null -> Unit
                            }
                        },
                    )
                    HSpacer(6.dp)
                    ButtonPrimaryVerticalWithIcon(
                        modifier = Modifier
                            .heightIn(90.dp)
                            .weight(1f),
                        icon = R.drawable.ic_transactions_0x,
                        title = stringResource(R.string.Swap),
                        onClick = {
                            navController.slideFromRight(R.id.swapTokenSelectFragment)
                        }
                    )
                }
                VSpacer(12.dp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            HeaderSorting {
                BalanceSortingSelector(
                    sortType = viewModel.sortType,
                    sortTypes = viewModel.sortTypes
                ) {
                    viewModel.sortType = it
                }

                Spacer(modifier = Modifier.weight(1f))

                if (accountViewItem.isWatchAccount) {
                    Image(
                        painter = painterResource(R.drawable.icon_binocule_24),
                        contentDescription = "binoculars icon"
                    )
                    HSpacer(16.dp)
                }

                TransparentButtonSecondaryCircle(
                    icon = R.drawable.ic_manage,
                    contentDescription = stringResource(R.string.ManageCoins_title),
                    tint = ComposeAppTheme.colors.blue0,
                    onClick = {
                        navController.slideFromRight(R.id.manageWalletsFragment)
                    }
                )

                HSpacer(16.dp)
            }

            when (uiState.headerNote) {
                HeaderNote.None -> Unit
                HeaderNote.NonStandardAccount -> {
                    NoteError(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
                        text = stringResource(R.string.AccountRecovery_MigrationRequired),
                        onClick = {
                            FaqManager.showFaqPage(
                                navController,
                                FaqManager.faqPathMigrationRequired
                            )
                        }
                    )
                }

                HeaderNote.NonRecommendedAccount -> {
                    NoteWarning(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
                        text = stringResource(R.string.AccountRecovery_MigrationRecommended),
                        onClick = {
                            FaqManager.showFaqPage(
                                navController,
                                FaqManager.faqPathMigrationRecommended
                            )
                        },
                        onClose = {
                            viewModel.onCloseHeaderNote(HeaderNote.NonRecommendedAccount)
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = rememberSaveable(
                    accountViewItem.id,
                    viewModel.sortType,
                    saver = LazyListState.Saver
                ) {
                    LazyListState()
                }
            ) {
                wallets(
                    items = buildList(balanceViewItems.size) {
                        balanceViewItems.filterTo(this) { true }
                    },
                    key = {
                        it.wallet.hashCode()
                    }
                ) { item ->
                    BalanceCardSwipable(
                        viewItem = item,
                        viewModel = viewModel,
                        navController = navController,
                        revealed = revealedCardId == item.wallet.hashCode(),
                        onReveal = { walletHashCode ->
                            if (revealedCardId != walletHashCode) {
                                revealedCardId = walletHashCode
                            }
                        },
                        onConceal = {
                            revealedCardId = null
                        }
                    )
                }
            }
        }
    }
    uiState.openSend?.let { openSend ->
        navController.slideFromRight(
            R.id.sendTokenSelectFragment,
            SendTokenSelectFragment.prepareParams(
                openSend.blockchainTypes,
                openSend.tokenTypes,
                openSend.address,
                openSend.amount
            )
        )
        viewModel.onSendOpened()
    }
}

@Composable
fun BalanceSortingSelector(
    sortType: BalanceSortType,
    sortTypes: List<BalanceSortType>,
    onSelectSortType: (BalanceSortType) -> Unit
) {
    var showSortTypeSelectorDialog by remember { mutableStateOf(false) }

    ButtonSecondaryTransparent(
        title = stringResource(sortType.getTitleRes()),
        iconRight = R.drawable.ic_down_arrow_20,
        onClick = {
            showSortTypeSelectorDialog = true
        }
    )

    if (showSortTypeSelectorDialog) {
        SelectorDialogCompose(
            title = stringResource(R.string.Balance_Sort_PopupTitle),
            items = sortTypes.map {
                SelectorItem(stringResource(it.getTitleRes()), it == sortType, it)
            },
            onDismissRequest = {
                showSortTypeSelectorDialog = false
            },
            onSelectItem = onSelectSortType
        )
    }
}

@Composable
fun TotalBalanceRow(
    totalState: TotalUIState,
    onClickTitle: () -> Unit,
    onClickSubtitle: () -> Unit
) {
    when (totalState) {
        TotalUIState.Hidden -> {
            DoubleText(
                title = "*****",
                body = "*****",
                dimmed = false,
                onClickTitle = onClickTitle,
                onClickSubtitle = onClickSubtitle
            )
        }

        is TotalUIState.Visible -> {
            DoubleText(
                title = totalState.primaryAmountStr,
                body = totalState.secondaryAmountStr,
                dimmed = totalState.dimmed,
                onClickTitle = onClickTitle,
                onClickSubtitle = onClickSubtitle,
            )
        }
    }
}

@Composable
fun TransparentButtonSecondaryCircle(
    icon: Int,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .background(Color.Transparent, shape = CircleShape)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

fun <T> LazyListScope.wallets(
    items: List<T>,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable (LazyItemScope.(item: T) -> Unit),
) {
    item {
        VSpacer(height = 8.dp)
    }
    items(items = items, key = key, itemContent = {
        Row(modifier = Modifier.padding(bottom = 8.dp)) {
            itemContent(it)
        }
    })
    item {
        VSpacer(height = 10.dp)
    }
}


