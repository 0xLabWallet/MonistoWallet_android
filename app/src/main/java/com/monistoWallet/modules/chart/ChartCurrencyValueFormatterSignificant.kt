package com.monistoWallet.modules.chart

import com.monistoWallet.core.App
import com.monistoWallet.entities.Currency
import java.math.BigDecimal

class ChartCurrencyValueFormatterSignificant : ChartModule.ChartNumberFormatter {
    override fun formatValue(currency: Currency, value: BigDecimal): String {
        return com.monistoWallet.core.App.numberFormatter.formatFiatFull(value, currency.symbol)
    }
}
