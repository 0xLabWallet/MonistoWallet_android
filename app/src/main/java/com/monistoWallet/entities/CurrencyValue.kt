package com.monistoWallet.entities

import android.os.Parcelable
import com.monistoWallet.core.App
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class CurrencyValue(val currency: Currency, val value: BigDecimal) : Parcelable {
    fun getFormattedFull(): String {
        return com.monistoWallet.core.App.numberFormatter.formatFiatFull(value, currency.symbol)
    }

    fun getFormattedShort(): String {
        return com.monistoWallet.core.App.numberFormatter.formatFiatShort(value, currency.symbol, 2)
    }
}
