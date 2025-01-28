package com.monistoWallet.modules.swap.confirmation.uniswap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.core.ethereum.CautionViewItemFactory
import com.monistoWallet.core.ethereum.EvmCoinServiceFactory
import com.monistoWallet.modules.evmfee.EvmCommonGasDataService
import com.monistoWallet.modules.evmfee.EvmFeeCellViewModel
import com.monistoWallet.modules.evmfee.EvmFeeService
import com.monistoWallet.modules.evmfee.IEvmGasPriceService
import com.monistoWallet.modules.evmfee.eip1559.Eip1559GasPriceService
import com.monistoWallet.modules.evmfee.legacy.LegacyGasPriceService
import com.monistoWallet.modules.send.evm.SendEvmData
import com.monistoWallet.modules.send.evm.settings.SendEvmNonceService
import com.monistoWallet.modules.send.evm.settings.SendEvmNonceViewModel
import com.monistoWallet.modules.send.evm.settings.SendEvmSettingsService
import com.monistoWallet.modules.sendevmtransaction.SendEvmTransactionService
import com.monistoWallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import com.monistoWallet.modules.swap.SwapMainModule
import com.wallet0x.ethereumkit.core.LegacyGasPriceProvider
import com.wallet0x.ethereumkit.core.eip1559.Eip1559GasPriceProvider
import com.wallet0x.ethereumkit.models.TransactionData

object UniswapConfirmationModule {

    class Factory(
        private val dex: SwapMainModule.Dex,
        private val transactionData: TransactionData,
        private val additionalInfo: SendEvmData.AdditionalInfo?
    ) : ViewModelProvider.Factory {

        private val evmKitWrapper by lazy { com.monistoWallet.core.App.evmBlockchainManager.getEvmKitManager(dex.blockchainType).evmKitWrapper!! }
        private val token by lazy { com.monistoWallet.core.App.evmBlockchainManager.getBaseToken(dex.blockchainType)!! }
        private val gasPriceService: IEvmGasPriceService by lazy {
            val evmKit = evmKitWrapper.evmKit
            if (evmKit.chain.isEIP1559Supported) {
                val gasPriceProvider = Eip1559GasPriceProvider(evmKit)
                Eip1559GasPriceService(gasPriceProvider, evmKit)
            } else {
                val gasPriceProvider = LegacyGasPriceProvider(evmKit)
                LegacyGasPriceService(gasPriceProvider)
            }
        }
        private val feeService by lazy {
            val gasDataService = EvmCommonGasDataService.instance(
                evmKitWrapper.evmKit,
                evmKitWrapper.blockchainType
            )
            EvmFeeService(evmKitWrapper.evmKit, gasPriceService, gasDataService, transactionData)
        }
        private val coinServiceFactory by lazy {
            EvmCoinServiceFactory(
                token,
                com.monistoWallet.core.App.marketKit,
                com.monistoWallet.core.App.currencyManager,
                com.monistoWallet.core.App.coinManager
            )
        }
        private val cautionViewItemFactory by lazy { CautionViewItemFactory(coinServiceFactory.baseCoinService) }
        private val nonceService by lazy { SendEvmNonceService(evmKitWrapper.evmKit) }
        private val settingsService by lazy { SendEvmSettingsService(feeService, nonceService) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendEvmTransactionViewModel::class.java -> {
                    SendEvmTransactionViewModel(
                        getSendService(),
                        coinServiceFactory,
                        cautionViewItemFactory,
                        blockchainType = dex.blockchainType,
                        contactsRepo = App.contactsRepository
                    ) as T
                }
                EvmFeeCellViewModel::class.java -> {
                    EvmFeeCellViewModel(feeService, gasPriceService, coinServiceFactory.baseCoinService) as T
                }
                SendEvmNonceViewModel::class.java -> {
                    SendEvmNonceViewModel(nonceService) as T
                }
                else -> throw IllegalArgumentException()
            }
        }

        private fun getSendService(): SendEvmTransactionService {
            val warnings = when (additionalInfo?.uniswapInfo?.priceImpact?.level) {
                SwapMainModule.PriceImpactLevel.Forbidden -> listOf(SwapMainModule.UniswapWarnings.PriceImpactForbidden(dex.provider.title))
                SwapMainModule.PriceImpactLevel.Warning -> listOf(SwapMainModule.UniswapWarnings.PriceImpactWarning)
                else -> listOf()
            }

            val sendEvmData = SendEvmData(transactionData, additionalInfo, warnings)
            return SendEvmTransactionService(
                sendEvmData,
                evmKitWrapper,
                settingsService,
                App.evmLabelManager
            )
        }
    }

}