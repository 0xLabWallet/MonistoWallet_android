package com.monistoWallet.modules.send.bitcoin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.core.ISendBitcoinAdapter
import com.monistoWallet.core.factories.FeeRateProviderFactory
import com.monistoWallet.entities.Wallet
import com.monistoWallet.modules.amount.AmountValidator
import com.monistoWallet.modules.xrate.XRateService

object SendBitcoinModule {
    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val wallet: Wallet,
        private val predefinedAddress: String?,
    ) : ViewModelProvider.Factory {
        val adapter =
            (com.monistoWallet.core.App.adapterManager.getAdapterForWallet(wallet) as? ISendBitcoinAdapter) ?: throw IllegalStateException("SendBitcoinAdapter is null")

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val provider = FeeRateProviderFactory.provider(wallet.token.blockchainType)!!
            val feeService = SendBitcoinFeeService(adapter)
            val feeRateService = SendBitcoinFeeRateService(provider)
            val amountService = SendBitcoinAmountService(adapter, wallet.coin.code, AmountValidator())
            val addressService = SendBitcoinAddressService(adapter, predefinedAddress)
            val pluginService = SendBitcoinPluginService(com.monistoWallet.core.App.localStorage, wallet.token.blockchainType)
            return SendBitcoinViewModel(
                adapter,
                wallet,
                feeRateService,
                feeService,
                amountService,
                addressService,
                pluginService,
                XRateService(com.monistoWallet.core.App.marketKit, com.monistoWallet.core.App.currencyManager.baseCurrency),
                com.monistoWallet.core.App.btcBlockchainManager,
                com.monistoWallet.core.App.contactsRepository,
                predefinedAddress == null,
            ) as T
        }
    }

}
