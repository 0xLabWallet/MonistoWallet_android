package com.monistoWallet.modules.settings.security.passcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App

object SecurityPasscodeSettingsModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SecuritySettingsViewModel(
                com.monistoWallet.core.App.systemInfoManager,
                com.monistoWallet.core.App.pinComponent,
                com.monistoWallet.core.App.balanceHiddenManager,
                com.monistoWallet.core.App.localStorage,
            ) as T
        }
    }

}
