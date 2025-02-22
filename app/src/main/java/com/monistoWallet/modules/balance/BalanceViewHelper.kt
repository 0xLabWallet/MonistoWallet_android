package com.monistoWallet.modules.balance

import com.monistoWallet.core.App
import com.monistoWallet.entities.Currency
import com.wallet0x.marketkit.models.CoinPrice
import java.math.BigDecimal

object BalanceViewHelper {

    fun getPrimaryAndSecondaryValues(
        balance: BigDecimal,
        visible: Boolean,
        fullFormat: Boolean,
        coinDecimals: Int,
        dimmed: Boolean,
        coinPrice: CoinPrice?,
        currency: Currency,
        balanceViewType: BalanceViewType
    ): Pair<DeemedValue<String>, DeemedValue<String>> {
        val coinValueStr = coinValue(
            balance = balance,
            visible = visible,
            fullFormat = fullFormat,
            coinDecimals = coinDecimals,
            dimmed = dimmed
        )
        val currencyValueStr = currencyValue(
            balance = balance,
            coinPrice = coinPrice,
            visible = visible,
            fullFormat = fullFormat,
            currency = currency,
            dimmed = dimmed
        )

        val primaryValue: DeemedValue<String>
        val secondaryValue: DeemedValue<String>
        when (balanceViewType) {
            BalanceViewType.CoinThenFiat -> {
                primaryValue = coinValueStr
                secondaryValue = currencyValueStr
            }

            BalanceViewType.FiatThenCoin -> {
                primaryValue = currencyValueStr
                secondaryValue = coinValueStr
            }
        }
        return Pair(primaryValue, secondaryValue)
    }

    fun coinValue(
        balance: BigDecimal,
        visible: Boolean,
        fullFormat: Boolean,
        coinDecimals: Int,
        dimmed: Boolean
    ): DeemedValue<String> {
        val formatted = if (fullFormat) {
            com.monistoWallet.core.App.numberFormatter.formatCoinFull(balance, null, coinDecimals)
        } else {
            com.monistoWallet.core.App.numberFormatter.formatCoinShort(balance, null, coinDecimals)
        }

        return DeemedValue(formatted, dimmed, visible)
    }

    fun currencyValue(
        balance: BigDecimal,
        coinPrice: CoinPrice?,
        visible: Boolean,
        fullFormat: Boolean,
        currency: Currency,
        dimmed: Boolean
    ): DeemedValue<String> {
        val dimmedOrExpired = dimmed || coinPrice?.expired ?: false
        val formatted = coinPrice?.value?.let { rate ->
            val balanceFiat = balance.multiply(rate)

            if (fullFormat) {
                com.monistoWallet.core.App.numberFormatter.formatFiatFull(balanceFiat, currency.symbol)
            } else {
                com.monistoWallet.core.App.numberFormatter.formatFiatShort(balanceFiat, currency.symbol, 8)
            }
        } ?: ""

        return DeemedValue(formatted, dimmedOrExpired, visible)
    }

    fun rateValue(coinPrice: CoinPrice?, currency: Currency, visible: Boolean): DeemedValue<String> {
        val value = coinPrice?.let {
            com.monistoWallet.core.App.numberFormatter.formatFiatFull(coinPrice.value, currency.symbol)
        } ?: ""

        return DeemedValue(value, dimmed = coinPrice?.expired ?: false, visible = visible)
    }

}