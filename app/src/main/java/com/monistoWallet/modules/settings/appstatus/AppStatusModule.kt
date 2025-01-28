package com.monistoWallet.modules.settings.appstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App

object AppStatusModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val viewModel = AppStatusViewModel(
                com.monistoWallet.core.App.systemInfoManager,
                com.monistoWallet.core.App.localStorage,
                com.monistoWallet.core.App.accountManager,
                com.monistoWallet.core.App.walletManager,
                com.monistoWallet.core.App.adapterManager,
                com.monistoWallet.core.App.marketKit,
                com.monistoWallet.core.App.evmBlockchainManager,
                com.monistoWallet.core.App.binanceKitManager,
                com.monistoWallet.core.App.tronKitManager,
                com.monistoWallet.core.App.solanaKitManager,
            )
            return viewModel as T
        }
    }

    sealed class BlockContent {
        data class Header(val title: String) : BlockContent()
        data class Text(val text: String) : BlockContent()
        data class TitleValue(val title: String, val value: String) : BlockContent()
    }

    data class BlockData(val title: String?, val content: List<BlockContent>)

    data class UiState(
        val appStatusAsText: String?,
        val blockViewItems: List<BlockData>,
    )

}
