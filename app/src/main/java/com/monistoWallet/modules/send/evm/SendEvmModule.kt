package com.monistoWallet.modules.send.evm

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.core.ISendEthereumAdapter
import com.monistoWallet.core.Warning
import com.monistoWallet.core.isNative
import com.monistoWallet.entities.Address
import com.monistoWallet.entities.Wallet
import com.monistoWallet.modules.amount.AmountValidator
import com.monistoWallet.modules.amount.SendAmountService
import com.monistoWallet.modules.send.evm.confirmation.EvmKitWrapperHoldingViewModel
import com.monistoWallet.modules.swap.SwapMainModule.PriceImpactViewItem
import com.monistoWallet.modules.walletconnect.request.WCRequestChain
import com.monistoWallet.modules.xrate.XRateService
import com.wallet0x.ethereumkit.models.TransactionData
import com.wallet0x.marketkit.models.Token
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode


data class SendEvmData(
    val transactionData: TransactionData,
    val additionalInfo: AdditionalInfo? = null,
    val warnings: List<Warning> = listOf()
) {
    sealed class AdditionalInfo : Parcelable {
        @Parcelize
        class Send(val info: SendInfo) : AdditionalInfo()

        @Parcelize
        class Uniswap(val info: UniswapInfo) : AdditionalInfo()

        @Parcelize
        class OneInchSwap(val info: OneInchSwapInfo) : AdditionalInfo()

        @Parcelize
        class WalletConnectRequest(val info: WalletConnectInfo) : AdditionalInfo()

        val sendInfo: SendInfo?
            get() = (this as? Send)?.info

        val uniswapInfo: UniswapInfo?
            get() = (this as? Uniswap)?.info

        val oneInchSwapInfo: OneInchSwapInfo?
            get() = (this as? OneInchSwap)?.info

        val walletConnectInfo: WalletConnectInfo?
            get() = (this as? WalletConnectRequest)?.info
    }

    @Parcelize
    data class SendInfo(
        val nftShortMeta: NftShortMeta? = null
    ) : Parcelable

    @Parcelize
    data class NftShortMeta(
        val nftName: String,
        val previewImageUrl: String?
    ) : Parcelable

    @Parcelize
    data class WalletConnectInfo(
        val dAppName: String?,
        val chain: WCRequestChain?
    ) : Parcelable

    @Parcelize
    data class UniswapInfo(
        val estimatedOut: BigDecimal,
        val estimatedIn: BigDecimal,
        val slippage: String? = null,
        val deadline: String? = null,
        val recipientDomain: String? = null,
        val price: String? = null,
        val priceImpact: PriceImpactViewItem? = null,
        val gasPrice: String? = null,
    ) : Parcelable

    @Parcelize
    data class OneInchSwapInfo(
        val tokenFrom: Token,
        val tokenTo: Token,
        val amountFrom: BigDecimal,
        val estimatedAmountTo: BigDecimal,
        val slippage: BigDecimal,
        val recipient: Address?,
        val price: String? = null
    ) : Parcelable
}

object SendEvmModule {

    const val transactionDataKey = "transactionData"
    const val additionalInfoKey = "additionalInfo"
    const val blockchainTypeKey = "blockchainType"
    const val backButtonKey = "backButton"
    const val sendNavGraphIdKey = "sendNavGraphId_key"
    const val sendEntryPointDestIdKey = "sendEntryPointDestIdKey"

    @Parcelize
    data class TransactionDataParcelable(
        val toAddress: String,
        val value: BigInteger,
        val input: ByteArray
    ) : Parcelable {
        constructor(transactionData: TransactionData) : this(
            transactionData.to.hex,
            transactionData.value,
            transactionData.input
        )
    }


    class Factory(private val wallet: Wallet, private val predefinedAddress: String?) : ViewModelProvider.Factory {
        val adapter = (com.monistoWallet.core.App.adapterManager.getAdapterForWallet(wallet) as? ISendEthereumAdapter) ?: throw IllegalArgumentException("SendEthereumAdapter is null")

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                EvmKitWrapperHoldingViewModel::class.java -> {
                    EvmKitWrapperHoldingViewModel(adapter.evmKitWrapper) as T
                }
                SendEvmViewModel::class.java -> {
                    val amountValidator = AmountValidator()
                    val coinMaxAllowedDecimals = wallet.token.decimals

                    val amountService = SendAmountService(
                        amountValidator,
                        wallet.token.coin.code,
                        adapter.balanceData.available.setScale(coinMaxAllowedDecimals, RoundingMode.DOWN),
                        wallet.token.type.isNative
                    )
                    val addressService = SendEvmAddressService(predefinedAddress)
                    val xRateService = XRateService(com.monistoWallet.core.App.marketKit, com.monistoWallet.core.App.currencyManager.baseCurrency)

                    SendEvmViewModel(
                        wallet,
                        wallet.token,
                        adapter,
                        xRateService,
                        amountService,
                        addressService,
                        coinMaxAllowedDecimals,
                        predefinedAddress == null,
                        com.monistoWallet.core.App.connectivityManager,
                    ) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }
}
