package com.monistoWallet.modules.swap.oneinch

import com.monistoWallet.core.convertedError
import com.monistoWallet.modules.swap.scaleUp
import com.wallet0x.ethereumkit.core.EthereumKit
import com.wallet0x.ethereumkit.models.Address
import com.wallet0x.ethereumkit.models.GasPrice
import com.wallet0x.marketkit.models.Token
import com.wallet0x.marketkit.models.TokenType
import com.wallet0x.oneinchkit.OneInchKit
import com.wallet0x.oneinchkit.Quote
import com.wallet0x.oneinchkit.Swap
import io.reactivex.Single
import java.math.BigDecimal

class OneInchKitHelper(
    evmKit: EthereumKit,
    apiKey: String
) {
    private val oneInchKit = OneInchKit.getInstance(evmKit, apiKey)

    // TODO take evmCoinAddress from oneInchKit
    private val evmCoinAddress = Address("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")

    private fun getTokenAddress(token: Token) = when (val tokenType = token.type) {
        TokenType.Native -> evmCoinAddress
        is TokenType.Eip20 -> Address(tokenType.address)
        else -> throw IllegalStateException("Unsupported tokenType: $tokenType")
    }

    val smartContractAddress: Address
        get() = oneInchKit.routerAddress

    fun getQuoteAsync(
        fromToken: Token,
        toToken: Token,
        fromAmount: BigDecimal
    ): Single<Quote> {
        return oneInchKit.getQuoteAsync(
            fromToken = getTokenAddress(fromToken),
            toToken = getTokenAddress(toToken),
            amount = fromAmount.scaleUp(fromToken.decimals)
        ).onErrorResumeNext {
            Single.error(it.convertedError)
        }
    }

    fun getSwapAsync(
        fromToken: Token,
        toToken: Token,
        fromAmount: BigDecimal,
        slippagePercentage: Float,
        recipient: String? = null,
        gasPrice: GasPrice? = null
    ): Single<Swap> {
        return oneInchKit.getSwapAsync(
            fromToken = getTokenAddress(fromToken),
            toToken = getTokenAddress(toToken),
            amount = fromAmount.scaleUp(fromToken.decimals),
            slippagePercentage = slippagePercentage,
            recipient = recipient?.let { Address(it) },
            gasPrice = gasPrice
        ).onErrorResumeNext {
            Single.error(it.convertedError)
        }
    }

}
