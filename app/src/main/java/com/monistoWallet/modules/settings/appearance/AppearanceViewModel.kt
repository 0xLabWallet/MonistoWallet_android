package com.monistoWallet.modules.settings.appearance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.core.ILocalStorage
import com.monistoWallet.core.managers.BaseTokenManager
import com.monistoWallet.entities.LaunchPage
import com.monistoWallet.modules.balance.BalanceViewType
import com.monistoWallet.modules.balance.BalanceViewTypeManager
import com.monistoWallet.modules.theme.ThemeService
import com.monistoWallet.modules.theme.ThemeType
import com.monistoWallet.ui.compose.Select
import com.monistoWallet.ui.compose.SelectOptional
import com.wallet0x.marketkit.models.Token
import kotlinx.coroutines.launch


class AppearanceViewModel(
    private val launchScreenService: LaunchScreenService,
    private val appIconService: AppIconService,
    private val themeService: ThemeService,
    private val baseTokenManager: BaseTokenManager,
    private val balanceViewTypeManager: BalanceViewTypeManager,
    private val localStorage: ILocalStorage,
) : ViewModel() {
    private var launchScreenOptions = launchScreenService.optionsFlow.value
    private var appIconOptions = appIconService.optionsFlow.value
    private var themeOptions = themeService.optionsFlow.value
    private var baseTokenOptions = buildBaseTokenSelect(baseTokenManager.baseTokenFlow.value)
    private var cardsTabEnabled = localStorage.cardsTabEnabled
    private var cryptoWalletTabEnabled = localStorage.cryptoWalletTabEnabled
    private var balanceViewTypeOptions =
        buildBalanceViewTypeSelect(balanceViewTypeManager.balanceViewTypeFlow.value)

    var uiState by mutableStateOf(
        AppearanceUIState(
            launchScreenOptions = launchScreenOptions,
            appIconOptions = appIconOptions,
            themeOptions = themeOptions,
            baseTokenOptions = baseTokenOptions,
            balanceViewTypeOptions = balanceViewTypeOptions,
            cardsTabEnabled = cardsTabEnabled,
            cryptoWalletTabEnabled = cryptoWalletTabEnabled
        )
    )

    init {
        viewModelScope.launch {
            launchScreenService.optionsFlow
                .collect {
                    handleUpdatedLaunchScreenOptions(it)
                }
        }
        viewModelScope.launch {
            appIconService.optionsFlow
                .collect {
                    handleUpdatedAppIconOptions(it)
                }
        }
        viewModelScope.launch {
            themeService.optionsFlow
                .collect {
                    handleUpdatedThemeOptions(it)
                }
        }
        viewModelScope.launch {
            baseTokenManager.baseTokenFlow
                .collect { baseToken ->
                    handleUpdatedBaseToken(buildBaseTokenSelect(baseToken))
                }
        }
        viewModelScope.launch {
            balanceViewTypeManager.balanceViewTypeFlow
                .collect {
                    handleUpdatedBalanceViewType(buildBalanceViewTypeSelect(it))
                }
        }
    }

    private fun buildBaseTokenSelect(token: Token?): SelectOptional<Token> {
        return SelectOptional(token, baseTokenManager.tokens)
    }

    private fun buildBalanceViewTypeSelect(value: BalanceViewType): Select<BalanceViewType> {
        return Select(value, balanceViewTypeManager.viewTypes)
    }

    private fun handleUpdatedLaunchScreenOptions(launchScreenOptions: Select<LaunchPage>) {
        this.launchScreenOptions = launchScreenOptions
        emitState()
    }

    private fun handleUpdatedAppIconOptions(appIconOptions: Select<AppIcon>) {
        this.appIconOptions = appIconOptions
        emitState()
    }

    private fun handleUpdatedThemeOptions(themeOptions: Select<ThemeType>) {
        this.themeOptions = themeOptions
        emitState()
    }

    private fun handleUpdatedBalanceViewType(balanceViewTypeOptions: Select<BalanceViewType>) {
        this.balanceViewTypeOptions = balanceViewTypeOptions
        emitState()
    }

    private fun handleUpdatedBaseToken(baseTokenOptions: SelectOptional<Token>) {
        this.baseTokenOptions = baseTokenOptions
        emitState()
    }

    private fun emitState() {
        uiState = AppearanceUIState(
            launchScreenOptions = launchScreenOptions,
            appIconOptions = appIconOptions,
            themeOptions = themeOptions,
            baseTokenOptions = baseTokenOptions,
            balanceViewTypeOptions = balanceViewTypeOptions,
            cardsTabEnabled = cardsTabEnabled,
            cryptoWalletTabEnabled = cryptoWalletTabEnabled
        )
    }

    fun onEnterLaunchPage(launchPage: LaunchPage) {
        launchScreenService.setLaunchScreen(launchPage)
    }

    fun onEnterAppIcon(enabledAppIcon: AppIcon) {
        appIconService.setAppIcon(enabledAppIcon)
    }

    fun onEnterTheme(themeType: ThemeType) {
        themeService.setThemeType(themeType)
    }

    fun onEnterBaseToken(token: Token) {
        baseTokenManager.setBaseToken(token)
    }

    fun onEnterBalanceViewType(viewType: BalanceViewType) {
        balanceViewTypeManager.setViewType(viewType)
    }

    fun setOnCardsTabEnabled(enabled: Boolean) {
        if (enabled.not() && (launchScreenOptions.selected == LaunchPage.Cards || launchScreenOptions.selected == LaunchPage.Watchlist)) {
            launchScreenService.setLaunchScreen(LaunchPage.Auto)
        }

        localStorage.cardsTabEnabled = enabled
        cardsTabEnabled = enabled
        emitState()
    }
    fun setOnCryptoWalletTabEnabled(enabled: Boolean) {
        if (enabled.not() && (launchScreenOptions.selected == LaunchPage.Balance || launchScreenOptions.selected == LaunchPage.Watchlist)) {
            launchScreenService.setLaunchScreen(LaunchPage.Auto)
        }

        localStorage.cryptoWalletTabEnabled = enabled
        cryptoWalletTabEnabled = enabled
        emitState()
    }

}

data class AppearanceUIState(
    val launchScreenOptions: Select<LaunchPage>,
    val appIconOptions: Select<AppIcon>,
    val themeOptions: Select<ThemeType>,
    val baseTokenOptions: SelectOptional<Token>,
    val balanceViewTypeOptions: Select<BalanceViewType>,
    val cardsTabEnabled: Boolean,
    val cryptoWalletTabEnabled: Boolean,
)
