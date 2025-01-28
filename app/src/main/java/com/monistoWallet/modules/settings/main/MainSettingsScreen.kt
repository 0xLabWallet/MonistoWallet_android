package com.monistoWallet.modules.settings.main

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel.Companion.userEmail
import com.monistoWallet.additional_wallet0x.settings.logout.ui.model.LogoutScreenState
import com.monistoWallet.additional_wallet0x.settings.logout.ui.presentation.LogOutDialogDialog
import com.monistoWallet.additional_wallet0x.settings.logout.ui.view_model.LogoutViewModel
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.core.slideFromBottom
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.modules.contacts.ContactsFragment
import com.monistoWallet.modules.contacts.Mode
import com.monistoWallet.modules.manageaccount.dialogs.BackupRequiredDialog
import com.monistoWallet.modules.settings.appearance.AppearanceModule
import com.monistoWallet.modules.settings.appearance.AppearanceViewModel
import com.monistoWallet.modules.settings.main.MainSettingsModule.CounterType
import com.monistoWallet.modules.walletconnect.WCAccountTypeNotSupportedDialog
import com.monistoWallet.modules.walletconnect.version2.WC2Manager
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.BadgeCount
import com.monistoWallet.ui.compose.components.CellSingleLineLawrenceSection
import com.monistoWallet.ui.compose.components.HsSwitch
import com.monistoWallet.ui.compose.components.RowUniversal
import com.monistoWallet.ui.compose.components.VSpacer
import com.monistoWallet.ui.compose.components.body_leah
import com.monistoWallet.ui.compose.components.subhead1_grey
import com.monistoWallet.ui.compose.components.title3_leah
import com.monistoWallet.ui.helpers.LinkHelper
import com.monistoWallet.ui.helpers.TextHelper.copyText
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: MainSettingsViewModel = viewModel(factory = MainSettingsModule.Factory()),
) {

    Box {
        Image(
            painter = painterResource(R.drawable.app_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column{
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp)
            ) {
                title3_leah(
                    text = stringResource(R.string.Account_Title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.height(12.dp))
                SettingSections(viewModel, navController)
            }
        }
    }
}


@Composable

private fun SettingSections(
    viewModel: MainSettingsViewModel,
    navController: NavController,
    vmLogout: LogoutViewModel = koinViewModel(),
    cardMainViewModel: RootAccountViewModel = koinViewModel(),
    ) {
    val appearanceViewModel = viewModel<AppearanceViewModel>(factory = AppearanceModule.Factory())
    val uiState = appearanceViewModel.uiState

//    val aboutViewModel = viewModel<AboutViewModel>()

    val showAlertManageWallet by viewModel.manageWalletShowAlertLiveData.observeAsState(false)
    val showAlertSecurityCenter by viewModel.securityCenterShowAlertLiveData.observeAsState(false)
    val showAlertAboutApp by viewModel.aboutAppShowAlertLiveData.observeAsState(false)
    val wcCounter by viewModel.wcCounterLiveData.observeAsState()
    val baseCurrency by viewModel.baseCurrencyLiveData.observeAsState()
    val language by viewModel.languageLiveData.observeAsState()
    val context = LocalContext.current

    var showLogOutDialog by remember { mutableStateOf(false) }

    VSpacer(20.dp)
    Row(
        Modifier.padding(horizontal = 28.dp)
    ) {
        Text(text = stringResource(R.string.CRYPTO_WALLET), color = Color.White, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        HsSwitch(
            checked = uiState.cryptoWalletTabEnabled,
            onCheckedChange = {
                if (!uiState.cardsTabEnabled && uiState.cryptoWalletTabEnabled) {
                    appearanceViewModel.setOnCardsTabEnabled(true)
                }
                appearanceViewModel.setOnCryptoWalletTabEnabled(it)
            }
        )
    }

    if (uiState.cryptoWalletTabEnabled) {
        VSpacer(32.dp)
        CellSingleLineLawrenceSection(
            listOf(
                {
                    HsSettingCell(
                        R.string.Settings_WalletConnect,
                        R.drawable.ic_wallet_connect_20,
                        value = (wcCounter as? CounterType.SessionCounter)?.number?.toString(),
                        counterBadge = (wcCounter as? CounterType.PendingRequestCounter)?.number?.toString(),
                        onClick = {
                            when (val state = viewModel.getWalletConnectSupportState()) {
                                WC2Manager.SupportState.Supported -> {
                                    navController.slideFromRight(R.id.wallet_connect_graph)
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

                                else -> {}
                            }
                        }
                    )
                },
                {
                    HsSettingCell(
                        R.string.My_Contacts,
                        R.drawable.ic_user_20,
                        onClick = {
                            navController.slideFromRight(R.id.contactsFragment, ContactsFragment.prepareParams(Mode.Full))
                        }
                    )
                },
                {
                    HsSettingCell(
                        R.string.Settings_BaseCurrency,
                        R.drawable.ic_currency,
                        value = baseCurrency?.code,
                        onClick = {
                            navController.slideFromRight(R.id.baseCurrencySettingsFragment)
                        }
                    )
                },
            )
        )

    }
    VSpacer(32.dp)

    Divider(
        thickness = 1.dp,
        color = ComposeAppTheme.colors.steel10,
    )
    VSpacer(32.dp)
    Row(
        Modifier.padding(horizontal = 28.dp)
    ) {
        Text(text = stringResource(R.string.CARD_SETTINGS), color = Color.White, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        HsSwitch(
            checked = uiState.cardsTabEnabled,
            onCheckedChange = {
                if (!uiState.cryptoWalletTabEnabled && uiState.cardsTabEnabled) {
                    appearanceViewModel.setOnCryptoWalletTabEnabled(true)
                }
                appearanceViewModel.setOnCardsTabEnabled(it)
            }
        )
    }
    val view = LocalView.current
    if (uiState.cardsTabEnabled) {
        VSpacer(32.dp)
        CellSingleLineLawrenceSection(
            listOf(
                {
                    HsSettingCell(
                        R.string.Referral,
                        R.drawable.ic_referral,
                        onClick = {
                            if (userEmail != "" && userEmail != "logout user") {
                                navController.slideFromRight(R.id.referralFragment)
                            } else {
                                Toast.makeText(context,
                                    context.getString(R.string.Please_login_to_account), Toast.LENGTH_SHORT).show()
                            }
                        },
                    )
                },
                {
                    HsSettingCell(
                        R.string.Referral_Id,
                        R.drawable.ic_share_20,
                        onClick = {
                            HudHelper.show0xSuccessMessage(view, view.context.getString(R.string.Hud_Text_Copied))
                            copyText("000000")
                        },
                        value = "000000",
                        endIcon = R.drawable.ic_copy_invite_code
                    )
                },
                {
                    HsSettingCell(
                        R.string.Change_email,
                        R.drawable.ic_mail_24,
                        onClick = {
                            if (userEmail != "" && userEmail != "logout user") {
                                navController.slideFromRight(R.id.changeEmailFragment)
                            } else {
                                Toast.makeText(context,
                                    context.getString(R.string.Please_login_to_account), Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                },
                {
                    HsSettingCell(
                        R.string.Change_password,
                        R.drawable.ic_change_password,
                        onClick = {
                            if (userEmail != "" && userEmail != "logout user") {
                                navController.slideFromRight(R.id.changePasswordFragment)
                            } else {
                                Toast.makeText(context,
                                    context.getString(R.string.Please_login_to_account), Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                },
                {
                    HsSettingCell(
                        R.string.Invite_friends,
                        R.drawable.ic_invite_friends,
                        onClick = {

                        }
                    )
                },
                {
                    HsSettingCell(
                        R.string.Log_Out,
                        R.drawable.ic_log_out,
                        onClick = {
                            if (userEmail != "" && userEmail != "logout user") {
                                showLogOutDialog = true
                            } else {
                                Toast.makeText(context,
                                    context.getString(R.string.Please_login_to_account), Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                },
            )
        )
    }

    if (showLogOutDialog) {
        LogOutDialogDialog({
            showLogOutDialog = false
        }, {
            vmLogout.logout()
            showLogOutDialog = false
        })
    }
    var isLoading by remember { mutableStateOf(false) }
    Crossfade(targetState = vmLogout.logoutScreenStateLD, label = "") {
        when (it) {
            is LogoutScreenState.Error -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(view, "Logout Error", it.message)
                    navController.popBackStack()
                }
            }

            is LogoutScreenState.Loading -> {
                LaunchedEffect(Unit) {
                    isLoading = true
                }
            }

            is LogoutScreenState.Result -> {
                isLoading = false
                cardMainViewModel.logout()
                vmLogout.clearStates()
            }

        }
    }

    VSpacer(32.dp)
    Divider(
        thickness = 1.dp,
        color = ComposeAppTheme.colors.steel10,
    )
    VSpacer(32.dp)
    CellSingleLineLawrenceSection(
        listOf(
            {
                HsSettingCell(
                    R.string.Settings_Language,
                    R.drawable.ic_language,
                    value = language,
                    onClick = {
                        navController.slideFromRight(R.id.languageSettingsFragment)
                    }
                )
            },
            {
                HsSettingCell(
                    R.string.Settings_SecurityCenter,
                    R.drawable.ic_security,
                    showAlert = showAlertSecurityCenter,
                    onClick = {
                        navController.slideFromRight(R.id.securitySettingsFragment)
                    }
                )
            },
        )
    )

    VSpacer(58.dp)

    Text(text = stringResource(R.string.SUPPORT_CENTER), color = Color.White, fontSize = 16.sp,
        modifier = Modifier.padding(horizontal = 28.dp)
    )
    VSpacer(11.dp)
    CellSingleLineLawrenceSection(
        listOf {
            HsSettingCell(
                R.string.Telegram,
                R.drawable.ic_telegram_20,
                onClick = { LinkHelper.openLinkInAppBrowser(context, viewModel.appTelegramLink) }
            )
        }
    )

    VSpacer(32.dp)

}

fun getAppAndAndroidVersion(context: Context): String {
    val packageManager = context.packageManager
    val packageName = context.packageName
    var versionName = ""
    var versionCode = 0

    try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        versionName = packageInfo.versionName ?: "N/A"
        versionCode = packageInfo.versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    val sdkInt = Build.VERSION.SDK_INT
    return "BilderPay $versionName ($versionCode)\nAndroid $sdkInt"
}

@Composable
fun HsSettingCell(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    value: String? = null,
    counterBadge: String? = null,
    showAlert: Boolean = false,
    onClick: () -> Unit,
    endIcon: Int = -1
) {
    RowUniversal(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = onClick
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = icon),
            contentDescription = null,
        )
        body_leah(
            text = stringResource(title),
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.weight(1f))

        if (counterBadge != null) {
            BadgeCount(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = counterBadge
            )
        } else if (value != null) {
            subhead1_grey(
                text = value,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        if (showAlert) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.ic_attention_red_20),
                contentDescription = null,
            )
            Spacer(Modifier.width(12.dp))
        }
        Image(
            modifier = Modifier.size(20.dp),
            painter = if (endIcon == -1) painterResource(id = R.drawable.ic_arrow_right) else painterResource(
                id = endIcon
            ),
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun previewSettingsScreen() {
    ComposeAppTheme {
        Column {
            CellSingleLineLawrenceSection(
                listOf({
                    HsSettingCell(
                        R.string.Settings_Faq,
                        R.drawable.ic_faq_20,
                        showAlert = true,
                        onClick = { }
                    )
                }, {
                    HsSettingCell(
                        R.string.Guides_Title,
                        R.drawable.ic_academy_20,
                        onClick = { }
                    )
                })
            )

            Spacer(Modifier.height(32.dp))

            CellSingleLineLawrenceSection(
                listOf {
                    HsSettingCell(
                        R.string.Settings_WalletConnect,
                        R.drawable.ic_wallet_connect_20,
                        counterBadge = "13",
                        onClick = { }
                    )
                }
            )
        }
    }
}
