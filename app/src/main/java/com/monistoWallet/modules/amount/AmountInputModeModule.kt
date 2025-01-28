package com.monistoWallet.modules.amount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.xrate.XRateService

object AmountInputModeModule {

    class Factory(private val coinUid: String) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            val xRateService = XRateService(com.monistoWallet.core.App.marketKit, com.monistoWallet.core.App.currencyManager.baseCurrency)

            return AmountInputModeViewModel(com.monistoWallet.core.App.localStorage, xRateService, coinUid) as T
        }
    }
}