package com.monistoWallet.modules.main

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.main.ui.presentation.CardScreen
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.core.slideFromBottom
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.modules.balance.ui.BalanceScreen
import com.monistoWallet.modules.intro.canShowCardScreen
import com.monistoWallet.modules.main.MainModule.MainNavigation
import com.monistoWallet.modules.manageaccount.dialogs.BackupRequiredDialog
import com.monistoWallet.modules.rooteddevice.RootedDeviceModule
import com.monistoWallet.modules.rooteddevice.RootedDeviceScreen
import com.monistoWallet.modules.rooteddevice.RootedDeviceViewModel
import com.monistoWallet.modules.settings.main.SettingsScreen
import com.monistoWallet.modules.tor.TorStatusView
import com.monistoWallet.modules.transactions.TransactionsModule
import com.monistoWallet.modules.transactions.TransactionsViewModel
import com.monistoWallet.modules.walletconnect.WCAccountTypeNotSupportedDialog
import com.monistoWallet.modules.walletconnect.version2.WC2Manager.SupportState
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.DisposableLifecycleCallbacks
import com.monistoWallet.ui.compose.components.HsBottomNavigation
import com.monistoWallet.ui.compose.components.HsBottomNavigationItem
import com.monistoWallet.ui.extensions.WalletSwitchBottomSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainFragment : BaseComposeFragment() {

    private val transactionsViewModel by navGraphViewModels<TransactionsViewModel>(R.id.mainFragment) { TransactionsModule.Factory() }
    private var intentUri: Uri? = null

    @Composable
    override fun GetContent(navController: NavController) {
        MainScreenWithRootedDeviceCheck(
            transactionsViewModel = transactionsViewModel,
            deepLink = intentUri,
            navController = navController,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intentUri = activity?.intent?.data
        activity?.intent?.data = null

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().moveTaskToBack(true)
                }
            })
    }

}

@Composable
fun MainScreenWithRootedDeviceCheck(
    transactionsViewModel: TransactionsViewModel,
    deepLink: Uri?,
    navController: NavController,
    rootedDeviceViewModel: RootedDeviceViewModel = viewModel(factory = RootedDeviceModule.Factory())
) {
    if (rootedDeviceViewModel.showRootedDeviceWarning) {
        RootedDeviceScreen { rootedDeviceViewModel.ignoreRootedDeviceWarning() }
    } else {
        MainScreen(transactionsViewModel, deepLink, navController)
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun MainScreen(
    transactionsViewModel: TransactionsViewModel,
    deepLink: Uri?,
    fragmentNavController: NavController,
    viewModel: MainViewModel = viewModel(factory = MainModule.Factory(deepLink)),
) {val uiState = viewModel.uiState
    var selectedPage by remember { mutableStateOf(if (canShowCardScreen) 1 else uiState.selectedTabIndex) }
    canShowCardScreen = false

    val pagerState = rememberPagerState(initialPage = selectedPage) { uiState.mainNavItems.size }
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetBackgroundColor = ComposeAppTheme.colors.transparent,
        sheetContent = {
            WalletSwitchBottomSheet(
                wallets = viewModel.wallets,
                watchingAddresses = viewModel.watchWallets,
                selectedAccount = uiState.activeWallet,
                onSelectListener = {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                        viewModel.onSelect(it)
                    }
                },
                onCancelClick = {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }
            )
        },
    ) {
        Box(Modifier.fillMaxSize()) {
            Scaffold(
                backgroundColor = Color.Black,
                bottomBar = {
                    Column {
                        if (uiState.torEnabled) {
                            TorStatusView()
                        }
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.5.dp),
                            color = colorResource(id = R.color.grey_2)
                        )
                        HsBottomNavigation(
                            backgroundColor = Color(0xFF000000),
                            elevation = 10.dp
                        ) {
                            uiState.mainNavItems.forEachIndexed { index, item ->
                                HsBottomNavigationItem(
                                    icon = {
                                        BadgedIcon(item.badge) {
                                            Icon(
                                                painter = painterResource(item.mainNavItem.iconRes),
                                                contentDescription = stringResource(item.mainNavItem.titleRes)
                                            )
                                        }
                                    },
                                    selected = index == selectedPage,
                                    enabled = item.enabled,
                                    selectedContentColor = ComposeAppTheme.colors.jacob,
                                    unselectedContentColor = if (item.enabled) ComposeAppTheme.colors.grey else ComposeAppTheme.colors.grey50,
                                    onClick = {
                                        coroutineScope.launch {
                                            selectedPage = index
                                            pagerState.scrollToPage(index) // Синхронизация с Pager
                                            viewModel.onSelect(item.mainNavItem)
                                        }
                                    },
                                    onLongClick = {}
                                )
                            }
                        }
                    }
                }
            ) {
                Image(
                    painter = painterResource(R.drawable.app_bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                BackHandler(enabled = modalBottomSheetState.isVisible) {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }
                Column(modifier = Modifier.padding(it)) {
                    LaunchedEffect(key1 = selectedPage) {
                        pagerState.scrollToPage(selectedPage)
                    }

                    HorizontalPager(
                        modifier = Modifier.weight(1f),
                        state = pagerState,
                        userScrollEnabled = false,
                        verticalAlignment = Alignment.Top
                    ) { page ->
                        when (uiState.mainNavItems[page].mainNavItem) {
                            MainNavigation.Balance -> BalanceScreen(fragmentNavController)
                            MainNavigation.Card -> CardScreen()
                            MainNavigation.Settings -> SettingsScreen(fragmentNavController)
                        }
                    }
                }
            }
            HideContentBox(uiState.contentHidden)
        }
    }

    if (uiState.showWhatsNew) {
//        LaunchedEffect(Unit) {
//            fragmentNavController.slideFromBottom(
//                R.id.releaseNotesFragment,
//                bundleOf(ReleaseNotesFragment.showAsClosablePopupKey to true)
//            )
//            viewModel.whatsNewShown()
//        }
    }

    if (uiState.showRateAppDialog) {
        val context = LocalContext.current
//        RateApp(
//            onRateClick = {
//                RateAppManager.openPlayMarket(context)
//                viewModel.closeRateDialog()
//            },
//            onCancelClick = { viewModel.closeRateDialog() }
//        )
    }

    if (uiState.wcSupportState != null) {
        when (val wcSupportState = uiState.wcSupportState) {
            SupportState.NotSupportedDueToNoActiveAccount -> {
                fragmentNavController.slideFromBottom(R.id.wcErrorNoAccountFragment)
            }

            is SupportState.NotSupportedDueToNonBackedUpAccount -> {
                val text = stringResource(R.string.WalletConnect_Error_NeedBackup)
                fragmentNavController.slideFromBottom(
                    R.id.backupRequiredDialog,
                    BackupRequiredDialog.prepareParams(wcSupportState.account, text)
                )
            }

            is SupportState.NotSupported -> {
                fragmentNavController.slideFromBottom(
                    R.id.wcAccountTypeNotSupportedDialog,
                    WCAccountTypeNotSupportedDialog.prepareParams(wcSupportState.accountTypeDescription)
                )
            }

            else -> {}
        }
        viewModel.wcSupportStateHandled()
    }

    uiState.deeplinkPage?.let { deepLinkPage ->
        LaunchedEffect(Unit) {
            delay(500)
            fragmentNavController.slideFromRight(
                deepLinkPage.navigationId,
                deepLinkPage.bundle
            )
            viewModel.deeplinkPageHandled()
        }
    }

    DisposableLifecycleCallbacks(
        onResume = viewModel::onResume,
    )
}

@Composable
private fun HideContentBox(contentHidden: Boolean) {
    val backgroundModifier = if (contentHidden) {
        Modifier.background(ComposeAppTheme.colors.tyler)
    } else {
        Modifier
    }
    Box(
        Modifier
            .fillMaxSize()
            .then(backgroundModifier))
}

@Composable
private fun BadgedIcon(
    badge: MainModule.BadgeType?,
    icon: @Composable BoxScope.() -> Unit,
) {
    when (badge) {
        is MainModule.BadgeType.BadgeNumber ->
            BadgedBox(
                badge = {
                    Badge(
                        backgroundColor = ComposeAppTheme.colors.lucian
                    ) {
                        Text(
                            text = badge.number.toString(),
                            style = ComposeAppTheme.typography.micro,
                            color = ComposeAppTheme.colors.white,
                        )
                    }
                },
                content = icon
            )

        MainModule.BadgeType.BadgeDot ->
            BadgedBox(
                badge = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                ComposeAppTheme.colors.lucian,
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) { }
                },
                content = icon
            )

        else -> {
            Box {
                icon()
            }
        }
    }
}
