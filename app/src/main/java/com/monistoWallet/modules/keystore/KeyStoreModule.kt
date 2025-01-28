package com.monistoWallet.modules.keystore

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import kotlinx.parcelize.Parcelize

object KeyStoreModule {
    class Factory(private val mode: ModeType) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return KeyStoreViewModel(com.monistoWallet.core.App.keyStoreManager, mode) as T
        }
    }

    @Parcelize
    enum class ModeType : Parcelable {
        NoSystemLock,
        InvalidKey,
        UserAuthentication
    }
}