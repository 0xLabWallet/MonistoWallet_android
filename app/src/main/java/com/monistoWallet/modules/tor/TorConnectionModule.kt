package com.monistoWallet.modules.tor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App

object TorConnectionModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TorConnectionViewModel(com.monistoWallet.core.App.torKitManager, com.monistoWallet.core.App.connectivityManager) as T
        }
    }

    data class TorViewState(
        val stateText: Int,
        val showRetryButton: Boolean,
        val torIsActive: Boolean,
        val showNetworkConnectionError: Boolean,
    )

}
