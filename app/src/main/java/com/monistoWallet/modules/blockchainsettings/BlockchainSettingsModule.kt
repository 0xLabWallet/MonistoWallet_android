package com.monistoWallet.modules.blockchainsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.core.order
import com.monistoWallet.entities.BtcRestoreMode
import com.monistoWallet.entities.EvmSyncSource
import com.wallet0x.marketkit.models.Blockchain
import com.wallet0x.solanakit.models.RpcSource

object BlockchainSettingsModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service =
                BlockchainSettingsService(
                    com.monistoWallet.core.App.btcBlockchainManager,
                    com.monistoWallet.core.App.evmBlockchainManager,
                    com.monistoWallet.core.App.evmSyncSourceManager,
                    com.monistoWallet.core.App.solanaRpcSourceManager
                )
            return BlockchainSettingsViewModel(service) as T
        }
    }

    data class BlockchainViewItem(
        val title: String,
        val subtitle: String,
        val imageUrl: String,
        val blockchainItem: BlockchainItem
    )

    sealed class BlockchainItem {
        abstract val blockchain: Blockchain

        class Btc(
            override val blockchain: Blockchain,
            val restoreMode: BtcRestoreMode
        ) : BlockchainItem()

        class Evm(
            override val blockchain: Blockchain,
            val syncSource: EvmSyncSource
        ) : BlockchainItem()

        class Solana(
            override val blockchain: Blockchain,
            val rpcSource: RpcSource
        ) : BlockchainItem()

        val order
            get() = blockchain.type.order
    }

}
