package com.monistoWallet.modules.settings.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App

object PersonalSupportModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PersonalSupportViewModel(com.monistoWallet.core.App.marketKit, com.monistoWallet.core.App.localStorage) as T
        }
    }

    data class UiState(
        val contactName: String,
        val showSuccess: Boolean = false,
        val showError: Boolean = false,
        val showSpinner: Boolean = false,
        val buttonEnabled: Boolean = false,
        val showRequestForm: Boolean = false,
    )
}
