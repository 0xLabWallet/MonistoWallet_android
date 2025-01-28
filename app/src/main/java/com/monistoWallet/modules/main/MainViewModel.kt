package com.monistoWallet.modules.main

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.collectWith
import com.monistoWallet.R
import com.monistoWallet.core.IAccountManager
import com.monistoWallet.core.IBackupManager
import com.monistoWallet.core.ILocalStorage
import com.monistoWallet.core.IRateAppManager
import com.monistoWallet.core.ITermsManager
import com.monistoWallet.core.managers.ActiveAccountState
import com.monistoWallet.core.managers.ReleaseNotesManager
import com.monistoWallet.entities.LaunchPage
import com.monistoWallet.modules.coin.CoinFragment
import com.monistoWallet.modules.main.MainModule.MainNavigation
import com.monistoWallet.modules.market.platform.MarketPlatformFragment
import com.monistoWallet.modules.market.topplatforms.Platform
import com.monistoWallet.modules.nft.collection.NftCollectionFragment
import com.monistoWallet.modules.walletconnect.list.WCListFragment
import com.monistoWallet.modules.walletconnect.version2.WC2Manager
import com.monistoWallet.modules.walletconnect.version2.WC2SessionManager
import com.monistoWallet.core.IPinComponent
import com.monistoWallet.entities.Account
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(
    private val pinComponent: IPinComponent,
    rateAppManager: IRateAppManager,
    private val backupManager: IBackupManager,
    private val termsManager: ITermsManager,
    private val accountManager: IAccountManager,
    private val releaseNotesManager: ReleaseNotesManager,
    private val localStorage: ILocalStorage,
    wc2SessionManager: WC2SessionManager,
    private val wc2Manager: WC2Manager,
    deepLink: Uri?
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private var wc2PendingRequestsCount = 0
    private var cryptoWalletTabEnabled = localStorage.cryptoWalletTabEnabledFlow.value
    private var cardsTabEnabled = localStorage.cardsTabEnabledFlow.value
    private var transactionsEnabled = isTransactionsTabEnabled()
    private var settingsBadge: MainModule.BadgeType? = null
    private val launchPage: LaunchPage
        get() = localStorage.launchPage ?: LaunchPage.Auto

    private var currentMainTab: MainNavigation
        get() = localStorage.mainTab ?: MainNavigation.Balance
        set(value) {
            localStorage.mainTab = value
        }

    private var relaunchBySettingChange: Boolean
        get() = localStorage.relaunchBySettingChange
        set(value) {
            localStorage.relaunchBySettingChange = value
        }

    private val items: List<MainNavigation>
        get() = if (cryptoWalletTabEnabled && cardsTabEnabled) {
            listOf(
                MainNavigation.Balance,
                MainNavigation.Card,
                MainNavigation.Settings,
            )
        } else {
            if (cryptoWalletTabEnabled) {
                listOf(
                    MainNavigation.Balance,
                    MainNavigation.Settings,
                )
            } else {
                listOf(
                    MainNavigation.Card,
                    MainNavigation.Settings,
                )
            }
        }

    private var selectedTabIndex = getTabIndexToOpen(deepLink)
    private var deeplinkPage: DeeplinkPage? = null
    private var mainNavItems = navigationItems()
    private var showRateAppDialog = false
    private var contentHidden = pinComponent.isLocked
    private var showWhatsNew = false
    private var activeWallet = accountManager.activeAccount
    private var wcSupportState: WC2Manager.SupportState? = null
    private var torEnabled = localStorage.torEnabled

    val wallets: List<Account>
        get() = accountManager.accounts.filter { !it.isWatchAccount }

    val watchWallets: List<Account>
        get() = accountManager.accounts.filter { it.isWatchAccount }

    var uiState by mutableStateOf(
        MainModule.UiState(
            selectedTabIndex = selectedTabIndex,
            deeplinkPage = deeplinkPage,
            mainNavItems = mainNavItems,
            showRateAppDialog = showRateAppDialog,
            contentHidden = contentHidden,
            showWhatsNew = showWhatsNew,
            activeWallet = activeWallet,
            wcSupportState = wcSupportState,
            torEnabled = torEnabled
        )
    )
        private set

    init {
        localStorage.cardsTabEnabledFlow.collectWith(viewModelScope) {
            cardsTabEnabled = it
            syncNavigation()
        }
        localStorage.cryptoWalletTabEnabledFlow.collectWith(viewModelScope) {
            cryptoWalletTabEnabled = it
            syncNavigation()
        }

        termsManager.termsAcceptedSignalFlow.collectWith(viewModelScope) {
            updateSettingsBadge()
        }

        wc2SessionManager.pendingRequestCountFlow.collectWith(viewModelScope) {
            wc2PendingRequestsCount = it
            updateSettingsBadge()
        }

        rateAppManager.showRateAppFlow.collectWith(viewModelScope) {
            showRateAppDialog = it
            syncState()
        }

        disposables.add(backupManager.allBackedUpFlowable.subscribe {
            updateSettingsBadge()
        })

        disposables.add(pinComponent.pinSetFlowable.subscribe {
            updateSettingsBadge()
        })

        disposables.add(accountManager.accountsFlowable.subscribe {
            updateTransactionsTabEnabled()
            updateSettingsBadge()
        })

        viewModelScope.launch {
            accountManager.activeAccountStateFlow.collect {
                if (it is ActiveAccountState.ActiveAccount) {
                    updateTransactionsTabEnabled()
                }
            }
        }

        accountManager.activeAccountStateFlow.collectWith(viewModelScope) {
            (it as? ActiveAccountState.ActiveAccount)?.let { state ->
                activeWallet = state.account
                syncState()
            }
        }

        updateSettingsBadge()
        updateTransactionsTabEnabled()
        //showWhatsNew()
    }

    private fun isTransactionsTabEnabled(): Boolean =
        !accountManager.isAccountsEmpty && accountManager.activeAccount?.type !is com.monistoWallet.entities.AccountType.Cex


    override fun onCleared() {
        disposables.clear()
    }

    fun whatsNewShown() {
        showWhatsNew = false
        syncState()
    }

    fun closeRateDialog() {
        showRateAppDialog = false
        syncState()
    }

    fun onSelect(account: Account) {
        accountManager.setActiveAccountId(account.id)
        activeWallet = account
        syncState()
    }

    fun onResume() {
        contentHidden = pinComponent.isLocked
        syncState()
    }

    fun onSelect(mainNavItem: MainNavigation) {
        if (mainNavItem != MainNavigation.Settings) {
            currentMainTab = mainNavItem
        }
        selectedTabIndex = items.indexOf(mainNavItem)
        syncNavigation()
    }

    private fun updateTransactionsTabEnabled() {
        transactionsEnabled = isTransactionsTabEnabled()
        syncNavigation()
    }

    fun wcSupportStateHandled() {
        wcSupportState = null
        syncState()
    }

    private fun syncState() {
        uiState = MainModule.UiState(
            selectedTabIndex = selectedTabIndex,
            deeplinkPage = deeplinkPage,
            mainNavItems = mainNavItems,
            showRateAppDialog = showRateAppDialog,
            contentHidden = contentHidden,
            showWhatsNew = showWhatsNew,
            activeWallet = activeWallet,
            wcSupportState = wcSupportState,
            torEnabled = torEnabled
        )
    }

    private fun navigationItems(): List<MainModule.NavigationViewItem> {
        return items.mapIndexed { index, mainNavItem ->
            getNavItem(mainNavItem, index == selectedTabIndex)
        }
    }

    private fun getNavItem(item: MainNavigation, selected: Boolean) = when (item) {

        MainNavigation.Settings -> {
            MainModule.NavigationViewItem(
                mainNavItem = item,
                selected = selected,
                enabled = true,
                badge = settingsBadge
            )
        }

        MainNavigation.Balance -> {
            MainModule.NavigationViewItem(
                mainNavItem = item,
                selected = selected,
                enabled = true,
            )
        }
        MainNavigation.Card -> {
            MainModule.NavigationViewItem(
                mainNavItem = item,
                selected = selected,
                enabled = true,
            )
        }
    }

    private fun getTabIndexToOpen(deepLink: Uri? = null): Int {
        deepLink?.let {
            val (tab, deeplinkPageData) = getNavigationDataForDeeplink(it)
            deeplinkPage = deeplinkPageData
            currentMainTab = tab
            return items.indexOf(tab)
        }

        val tab = when {
            relaunchBySettingChange -> {
                relaunchBySettingChange = false
                MainNavigation.Settings
            }

            !cryptoWalletTabEnabled -> {
                MainNavigation.Card
            }
            !cardsTabEnabled -> {
                MainNavigation.Balance
            }

            else -> getLaunchTab()
        }

        return items.indexOf(tab)
    }

    private fun getLaunchTab(): MainNavigation = when (launchPage) {
        LaunchPage.Watchlist,
        LaunchPage.Cards -> MainNavigation.Card
        LaunchPage.Balance -> MainNavigation.Balance
        LaunchPage.Auto -> currentMainTab
    }

    private fun getNavigationDataForDeeplink(deepLink: Uri): Pair<MainNavigation, DeeplinkPage?> {
        var tab = currentMainTab
        var deeplinkPage: DeeplinkPage? = null
        val deeplinkString = deepLink.toString()
        when {
            deeplinkString.startsWith("wallet0x:") -> {
                val uid = deepLink.getQueryParameter("uid")
                when {
                    deeplinkString.contains("coin-page") -> {
                        uid?.let {
                            deeplinkPage = DeeplinkPage(R.id.coinFragment, CoinFragment.prepareParams(it, "widget_click"))
                        }
                    }

                    deeplinkString.contains("nft-collection") -> {
                        val blockchainTypeUid = deepLink.getQueryParameter("blockchainTypeUid")
                        if (uid != null && blockchainTypeUid != null) {
                            deeplinkPage = DeeplinkPage(R.id.nftCollectionFragment, NftCollectionFragment.prepareParams(uid, blockchainTypeUid))
                        }
                    }

                    deeplinkString.contains("top-platforms") -> {
                        val title = deepLink.getQueryParameter("title")
                        if (title != null && uid != null) {
                            val platform = Platform(uid, title)
                            deeplinkPage = DeeplinkPage(R.id.marketPlatformFragment, MarketPlatformFragment.prepareParams(platform))
                        }
                    }
                }

            }

            deeplinkString.startsWith("wc:") -> {
                wcSupportState = wc2Manager.getWalletConnectSupportState()
                if (wcSupportState == WC2Manager.SupportState.Supported) {
                    deeplinkPage = DeeplinkPage(R.id.wallet_connect_graph, WCListFragment.prepareParams(deeplinkString))
                    tab = MainNavigation.Settings
                }
            }

            else -> {}
        }
        return Pair(tab, deeplinkPage)
    }

    private fun syncNavigation() {
        mainNavItems = navigationItems()
        if (selectedTabIndex >= mainNavItems.size) {
            selectedTabIndex = mainNavItems.size - 1
        }
        syncState()
    }

    private fun showWhatsNew() {
        viewModelScope.launch {
            if (releaseNotesManager.shouldShowChangeLog()) {
                delay(2000)
                showWhatsNew = true
                syncState()
            }
        }
    }

    private fun updateSettingsBadge() {
        val showDotBadge =
            !(backupManager.allBackedUp && termsManager.allTermsAccepted && pinComponent.isPinSet) || accountManager.hasNonStandardAccount

        settingsBadge = if (wc2PendingRequestsCount > 0) {
            MainModule.BadgeType.BadgeNumber(wc2PendingRequestsCount)
        } else if (showDotBadge) {
            MainModule.BadgeType.BadgeDot
        } else {
            null
        }
        syncNavigation()
    }

    fun deeplinkPageHandled() {
        deeplinkPage = null
        syncState()
    }

}
