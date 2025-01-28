package com.monistoWallet.modules.swap.uniswap

import com.monistoWallet.entities.Address
import com.monistoWallet.modules.swap.SwapMainModule.ExactType
import com.monistoWallet.modules.swap.SwapMainModule.SwapData.UniswapData
import com.monistoWallet.modules.swap.SwapMainModule.SwapResultState
import com.monistoWallet.modules.swap.UniversalSwapTradeData
import com.monistoWallet.modules.swap.settings.uniswap.SwapTradeOptions
import com.wallet0x.ethereumkit.models.TransactionData
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.Token
import com.wallet0x.marketkit.models.TokenType
import com.wallet0x.uniswapkit.TradeError
import com.wallet0x.uniswapkit.UniswapKit
import com.wallet0x.uniswapkit.models.SwapData
import com.wallet0x.uniswapkit.models.TradeOptions
import com.wallet0x.uniswapkit.models.TradeType
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

class UniswapV2TradeService(
    private val uniswapKit: UniswapKit
) : IUniswapTradeService {

    private var swapDataDisposable: Disposable? = null
    private var swapData: SwapData? = null

    override var state: SwapResultState = SwapResultState.NotReady()
        private set(value) {
            field = value
            _stateFlow.update { value }
        }

    override val recipient: Address?
        get() = tradeOptions.recipient
    override val slippage: BigDecimal
        get() = tradeOptions.allowedSlippage
    override val ttl: Long
        get() = tradeOptions.ttl

    private val _stateFlow = MutableStateFlow(state)
    override val stateFlow: StateFlow<SwapResultState>
        get() = _stateFlow

    override var tradeOptions: SwapTradeOptions = SwapTradeOptions()
        set(value) {
            field = value
        }

    override fun stop() {
        clearDisposables()
    }

    override fun fetchSwapData(
        tokenFrom: Token?,
        tokenTo: Token?,
        amountFrom: BigDecimal?,
        amountTo: BigDecimal?,
        exactType: ExactType
    ) {
        if (tokenFrom == null || tokenTo == null) {
            state = SwapResultState.NotReady()
            return
        }

        state = SwapResultState.Loading

        swapDataDisposable?.dispose()
        swapDataDisposable = null

        swapDataDisposable = swapDataSingle(tokenFrom, tokenTo)
            .subscribeOn(Schedulers.io())
            .subscribe({
                swapData = it
                syncTradeData(exactType, amountFrom, amountTo, tokenFrom, tokenTo)
            }, { error ->
                state = SwapResultState.NotReady(listOf(error))
            })
    }

    override fun updateSwapSettings(recipient: Address?, slippage: BigDecimal?, ttl: Long?) {
        tradeOptions = SwapTradeOptions(
            slippage ?: TradeOptions.defaultAllowedSlippage,
            ttl ?: TradeOptions.defaultTtl,
            recipient
        )
    }

    @Throws
    override fun transactionData(tradeData: UniversalSwapTradeData): TransactionData {
        return uniswapKit.transactionData(tradeData.getTradeDataV2())
    }

    private fun clearDisposables() {
        swapDataDisposable?.dispose()
        swapDataDisposable = null
    }

    private fun syncTradeData(exactType: ExactType, amountFrom: BigDecimal?, amountTo: BigDecimal?, tokenFrom: Token, tokenTo: Token) {
        val swapData = swapData ?: return

        val amount = if (exactType == ExactType.ExactFrom) amountFrom else amountTo

        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            state = SwapResultState.NotReady()
            return
        }

        try {
            val tradeType = when (exactType) {
                ExactType.ExactFrom -> TradeType.ExactIn
                ExactType.ExactTo -> TradeType.ExactOut
            }
            val tradeData = tradeData(swapData, amount, tradeType, tradeOptions.tradeOptions)
            state = SwapResultState.Ready(UniswapData(tradeData))
        } catch (e: Throwable) {
            val error = when {
                e is TradeError.TradeNotFound && isEthWrapping(tokenFrom, tokenTo) -> TradeServiceError.WrapUnwrapNotAllowed
                else -> e
            }
            state = SwapResultState.NotReady(listOf(error))
        }
    }

    private fun swapDataSingle(tokenIn: Token?, tokenOut: Token?): Single<SwapData> {
        return try {
            val uniswapTokenIn = uniswapToken(tokenIn)
            val uniswapTokenOut = uniswapToken(tokenOut)

            uniswapKit.swapData(uniswapTokenIn, uniswapTokenOut)
        } catch (error: Throwable) {
            Single.error(error)
        }
    }

    private fun tradeData(swapData: SwapData, amount: BigDecimal, tradeType: TradeType, tradeOptions: TradeOptions): UniversalSwapTradeData {
        val tradeData = when (tradeType) {
            TradeType.ExactIn -> {
                uniswapKit.bestTradeExactIn(swapData, amount, tradeOptions)
            }
            TradeType.ExactOut -> {
                uniswapKit.bestTradeExactOut(swapData, amount, tradeOptions)
            }
        }
        return UniversalSwapTradeData.buildFromTradeDataV2(tradeData)
    }

    @Throws
    private fun uniswapToken(token: Token?) = when (val tokenType = token?.type) {
        TokenType.Native -> when (token.blockchainType) {
            BlockchainType.Ethereum,
            BlockchainType.BinanceSmartChain,
            BlockchainType.Polygon,
            BlockchainType.Optimism,
            BlockchainType.ArbitrumOne -> uniswapKit.etherToken()
            else -> throw Exception("Invalid coin for swap: $token")
        }
        is TokenType.Eip20 -> uniswapKit.token(
            com.wallet0x.ethereumkit.models.Address(
                tokenType.address
            ), token.decimals)
        else -> throw Exception("Invalid coin for swap: $token")
    }

    private val TokenType.isWeth: Boolean
        get() = this is TokenType.Eip20 && address.equals(uniswapKit.etherToken().address.hex, true)
    private val Token.isWeth: Boolean
        get() = type.isWeth
    private val Token.isNative: Boolean
        get() = type == TokenType.Native

    private fun isEthWrapping(tokenFrom: Token?, tokenTo: Token?) =
        when {
            tokenFrom == null || tokenTo == null -> false
            else -> {
                tokenFrom.isNative && tokenTo.isWeth || tokenTo.isNative && tokenFrom.isWeth
            }
        }

    sealed class TradeServiceError : Throwable() {
        object WrapUnwrapNotAllowed : TradeServiceError()
    }

}
