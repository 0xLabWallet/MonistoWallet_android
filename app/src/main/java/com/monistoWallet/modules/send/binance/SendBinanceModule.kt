package com.monistoWallet.modules.send.binance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.core.ISendBinanceAdapter
import com.monistoWallet.entities.Wallet
import com.monistoWallet.modules.amount.AmountValidator
import com.monistoWallet.modules.amount.SendAmountService
import com.monistoWallet.modules.xrate.XRateService

object SendBinanceModule {

    class Factory(
        private val wallet: Wallet,
        private val predefinedAddress: String?,
    ) : ViewModelProvider.Factory {
        val adapter = (com.monistoWallet.core.App.adapterManager.getAdapterForWallet(wallet) as? ISendBinanceAdapter) ?: throw IllegalStateException("SendBinanceAdapter is null")

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val amountValidator = AmountValidator()
            val amountService = SendAmountService(amountValidator, wallet.coin.code, adapter.availableBalance)
            val addressService = SendBinanceAddressService(adapter, predefinedAddress)
            val feeService = SendBinanceFeeService(adapter, wallet.token, com.monistoWallet.core.App.feeCoinProvider)
            val xRateService = XRateService(com.monistoWallet.core.App.marketKit, com.monistoWallet.core.App.currencyManager.baseCurrency)

            return SendBinanceViewModel(
                wallet,
                adapter,
                amountService,
                addressService,
                feeService,
                xRateService,
                com.monistoWallet.core.App.contactsRepository,
                predefinedAddress == null,
            ) as T
        }

    }

}
