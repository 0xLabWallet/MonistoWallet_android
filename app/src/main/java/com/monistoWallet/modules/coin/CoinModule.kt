package com.monistoWallet.modules.coin

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.R
import com.monistoWallet.core.App

object CoinModule {

    class Factory(private val coinUid: String) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val fullCoin = com.monistoWallet.core.App.marketKit.fullCoins(coinUids = listOf(coinUid)).first()
            val service = CoinService(fullCoin, com.monistoWallet.core.App.marketFavoritesManager)
            return CoinViewModel(service, listOf(service), com.monistoWallet.core.App.localStorage, com.monistoWallet.core.App.subscriptionManager) as T
        }

    }

    enum class Tab(@StringRes val titleResId: Int) {
        Overview(R.string.Coin_Tab_Overview),
//        Details(R.string.Coin_Tab_Details),
        Market(R.string.Coin_Tab_Market),
//        Tweets(R.string.Coin_Tab_Tweets);
    }
}
