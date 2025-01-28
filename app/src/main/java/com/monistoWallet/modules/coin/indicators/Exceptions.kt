package com.monistoWallet.modules.coin.indicators

import com.monistoWallet.R
import com.monistoWallet.core.providers.Translator

class NotIntegerException : Exception() {
    override fun getLocalizedMessage(): String {
        return Translator.getString(R.string.Error_NotInteger)
    }
}

class OutOfRangeException(val lower: Int, val upper: Int) : Exception() {
    override fun getLocalizedMessage(): String {
        return Translator.getString(R.string.Error_OutOfRange, lower, upper)
    }
}