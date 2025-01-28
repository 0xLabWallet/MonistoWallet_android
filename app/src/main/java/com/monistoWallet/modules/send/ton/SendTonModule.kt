package com.monistoWallet.modules.send.ton

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.core.ISendTonAdapter
import com.monistoWallet.entities.Wallet
import com.monistoWallet.modules.amount.AmountValidator
import com.monistoWallet.modules.xrate.XRateService
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.TokenQuery
import com.wallet0x.marketkit.models.TokenType

object SendTonModule {
    class Factory(
        private val wallet: Wallet,
        private val predefinedAddress: String?,
    ) : ViewModelProvider.Factory {
        val adapter = (com.monistoWallet.core.App.adapterManager.getAdapterForWallet(wallet) as? ISendTonAdapter) ?: throw IllegalStateException("ISendTonAdapter is null")

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendTonViewModel::class.java -> {
                    val amountValidator = AmountValidator()
                    val coinMaxAllowedDecimals = wallet.token.decimals

                    val amountService = SendTonAmountService(amountValidator, wallet.coin.code, adapter.availableBalance)
                    val addressService = SendTonAddressService(predefinedAddress)
                    val feeService = SendTonFeeService(adapter)
                    val xRateService = XRateService(com.monistoWallet.core.App.marketKit, com.monistoWallet.core.App.currencyManager.baseCurrency)
                    val feeToken = com.monistoWallet.core.App.coinManager.getToken(TokenQuery(BlockchainType.Ton, TokenType.Native)) ?: throw IllegalArgumentException()

                    SendTonViewModel(
                        wallet,
                        wallet.token,
                        feeToken,
                        adapter,
                        xRateService,
                        amountService,
                        addressService,
                        feeService,
                        coinMaxAllowedDecimals,
                        com.monistoWallet.core.App.contactsRepository,
                        predefinedAddress == null
                    ) as T
                }

                else -> throw IllegalArgumentException()
            }
        }
    }

}


