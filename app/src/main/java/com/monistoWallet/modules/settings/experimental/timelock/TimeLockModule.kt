package com.monistoWallet.modules.settings.experimental.timelock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App

object TimeLockModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TimeLockViewModel(com.monistoWallet.core.App.localStorage) as T
        }
    }
}
