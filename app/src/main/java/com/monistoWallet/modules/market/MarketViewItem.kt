package com.monistoWallet.modules.market

import androidx.compose.runtime.Immutable
import com.monistoWallet.core.App
import com.monistoWallet.core.iconPlaceholder
import com.monistoWallet.core.imageUrl
import com.wallet0x.marketkit.models.FullCoin

@Immutable
data class MarketViewItem(
    val fullCoin: FullCoin,
    val coinRate: String,
    val marketDataValue: com.monistoWallet.modules.market.MarketDataValue,
    val rank: String?,
    val favorited: Boolean,
) {

    val coinUid: String
        get() = fullCoin.coin.uid

    val coinCode: String
        get() = fullCoin.coin.code

    val coinName: String
        get() = fullCoin.coin.name

    val iconUrl: String
        get() = fullCoin.coin.imageUrl

    val iconPlaceHolder: Int
        get() = fullCoin.iconPlaceholder

    fun areItemsTheSame(other: MarketViewItem): Boolean {
        return fullCoin.coin == other.fullCoin.coin
    }

    fun areContentsTheSame(other: MarketViewItem): Boolean {
        return this == other
    }

    companion object {
        fun create(
            marketItem: com.monistoWallet.modules.market.MarketItem,
            marketField: com.monistoWallet.modules.market.MarketField,
            favorited: Boolean = false
        ): MarketViewItem {
            val marketDataValue = when (marketField) {
                com.monistoWallet.modules.market.MarketField.MarketCap -> {
                    val marketCapFormatted = com.monistoWallet.core.App.numberFormatter.formatFiatShort(
                        marketItem.marketCap.value,
                        marketItem.marketCap.currency.symbol,
                        2
                    )

                    com.monistoWallet.modules.market.MarketDataValue.MarketCap(marketCapFormatted)
                }
                com.monistoWallet.modules.market.MarketField.Volume -> {
                    val volumeFormatted = com.monistoWallet.core.App.numberFormatter.formatFiatShort(
                        marketItem.volume.value,
                        marketItem.volume.currency.symbol,
                        2
                    )

                    com.monistoWallet.modules.market.MarketDataValue.Volume(volumeFormatted)
                }
                com.monistoWallet.modules.market.MarketField.PriceDiff -> {
                    com.monistoWallet.modules.market.MarketDataValue.Diff(marketItem.diff)
                }
            }
            return MarketViewItem(
                marketItem.fullCoin,
                com.monistoWallet.core.App.numberFormatter.formatFiatFull(
                    marketItem.rate.value,
                    marketItem.rate.currency.symbol
                ),
                marketDataValue,
                marketItem.rank?.toString(),
                favorited
            )
        }
    }
}