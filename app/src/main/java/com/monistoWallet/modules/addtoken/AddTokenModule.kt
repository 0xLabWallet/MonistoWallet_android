package com.monistoWallet.modules.addtoken

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.wallet0x.marketkit.models.Token
import com.wallet0x.marketkit.models.TokenQuery

object AddTokenModule {
    class Factory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = AddTokenService(com.monistoWallet.core.App.coinManager, com.monistoWallet.core.App.walletManager, com.monistoWallet.core.App.accountManager, com.monistoWallet.core.App.marketKit)
            return AddTokenViewModel(service) as T
        }
    }

    interface IAddTokenBlockchainService {
        fun isValid(reference: String): Boolean
        fun tokenQuery(reference: String): TokenQuery
        suspend fun token(reference: String): Token
    }

}
