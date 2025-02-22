package com.monistoWallet.modules.btcblockchainsettings

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.core.parcelable
import com.wallet0x.marketkit.models.Blockchain

object BtcBlockchainSettingsModule {

    fun args(blockchain: Blockchain): Bundle {
        return bundleOf("blockchain" to blockchain)
    }

    class Factory(arguments: Bundle) : ViewModelProvider.Factory {
        private val blockchain = arguments.parcelable<Blockchain>("blockchain")!!

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            val service = BtcBlockchainSettingsService(
                blockchain,
                com.monistoWallet.core.App.btcBlockchainManager
            )

            return BtcBlockchainSettingsViewModel(service) as T
        }
    }

    data class ViewItem(
        val id: String,
        val title: String,
        val subtitle: String,
        val selected: Boolean,
        val icon: BlockchainSettingsIcon
    )

    sealed class BlockchainSettingsIcon {
        data class ApiIcon(val resId: Int): BlockchainSettingsIcon()
        data class BlockchainIcon(val url: String): BlockchainSettingsIcon()
    }
}