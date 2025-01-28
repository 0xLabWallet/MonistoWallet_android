package com.monistoWallet.modules.walletconnect.request.sendtransaction

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
import com.monistoWallet.modules.send.evm.SendEvmData.AdditionalInfo
import com.monistoWallet.modules.send.evm.SendEvmData.WalletConnectInfo
import com.monistoWallet.modules.send.evm.settings.SendEvmNonceService
import com.monistoWallet.modules.send.evm.settings.SendEvmNonceViewModel
import com.monistoWallet.modules.send.evm.settings.SendEvmSettingsService
import com.monistoWallet.modules.sendevmtransaction.SendEvmTransactionService
import com.monistoWallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import com.monistoWallet.modules.walletconnect.request.WCRequestChain
import com.monistoWallet.modules.walletconnect.request.sendtransaction.v2.WC2SendEthereumTransactionRequestService
import com.monistoWallet.modules.walletconnect.version2.WC2SessionManager
import com.wallet0x.ethereumkit.core.EthereumKit
import com.wallet0x.ethereumkit.core.LegacyGasPriceProvider
import com.wallet0x.ethereumkit.core.eip1559.Eip1559GasPriceProvider
import com.wallet0x.ethereumkit.models.Address
import com.wallet0x.ethereumkit.models.Chain
import com.wallet0x.ethereumkit.models.GasPrice
import com.wallet0x.ethereumkit.models.TransactionData
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.Token
import java.math.BigInteger

object WCRequestModule {

    class FactoryV2(private val requestData: WC2SessionManager.RequestData) : ViewModelProvider.Factory {
        private val service by lazy {
            WC2SendEthereumTransactionRequestService(requestData, com.monistoWallet.core.App.wc2SessionManager)
        }
        private val blockchainType = when (service.evmKitWrapper.evmKit.chain) {
            Chain.BinanceSmartChain -> BlockchainType.BinanceSmartChain
            Chain.Dexnet -> BlockchainType.Dexnet
            Chain.Polygon -> BlockchainType.Polygon
            Chain.Avalanche -> BlockchainType.Avalanche
            Chain.Optimism -> BlockchainType.Optimism
            Chain.ArbitrumOne -> BlockchainType.ArbitrumOne
            Chain.Gnosis -> BlockchainType.Gnosis
            Chain.Fantom -> BlockchainType.Fantom
            else -> BlockchainType.Ethereum
        }
        private val token by lazy {
            getToken(blockchainType)
        }
        private val transaction = service.transactionRequest.transaction
        private val transactionData =
            TransactionData(transaction.to, transaction.value, transaction.data)

        private val gasPrice by lazy { getGasPrice(transaction) }

        private val gasPriceService by lazy {
            getGasPriceService(gasPrice, service.evmKitWrapper.evmKit)
        }

        private val coinServiceFactory by lazy {
            EvmCoinServiceFactory(
                token,
                com.monistoWallet.core.App.marketKit,
                com.monistoWallet.core.App.currencyManager,
                com.monistoWallet.core.App.coinManager
            )
        }
        private val feeService by lazy {
            val evmKitWrapper = service.evmKitWrapper
            val gasDataService = EvmCommonGasDataService.instance(
                evmKitWrapper.evmKit,
                evmKitWrapper.blockchainType
            )
            EvmFeeService(evmKitWrapper.evmKit, gasPriceService, gasDataService, transactionData)
        }
        private val cautionViewItemFactory by lazy { CautionViewItemFactory(coinServiceFactory.baseCoinService) }
        private val additionalInfo = AdditionalInfo.WalletConnectRequest(WalletConnectInfo(service.transactionRequest.dAppName, service.chain))
        private val nonceService by lazy { SendEvmNonceService(service.evmKitWrapper.evmKit, transaction.nonce) }
        private val settingsService by lazy { SendEvmSettingsService(feeService, nonceService) }

        private val sendService by lazy {
            SendEvmTransactionService(
                SendEvmData(transactionData, additionalInfo),
                service.evmKitWrapper,
                settingsService,
                App.evmLabelManager
            )
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                WCSendEthereumTransactionRequestViewModel::class.java -> {
                    WCSendEthereumTransactionRequestViewModel(service) as T
                }
                EvmFeeCellViewModel::class.java -> {
                    EvmFeeCellViewModel(
                        feeService,
                        gasPriceService,
                        coinServiceFactory.baseCoinService
                    ) as T
                }
                SendEvmTransactionViewModel::class.java -> {
                    SendEvmTransactionViewModel(
                        sendService,
                        coinServiceFactory,
                        cautionViewItemFactory,
                        blockchainType = blockchainType,
                        contactsRepo = App.contactsRepository
                    ) as T
                }
                SendEvmNonceViewModel::class.java -> {
                    SendEvmNonceViewModel(nonceService) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    private fun getToken(blockchainType: BlockchainType): Token {
        return com.monistoWallet.core.App.evmBlockchainManager.getBaseToken(blockchainType)!!
    }

    private fun getGasPrice(transaction: WalletConnectTransaction): GasPrice? = when {
        transaction.maxFeePerGas != null && transaction.maxPriorityFeePerGas != null -> {
            GasPrice.Eip1559(transaction.maxFeePerGas, transaction.maxPriorityFeePerGas)
        }
        else -> {
            transaction.gasPrice?.let { GasPrice.Legacy(it) }
        }
    }

    private fun getGasPriceService(gasPrice: GasPrice?, evmKit: EthereumKit): IEvmGasPriceService {
        return when {
            gasPrice is GasPrice.Legacy || gasPrice == null && !evmKit.chain.isEIP1559Supported -> {
                val gasPriceProvider = LegacyGasPriceProvider(evmKit)
                LegacyGasPriceService(
                    gasPriceProvider,
                    initialGasPrice = (gasPrice as? GasPrice.Legacy)?.legacyGasPrice
                )
            }
            else -> {
                val gasPriceProvider = Eip1559GasPriceProvider(evmKit)
                Eip1559GasPriceService(
                    gasPriceProvider,
                    evmKit,
                    initialGasPrice = gasPrice as? GasPrice.Eip1559
                )
            }
        }
    }

    interface RequestAction {
        val chain: WCRequestChain?

        fun approve(transactionHash: ByteArray)
        fun reject()
    }

}

data class WalletConnectTransaction(
    val from: Address,
    val to: Address,
    val nonce: Long?,
    val gasPrice: Long?,
    val gasLimit: Long?,
    val maxPriorityFeePerGas: Long?,
    val maxFeePerGas: Long?,
    val value: BigInteger,
    val data: ByteArray
)
