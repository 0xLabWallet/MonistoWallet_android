package com.monistoWallet.modules.swap

import com.monistoWallet.R
import com.monistoWallet.core.IAppNumberFormatter
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.modules.swap.SwapMainModule.PriceImpactLevel
import com.monistoWallet.modules.swap.SwapMainModule.PriceImpactViewItem
import com.wallet0x.marketkit.models.Token
import com.wallet0x.uniswapkit.models.TradeOptions
import java.math.BigDecimal

class SwapViewItemHelper(private val numberFormatter: IAppNumberFormatter) {

    fun prices(sellPrice: BigDecimal, buyPrice: BigDecimal, tokenFrom: Token?, tokenTo: Token?): Pair<String?, String?> {
        val primaryPrice: String?
        val secondaryPrice: String?

        val sellPriceStr = price(sellPrice, tokenTo, tokenFrom)
        val buyPriceStr  = price(buyPrice, tokenFrom, tokenTo)
        if (sellPrice > buyPrice) {
            primaryPrice = sellPriceStr
            secondaryPrice = buyPriceStr
        } else {
            primaryPrice = buyPriceStr
            secondaryPrice = sellPriceStr
        }
        return Pair(primaryPrice, secondaryPrice)
    }

    private fun price(price: BigDecimal?, quoteToken: Token?, baseToken: Token?): String? {
        if (price == null || quoteToken == null || baseToken == null)
            return null

        return "1 ${baseToken.coin.code} = ${coinAmount(price, quoteToken.coin.code)}"
    }

    fun priceImpactViewItem(
        trade: SwapMainModule.SwapData.UniswapData,
        minLevel: PriceImpactLevel = PriceImpactLevel.Normal
    ): PriceImpactViewItem? {

        val priceImpact = trade.data.priceImpact ?: return null
        val impactLevel = trade.priceImpactLevel ?: return null
        if (impactLevel < minLevel) {
            return null
        }

        return PriceImpactViewItem(impactLevel, Translator.getString(R.string.Swap_Percent, priceImpact * BigDecimal.valueOf(-1)))
    }

    fun slippage(allowedSlippage: BigDecimal): String? {
        val defaultTradeOptions = TradeOptions()
        return if (allowedSlippage.compareTo(defaultTradeOptions.allowedSlippagePercent) == 0) {
            null
        } else {
            "$allowedSlippage%"
        }
    }

    fun deadline(ttl: Long): String? {
        val defaultTradeOptions = TradeOptions()
        return if (ttl == defaultTradeOptions.ttl) {
            null
        } else {
            Translator.getString(R.string.Duration_Minutes, ttl / 60)
        }
    }

    fun coinAmount(amount: BigDecimal, coinCode: String): String {
        return numberFormatter.formatCoinFull(amount, coinCode, 8)
    }

}
