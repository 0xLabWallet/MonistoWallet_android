package com.monistoWallet.modules.watchaddress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.address.AddressHandlerFactory
import com.wallet0x.marketkit.models.BlockchainType

object WatchAddressModule {

    val supportedBlockchainTypes = buildList {
        add(BlockchainType.Ethereum)
        add(BlockchainType.Tron)
        add(BlockchainType.Ton)
        add(BlockchainType.Bitcoin)
        add(BlockchainType.BitcoinCash)
        add(BlockchainType.Litecoin)
        add(BlockchainType.Dash)
        add(BlockchainType.ECash)
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = WatchAddressService(App.accountManager, App.walletActivator, App.accountFactory, App.marketKit, App.evmBlockchainManager)
            val addressHandlerFactory =
                AddressHandlerFactory()
            val addressParserChain = addressHandlerFactory.parserChain(
                blockchainTypes = supportedBlockchainTypes,
                blockchainTypesWithEns = listOf(BlockchainType.Ethereum)
            )
            return WatchAddressViewModel(service, addressParserChain) as T
        }
    }
}
