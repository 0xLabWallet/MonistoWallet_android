package com.monistoWallet.modules.send.tron

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.core.ISendTronAdapter
import com.monistoWallet.core.isNative
import com.monistoWallet.entities.Wallet
import com.monistoWallet.modules.amount.AmountValidator
import com.monistoWallet.modules.amount.SendAmountService
import com.monistoWallet.modules.xrate.XRateService
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.TokenQuery
import com.wallet0x.marketkit.models.TokenType
import java.math.RoundingMode

object SendTronModule {

    class Factory(
        private val wallet: Wallet,
        private val predefinedAddress: String?,
    ) : ViewModelProvider.Factory {
        val adapter = (com.monistoWallet.core.App.adapterManager.getAdapterForWallet(wallet) as? ISendTronAdapter) ?: throw IllegalStateException("SendTronAdapter is null")

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendTronViewModel::class.java -> {
                    val amountValidator = AmountValidator()
                    val coinMaxAllowedDecimals = wallet.token.decimals

                    val amountService = SendAmountService(
                        amountValidator,
                        wallet.token.coin.code,
                        adapter.balanceData.available.setScale(coinMaxAllowedDecimals, RoundingMode.DOWN),
                        wallet.token.type.isNative,
                    )
                    val addressService = SendTronAddressService(adapter, wallet.token, predefinedAddress)
                    val xRateService = XRateService(com.monistoWallet.core.App.marketKit, com.monistoWallet.core.App.currencyManager.baseCurrency)
                    val feeToken = com.monistoWallet.core.App.coinManager.getToken(TokenQuery(BlockchainType.Tron, TokenType.Native)) ?: throw IllegalArgumentException()

                    SendTronViewModel(
                        wallet,
                        wallet.token,
                        feeToken,
                        adapter,
                        xRateService,
                        amountService,
                        addressService,
                        coinMaxAllowedDecimals,
                        com.monistoWallet.core.App.contactsRepository,
                        predefinedAddress == null,
                        com.monistoWallet.core.App.connectivityManager,
                    ) as T
                }

                else -> throw IllegalArgumentException()
            }
        }
    }
}
