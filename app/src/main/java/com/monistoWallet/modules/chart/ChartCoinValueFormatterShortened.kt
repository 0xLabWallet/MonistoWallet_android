package com.monistoWallet.modules.chart

import com.monistoWallet.core.App
import com.monistoWallet.entities.Currency
import com.wallet0x.marketkit.models.FullCoin
import java.math.BigDecimal

class ChartCoinValueFormatterShortened(private val fullCoin: FullCoin) : ChartModule.ChartNumberFormatter {

    override fun formatValue(currency: Currency, value: BigDecimal): String {
        return com.monistoWallet.core.App.numberFormatter.formatCoinShort(value, fullCoin.coin.code, 8)
    }

}
