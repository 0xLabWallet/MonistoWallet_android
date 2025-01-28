package com.monistoWallet.modules.syncerror

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.entities.Wallet
import com.wallet0x.marketkit.models.Blockchain

object SyncErrorModule {

    class Factory(private val wallet: Wallet) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = SyncErrorService(
                wallet,
                com.monistoWallet.core.App.adapterManager,
                com.monistoWallet.core.App.appConfigProvider.reportEmail,
                com.monistoWallet.core.App.btcBlockchainManager,
                com.monistoWallet.core.App.evmBlockchainManager
            )
            return SyncErrorViewModel(service) as T
        }
    }

    data class BlockchainWrapper(
        val blockchain: Blockchain,
        val type: Type
    ) {
        enum class Type {
            Bitcoin, Evm
        }
    }
}
