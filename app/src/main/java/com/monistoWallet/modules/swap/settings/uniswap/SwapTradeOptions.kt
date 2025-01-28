package com.monistoWallet.modules.swap.settings.uniswap

import com.monistoWallet.entities.Address
import com.wallet0x.uniswapkit.models.TradeOptions
import java.math.BigDecimal

class SwapTradeOptions(
    var allowedSlippage: BigDecimal = TradeOptions.defaultAllowedSlippage,
    var ttl: Long = TradeOptions.defaultTtl,
    var recipient: Address? = null
) {

    val tradeOptions: TradeOptions
        get() {
            val address = recipient?.let {
                try {
                    com.wallet0x.ethereumkit.models.Address(it.hex)
                } catch (err: Exception) {
                    null
                }
            }

            return TradeOptions(allowedSlippage, ttl, address)
        }
}
