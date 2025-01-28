package com.monistoWallet.modules.settings.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App

object AboutModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AboutViewModel(com.monistoWallet.core.App.appConfigProvider, com.monistoWallet.core.App.termsManager, com.monistoWallet.core.App.systemInfoManager) as T
        }
    }
}
