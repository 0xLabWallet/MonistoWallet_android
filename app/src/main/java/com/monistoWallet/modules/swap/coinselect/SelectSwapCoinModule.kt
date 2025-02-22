package com.monistoWallet.modules.swap.coinselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.swap.SwapMainModule

object SelectSwapCoinModule {

    class Factory(private val dex: SwapMainModule.Dex) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val coinProvider by lazy {
                SwapCoinProvider(
                    dex,
                    com.monistoWallet.core.App.walletManager,
                    com.monistoWallet.core.App.adapterManager,
                    com.monistoWallet.core.App.currencyManager,
                    com.monistoWallet.core.App.marketKit
                )
            }
            return SelectSwapCoinViewModel(coinProvider) as T
        }
    }

}
