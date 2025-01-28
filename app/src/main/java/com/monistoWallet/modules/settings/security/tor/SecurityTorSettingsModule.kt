package com.monistoWallet.modules.settings.security.tor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App

object SecurityTorSettingsModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SecurityTorSettingsViewModel(com.monistoWallet.core.App.torKitManager, com.monistoWallet.core.App.pinComponent) as T
        }
    }

}

enum class TorStatus {
    Connected,
    Closed,
    Failed,
    Connecting;
}