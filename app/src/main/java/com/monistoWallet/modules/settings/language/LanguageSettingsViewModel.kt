package com.monistoWallet.modules.settings.language

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.core.ILocalStorage
import com.monistoWallet.core.managers.LanguageManager
import com.monistoWallet.core.helpers.LocaleType

class LanguageSettingsViewModel(
    private val languageManager: LanguageManager,
    private val localStorage: ILocalStorage
) : ViewModel() {

    val languageItems = LocaleType.values().map {
        LanguageViewItem(
            it,
            languageManager.getName(it.tag),
            languageManager.getNativeName(it.tag),
            it.icon,
            currentLocaleTag == it.tag
        )
    }

    private var currentLocaleTag: String
        get() = languageManager.currentLocaleTag
        set(value) {
            languageManager.currentLocaleTag = value
        }

    var closeScreen by mutableStateOf(false)
        private set

    var reloadApp by mutableStateOf(false)
        private set

    fun onSelectLocale(localeType: LocaleType) {
        if (localeType.tag == currentLocaleTag) {
            closeScreen = true
        } else {
            localStorage.relaunchBySettingChange = true
            currentLocaleTag = localeType.tag
            reloadApp = true
        }
    }

}
