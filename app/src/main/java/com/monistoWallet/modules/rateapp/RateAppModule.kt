package com.monistoWallet.modules.rateapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App

object RateAppModule {

    class Factory() : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RateAppViewModel(App.rateAppManager) as T
        }
    }
}
