package com.monistoWallet.modules.basecurrency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.entities.Currency

object BaseCurrencySettingsModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BaseCurrencySettingsViewModel(com.monistoWallet.core.App.currencyManager) as T
        }
    }
}

data class CurrencyViewItem(val currency: Currency, val selected: Boolean) {
    override fun equals(other: Any?): Boolean {
        if (other is CurrencyViewItem) {
            return currency == other.currency
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return currency.hashCode()
    }
}
