package com.monistoWallet.modules.evmnetwork

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.core.parcelable
import com.wallet0x.marketkit.models.Blockchain

object EvmNetworkModule {

    fun args(blockchain: Blockchain): Bundle {
        return bundleOf("blockchain" to blockchain)
    }

    class Factory(arguments: Bundle) : ViewModelProvider.Factory {
        private val blockchain = arguments.parcelable<Blockchain>("blockchain")!!

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EvmNetworkViewModel(blockchain, com.monistoWallet.core.App.evmSyncSourceManager) as T
        }
    }

}
