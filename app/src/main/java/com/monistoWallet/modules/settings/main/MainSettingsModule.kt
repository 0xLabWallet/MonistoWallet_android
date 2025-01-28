package com.monistoWallet.modules.settings.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App

object MainSettingsModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = MainSettingsService(
                com.monistoWallet.core.App.backupManager,
                com.monistoWallet.core.App.languageManager,
                com.monistoWallet.core.App.systemInfoManager,
                com.monistoWallet.core.App.currencyManager,
                com.monistoWallet.core.App.termsManager,
                com.monistoWallet.core.App.pinComponent,
                com.monistoWallet.core.App.wc2SessionManager,
                com.monistoWallet.core.App.wc2Manager,
                com.monistoWallet.core.App.accountManager,
                com.monistoWallet.core.App.appConfigProvider,
            )
            val viewModel = MainSettingsViewModel(
                service,
                com.monistoWallet.core.App.appConfigProvider.companyWebPageLink,
            )

            return viewModel as T
        }
    }

    sealed class CounterType {
        class SessionCounter(val number: Int) : CounterType()
        class PendingRequestCounter(val number: Int) : CounterType()
    }

}
