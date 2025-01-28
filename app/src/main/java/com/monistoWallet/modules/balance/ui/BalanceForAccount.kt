package com.monistoWallet.modules.balance.ui

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.monistoWallet.R
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.core.slideFromBottom
import com.monistoWallet.core.utils.ModuleField
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.backupalert.BackupAlert
import com.monistoWallet.modules.balance.AccountViewItem
import com.monistoWallet.modules.balance.BalanceModule
import com.monistoWallet.modules.balance.BalanceViewModel
import com.monistoWallet.modules.contacts.screen.ConfirmationBottomSheet
import com.monistoWallet.modules.manageaccount.dialogs.BackupRequiredDialog
import com.monistoWallet.modules.manageaccounts.ManageAccountsModule
import com.monistoWallet.modules.qrscanner.QRScannerActivity
import com.monistoWallet.modules.swap.settings.Caution
import com.monistoWallet.modules.walletconnect.WCAccountTypeNotSupportedDialog
import com.monistoWallet.modules.walletconnect.list.WalletConnectListViewModel
import com.monistoWallet.modules.walletconnect.version2.WC2Manager
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.MenuItem
import com.monistoWallet.ui.compose.components.title3_leah
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.ui.compose.components.DoubledAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun BalanceForAccount(navController: NavController, accountViewItem: AccountViewItem) {

    val viewModel = viewModel<BalanceViewModel>(factory = BalanceModule.Factory())

    val context = LocalContext.current
    val qrScannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.handleScannedData(result.data?.getStringExtra(ModuleField.SCAN_ADDRESS) ?: "")
            }
        }

    Column {
        DoubledAppBar(
            leftButton = MenuItem(
                title = TranslatableString.ResString(R.string.ManageAccount_SwitchWallet_Title),
                icon = R.drawable.switch_wallet,
                onClick = {
                    navController.navigate(R.id.mainFragment)
                    navController.slideFromBottom(
                        R.id.manageAccountsFragment,
                        ManageAccountsModule.prepareParams(ManageAccountsModule.Mode.Switcher)
                    )
                }
            ),
            centerContent = {
                BalanceTitleRow(navController, accountViewItem.name)
            },
            rightButton = MenuItem(
                title = TranslatableString.ResString(R.string.WalletConnect_NewConnect),
                icon = R.drawable.ic_qr_scan_20,
                onClick = {
                    when (val state = viewModel.getWalletConnectSupportState()) {
                        WC2Manager.SupportState.Supported -> {
                            qrScannerLauncher.launch(QRScannerActivity.getScanQrIntent(context, true))
                        }

                        WC2Manager.SupportState.NotSupportedDueToNoActiveAccount -> {
                            navController.slideFromBottom(R.id.wcErrorNoAccountFragment)
                        }

                        is WC2Manager.SupportState.NotSupportedDueToNonBackedUpAccount -> {
                            val text = Translator.getString(R.string.WalletConnect_Error_NeedBackup)
                            navController.slideFromBottom(
                                R.id.backupRequiredDialog,
                                BackupRequiredDialog.prepareParams(state.account, text)
                            )
                        }

                        is WC2Manager.SupportState.NotSupported -> {
                            navController.slideFromBottom(
                                R.id.wcAccountTypeNotSupportedDialog,
                                WCAccountTypeNotSupportedDialog.prepareParams(state.accountTypeDescription)
                            )
                        }
                    }
                }
            )
        )

        ScreenWallet(navController, accountViewItem)
    }

    Divider(
        thickness = 1.dp,
        color = ComposeAppTheme.colors.steel10,
        modifier = Modifier.padding(0.dp, 300.dp, 0.dp, 0.dp)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScreenWallet(navController: NavController, accountViewItem: AccountViewItem) {

    val viewModel = viewModel<BalanceViewModel>(factory = BalanceModule.Factory())

    val context = LocalContext.current
    val invalidUrlBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    val qrScannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.handleScannedData(
                    result.data?.getStringExtra(ModuleField.SCAN_ADDRESS) ?: ""
                )
        }
    }


    viewModel.uiState.errorMessage?.let { message ->
        val view = LocalView.current
        HudHelper.showErrorMessage(view, text = message)
        viewModel.errorShown()
    }

    when (viewModel.connectionResult) {
        WalletConnectListViewModel.ConnectionResult.Error -> {
            LaunchedEffect(viewModel.connectionResult) {
                coroutineScope.launch {
                    delay(300)
                    invalidUrlBottomSheetState.show()
                }
            }
            viewModel.onHandleRoute()
        }

        else -> Unit
    }

    BackupAlert(navController)
    ModalBottomSheetLayout(
        sheetState = invalidUrlBottomSheetState,
        sheetBackgroundColor = ComposeAppTheme.colors.transparent,
        sheetContent = {
            ConfirmationBottomSheet(
                title = stringResource(R.string.WalletConnect_Title),
                text = stringResource(R.string.WalletConnect_Error_InvalidUrl),
                iconPainter = painterResource(R.drawable.ic_wallet_connect_24),
                iconTint = ColorFilter.tint(ComposeAppTheme.colors.jacob),
                confirmText = stringResource(R.string.Button_TryAgain),
                cautionType = Caution.Type.Warning,
                cancelText = stringResource(R.string.Button_Cancel),
                onConfirm = {
                    coroutineScope.launch {
                        invalidUrlBottomSheetState.hide()
                        qrScannerLauncher.launch(QRScannerActivity.getScanQrIntent(context, true))
                    }
                },
                onClose = {
                    coroutineScope.launch { invalidUrlBottomSheetState.hide() }
                }
            )
        }
    ) {
        Column {
            val uiState = viewModel.uiState

            Crossfade(uiState.viewState, label = "") { viewState ->
                when (viewState) {
                    ViewState.Success -> {
                        val balanceViewItems = uiState.balanceViewItems

                        if (balanceViewItems.isNotEmpty()) {
                            BalanceItems(
                                balanceViewItems,
                                viewModel,
                                accountViewItem,
                                navController,
                                uiState,
                                viewModel.totalUiState
                            )
                        } else {
                            BalanceItemsEmpty(navController)
                        }
                    }

                    ViewState.Loading,
                    is ViewState.Error,
                    null -> {
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceTitleRow(
    navController: NavController,
    title: String
) {
    Row(

        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(
            modifier = Modifier
                .weight(weight = 1f)
        )
        title3_leah(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
        )
        Spacer(modifier = Modifier
            .weight(weight = 1f)
        )
    }
}