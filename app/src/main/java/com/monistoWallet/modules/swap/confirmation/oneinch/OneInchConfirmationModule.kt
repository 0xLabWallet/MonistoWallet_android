package com.monistoWallet.modules.swap.confirmation.oneinch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.core.ethereum.CautionViewItemFactory
import com.monistoWallet.core.ethereum.EvmCoinServiceFactory
import com.monistoWallet.modules.evmfee.EvmFeeCellViewModel
import com.monistoWallet.modules.evmfee.IEvmGasPriceService
import com.monistoWallet.modules.evmfee.eip1559.Eip1559GasPriceService
import com.monistoWallet.modules.evmfee.legacy.LegacyGasPriceService
import com.monistoWallet.modules.send.evm.settings.SendEvmNonceService
import com.monistoWallet.modules.send.evm.settings.SendEvmNonceViewModel
import com.monistoWallet.modules.send.evm.settings.SendEvmSettingsService
import com.monistoWallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import com.monistoWallet.modules.swap.SwapMainModule.OneInchSwapParameters
import com.monistoWallet.modules.swap.SwapViewItemHelper
import com.monistoWallet.modules.swap.oneinch.OneInchKitHelper
import com.wallet0x.ethereumkit.core.LegacyGasPriceProvider
import com.wallet0x.ethereumkit.core.eip1559.Eip1559GasPriceProvider
import com.wallet0x.marketkit.models.BlockchainType

object OneInchConfirmationModule {

    class Factory(val blockchainType: BlockchainType, private val oneInchSwapParameters: OneInchSwapParameters) : ViewModelProvider.Factory {

        private val evmKitWrapper by lazy { com.monistoWallet.core.App.evmBlockchainManager.getEvmKitManager(blockchainType).evmKitWrapper!! }
        private val oneInchKitHelper by lazy { OneInchKitHelper(evmKitWrapper.evmKit, com.monistoWallet.core.App.appConfigProvider.oneInchApiKey) }
        private val token by lazy { com.monistoWallet.core.App.evmBlockchainManager.getBaseToken(blockchainType)!! }
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
            OneInchFeeService(oneInchKitHelper, evmKitWrapper.evmKit, gasPriceService, oneInchSwapParameters)
        }
        private val coinServiceFactory by lazy {
            EvmCoinServiceFactory(
                token,
                com.monistoWallet.core.App.marketKit,
                com.monistoWallet.core.App.currencyManager,
                com.monistoWallet.core.App.coinManager
            )
        }
        private val nonceService by lazy { SendEvmNonceService(evmKitWrapper.evmKit) }
        private val settingsService by lazy { SendEvmSettingsService(feeService, nonceService) }
        private val sendService by lazy {
            OneInchSendEvmTransactionService(
                evmKitWrapper,
                feeService,
                settingsService,
                SwapViewItemHelper(com.monistoWallet.core.App.numberFormatter)
            )
        }
        private val cautionViewItemFactory by lazy { CautionViewItemFactory(coinServiceFactory.baseCoinService) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendEvmTransactionViewModel::class.java -> {
                    SendEvmTransactionViewModel(
                        sendService,
                        coinServiceFactory,
                        cautionViewItemFactory,
                        blockchainType = blockchainType,
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
    }

}
