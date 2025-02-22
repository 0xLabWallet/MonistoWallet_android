package com.monistoWallet.modules.settings.appearance

import com.monistoWallet.core.ILocalStorage
import com.monistoWallet.entities.LaunchPage
import com.monistoWallet.ui.compose.Select
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LaunchScreenService(private val localStorage: ILocalStorage) {
    private val screens = LaunchPage.values().asList()

    private val _optionsFlow = MutableStateFlow(
        Select(localStorage.launchPage ?: LaunchPage.Auto, screens)
    )
    val optionsFlow = _optionsFlow.asStateFlow()

    fun setLaunchScreen(launchPage: LaunchPage) {
        localStorage.launchPage = launchPage

        _optionsFlow.update {
            Select(launchPage, screens)
        }
    }
}
