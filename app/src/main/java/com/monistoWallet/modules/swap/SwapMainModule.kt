package com.monistoWallet.modules.swap

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.Warning
import com.monistoWallet.core.fiat.AmountTypeSwitchService
import com.monistoWallet.core.fiat.FiatService
import com.monistoWallet.entities.Address
import com.monistoWallet.entities.CurrencyValue
import com.monistoWallet.modules.swap.allowance.SwapAllowanceService
import com.monistoWallet.modules.swap.allowance.SwapAllowanceViewModel
import com.monistoWallet.modules.swap.allowance.SwapPendingAllowanceService
import com.monistoWallet.modules.swap.confirmation.BaseSwapConfirmationFragment
import com.monistoWallet.ui.compose.Select
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.WithTranslatableTitle
import com.monistoWallet.core.parcelable
import com.wallet0x.ethereumkit.core.EthereumKit
import com.wallet0x.marketkit.models.Blockchain
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.Token
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.absoluteValue

object SwapMainModule {

    private const val tokenFromKey = "token_from_key"
    const val resultKey = "swap_settings_result"
    const val swapSettingsRecipientKey = "swap_settings_recipient"
    const val swapSettingsSlippageKey = "swap_settings_slippage"
    const val swapSettingsTtlKey = "swap_settings_ttl"

    fun prepareParams(tokenFrom: Token, swapEntryPointDestId: Int = 0) = bundleOf(
        tokenFromKey to tokenFrom,
        BaseSwapConfirmationFragment.swapEntryPointDestIdKey to swapEntryPointDestId
    )

    data class ProviderViewItem(
        val provider: ISwapProvider,
        val selected: Boolean,
    )

    class Factory(arguments: Bundle) : ViewModelProvider.Factory {
        private val tokenFrom: Token? = arguments.parcelable(tokenFromKey)
        private val swapProviders: List<ISwapProvider> = listOf(
            UniswapProvider,
            UniswapV3Provider,
            PancakeSwapProvider,
            PancakeSwapV3Provider,
            OneInchProvider,
            QuickSwapProvider
        )
        private val switchService by lazy { AmountTypeSwitchService() }
        private val swapMainXService by lazy { SwapMainService(tokenFrom, swapProviders, com.monistoWallet.core.App.localStorage) }
        private val evmKit: EthereumKit = com.monistoWallet.core.App.evmBlockchainManager.getEvmKitManager(swapMainXService.dex.blockchainType).evmKitWrapper?.evmKit ?: throw Exception("EvmKit is not initialized")
        private val allowanceService by lazy { SwapAllowanceService(com.monistoWallet.core.App.adapterManager, evmKit) }
        private val pendingAllowanceService by lazy { SwapPendingAllowanceService(com.monistoWallet.core.App.adapterManager, allowanceService) }
        private val errorShareService by lazy { com.monistoWallet.modules.swap.ErrorShareService() }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            return when (modelClass) {
                SwapMainViewModel::class.java -> {
                    val fromFiatService = FiatService(switchService, com.monistoWallet.core.App.currencyManager, com.monistoWallet.core.App.marketKit)
                    switchService.fromListener = fromFiatService
                    val toFiatService = FiatService(switchService, com.monistoWallet.core.App.currencyManager, com.monistoWallet.core.App.marketKit)
                    switchService.toListener = toFiatService

                    val fromTokenService = SwapTokenService(
                        switchService = switchService,
                        fiatService = fromFiatService,
                        resetAmountOnCoinSelect = true,
                        initialToken = tokenFrom
                    )
                    val toTokenService = SwapTokenService(
                        switchService = switchService,
                        fiatService = toFiatService,
                        resetAmountOnCoinSelect = false,
                        initialToken = null
                    )

                    val formatter = SwapViewItemHelper(com.monistoWallet.core.App.numberFormatter)
                    SwapMainViewModel(
                        formatter,
                        swapMainXService,
                        switchService,
                        fromTokenService,
                        toTokenService,
                        allowanceService,
                        pendingAllowanceService,
                        errorShareService,
                        TimerService(evmKit),
                        com.monistoWallet.core.App.currencyManager,
                        com.monistoWallet.core.App.adapterManager
                    ) as T
                }

                SwapAllowanceViewModel::class.java -> {
                    SwapAllowanceViewModel(
                        errorShareService,
                        allowanceService,
                        pendingAllowanceService,
                        SwapViewItemHelper(com.monistoWallet.core.App.numberFormatter)
                    ) as T
                }

                else -> throw IllegalArgumentException()
            }
        }

    }

    interface ISwapTradeService {
        val state: SwapResultState
        val stateFlow: Flow<SwapResultState>
        val recipient: Address?
        val slippage: BigDecimal
        val ttl: Long? get() = null

        fun stop()
        fun fetchSwapData(
            tokenFrom: Token?,
            tokenTo: Token?,
            amountFrom: BigDecimal?,
            amountTo: BigDecimal?,
            exactType: ExactType
        )

        fun updateSwapSettings(recipient: Address?, slippage: BigDecimal?, ttl: Long?)
    }

    data class SwapState(
        val dex: Dex,
        val providerViewItems: List<ProviderViewItem>,
        val availableBalance: String?,
        val amountTypeSelect: Select<AmountTypeItem>,
        val amountTypeSelectEnabled: Boolean,
        val fromState: SwapCoinCardViewState,
        val toState: SwapCoinCardViewState,
        val tradeView: TradeViewX?,
        val tradePriceExpiration: Float?,
        val error: String?,
        val buttons: SwapButtons,
        val hasNonZeroBalance: Boolean?,
        val recipient: Address?,
        val slippage: BigDecimal,
        val ttl: Long?,
        val refocusKey: Long
    )

    data class SwapCoinCardViewState(
        val token: Token?,
        val uuid: Long,
        val inputState: SwapAmountInputState,
    )

    data class SwapAmountInputState(
        val amount: String,
        val secondaryInfo: String,
        val primaryPrefix: String?,
        val validDecimals: Int,
        val amountEnabled: Boolean,
        val dimAmount: Boolean,
    )

    @Parcelize
    data class PriceImpactViewItem(val level: PriceImpactLevel, val value: String) : Parcelable

    sealed class AmountTypeItem : WithTranslatableTitle {
        object Coin : AmountTypeItem()
        class Currency(val name: String) : AmountTypeItem()

        override val title: TranslatableString
            get() = when (this) {
                Coin -> TranslatableString.ResString(R.string.Swap_AmountTypeCoin)
                is Currency -> TranslatableString.PlainString(name)
            }

        override fun equals(other: Any?): Boolean {
            return other is Coin && this is Coin || other is Currency && this is Currency && other.name == this.name
        }

        override fun hashCode() = when (this) {
            Coin -> javaClass.hashCode()
            is Currency -> name.hashCode()
        }
    }

    sealed class SwapResultState {
        object Loading : SwapResultState()
        class Ready(val swapData: SwapData) : SwapResultState()
        class NotReady(val errors: List<Throwable> = listOf()) : SwapResultState()
    }

    sealed class SwapData {
        data class OneInchData(val data: OneInchSwapParameters) : SwapData()
        data class UniswapData(val data: UniversalSwapTradeData) : SwapData() {
            private val normalPriceImpact = BigDecimal(1)
            private val warningPriceImpact = BigDecimal(5)
            private val forbiddenPriceImpact = BigDecimal(20)

            val priceImpactLevel: PriceImpactLevel? = data.priceImpact?.let {
                when {
                    it >= BigDecimal.ZERO && it < normalPriceImpact -> PriceImpactLevel.Negligible
                    it >= normalPriceImpact && it < warningPriceImpact -> PriceImpactLevel.Normal
                    it >= warningPriceImpact && it < forbiddenPriceImpact -> PriceImpactLevel.Warning
                    else -> PriceImpactLevel.Forbidden
                }
            }
        }
    }

    data class TradeViewX(
        val providerTradeData: ProviderTradeData,
        val expired: Boolean = false
    )

    sealed class ProviderTradeData {
        class OneInchTradeViewItem(
            val primaryPrice: String? = null,
            val secondaryPrice: String? = null,
        ) : ProviderTradeData()

        class UniswapTradeViewItem(
            val primaryPrice: String? = null,
            val secondaryPrice: String? = null,
            val priceImpact: PriceImpactViewItem? = null,
        ) : ProviderTradeData()
    }

    @Parcelize
    class Dex(val blockchain: Blockchain, val provider: ISwapProvider) : Parcelable {
        val blockchainType get() = blockchain.type
    }

    interface ISwapProvider : Parcelable {
        val id: String
        val title: String
        val url: String
        val supportsExactOut: Boolean

        fun supports(blockchainType: BlockchainType): Boolean
    }

    @Parcelize
    object UniswapProvider : ISwapProvider {
        override val id get() = "uniswap"
        override val title get() = "Uniswap"
        override val url get() = "https://uniswap.org/"
        override val supportsExactOut get() = true

        override fun supports(blockchainType: BlockchainType): Boolean {
            return blockchainType == BlockchainType.Ethereum
        }
    }

    @Parcelize
    object UniswapV3Provider : ISwapProvider {
        override val id get() = "uniswap_v3"
        override val title get() = "Uniswap V3"
        override val url get() = "https://uniswap.org/"
        override val supportsExactOut get() = true

        override fun supports(blockchainType: BlockchainType) = when (blockchainType) {
            BlockchainType.Ethereum,
            BlockchainType.ArbitrumOne,
//            BlockchainType.Optimism,
            BlockchainType.Polygon,
            BlockchainType.Dexnet,
            BlockchainType.BinanceSmartChain -> true
            else -> false
        }
    }

    @Parcelize
    object PancakeSwapProvider : ISwapProvider {
        override val id get() = "pancake"
        override val title get() = "PancakeSwap"
        override val url get() = "https://pancakeswap.finance/"
        override val supportsExactOut get() = true

        override fun supports(blockchainType: BlockchainType): Boolean {
            return blockchainType == BlockchainType.BinanceSmartChain
        }
    }

    @Parcelize
    object PancakeSwapV3Provider : ISwapProvider {
        override val id get() = "pancake_v3"
        override val title get() = "PancakeSwap V3"
        override val url get() = "https://pancakeswap.finance/"
        override val supportsExactOut get() = true

        override fun supports(blockchainType: BlockchainType) = when (blockchainType) {
            BlockchainType.BinanceSmartChain,
            BlockchainType.Ethereum -> true
            else -> false
        }
    }

    @Parcelize
    object OneInchProvider : ISwapProvider {
        override val id get() = "oneinch"
        override val title get() = "1inch"
        override val url get() = "https://app.1inch.io/"
        override val supportsExactOut get() = false

        override fun supports(blockchainType: BlockchainType) = when (blockchainType) {
            BlockchainType.Ethereum,
            BlockchainType.BinanceSmartChain,
            BlockchainType.Polygon,
            BlockchainType.Avalanche,
            BlockchainType.Optimism,
            BlockchainType.Gnosis,
            BlockchainType.Fantom,
            BlockchainType.ArbitrumOne -> true

            else -> false
        }
    }

    @Parcelize
    object QuickSwapProvider : ISwapProvider {
        override val id get() = "quickswap"
        override val title get() = "QuickSwap"
        override val url get() = "https://quickswap.exchange/"
        override val supportsExactOut get() = true

        override fun supports(blockchainType: BlockchainType): Boolean {
            return blockchainType == BlockchainType.Polygon
        }
    }

    @Parcelize
    data class ApproveData(
        val dex: Dex,
        val token: Token,
        val spenderAddress: String,
        val amount: BigDecimal,
        val allowance: BigDecimal
    ) : Parcelable

    @Parcelize
    enum class PriceImpactLevel : Parcelable {
        Negligible, Normal, Warning, Forbidden
    }

    abstract class UniswapWarnings : Warning() {
        object PriceImpactWarning : UniswapWarnings()
        class PriceImpactForbidden(val providerName: String) : UniswapWarnings()
    }

    @Parcelize
    data class OneInchSwapParameters(
        val tokenFrom: Token,
        val tokenTo: Token,
        val amountFrom: BigDecimal,
        val amountTo: BigDecimal,
        val slippage: BigDecimal,
        val recipient: Address? = null
    ) : Parcelable

    sealed class SwapError : Throwable() {
        object InsufficientBalanceFrom : SwapError()
        object InsufficientAllowance : SwapError()
        object RevokeAllowanceRequired : SwapError()
        object ForbiddenPriceImpactLevel : SwapError()
    }

    @Parcelize
    data class CoinBalanceItem(
        val token: Token,
        val balance: BigDecimal?,
        val fiatBalanceValue: CurrencyValue?,
    ) : Parcelable

    enum class ExactType {
        ExactFrom, ExactTo
    }

    sealed class SwapActionState {
        object Hidden : SwapActionState()
        class Enabled(val buttonTitle: String) : SwapActionState()
        class Disabled(val buttonTitle: String, val loading: Boolean = false) : SwapActionState()

        val title: String
            get() = when (this) {
                is Enabled -> this.buttonTitle
                is Disabled -> this.buttonTitle
                else -> ""
            }

        val showProgress: Boolean
            get() = this is Disabled && loading
    }

    data class SwapButtons(
        val revoke: SwapActionState,
        val approve: SwapActionState,
        val proceed: SwapActionState
    )

}

fun BigDecimal.scaleUp(scale: Int): BigInteger {
    val exponent = scale - scale()

    return if (exponent >= 0) {
        unscaledValue() * BigInteger.TEN.pow(exponent)
    } else {
        unscaledValue() / BigInteger.TEN.pow(exponent.absoluteValue)
    }
}
