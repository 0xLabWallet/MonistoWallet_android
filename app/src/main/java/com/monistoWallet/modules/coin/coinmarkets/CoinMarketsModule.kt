package com.monistoWallet.modules.coin.coinmarkets

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.modules.market.MarketField
import com.monistoWallet.ui.compose.Select
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.WithTranslatableTitle
import com.wallet0x.marketkit.models.FullCoin
import java.math.BigDecimal

object CoinMarketsModule {
    class Factory(private val fullCoin: FullCoin) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = CoinMarketsService(fullCoin, com.monistoWallet.core.App.currencyManager, com.monistoWallet.core.App.marketKit)
            return CoinMarketsViewModel(service) as T
        }
    }

    @Immutable
    data class Menu(
        val sortDescending: Boolean,
        val marketFieldSelect: Select<com.monistoWallet.modules.market.MarketField>
    )

    sealed class VolumeMenuType : WithTranslatableTitle {
        class Coin(val name: String) : VolumeMenuType()
        class Currency(val name: String) : VolumeMenuType()

        override val title: TranslatableString
            get() = when (this) {
                is Coin -> TranslatableString.PlainString(name)
                is Currency -> TranslatableString.PlainString(name)
            }
    }
}

data class MarketTickerItem(
    val market: String,
    val marketImageUrl: String?,
    val baseCoinCode: String,
    val targetCoinCode: String,
    val rate: BigDecimal,
    val volume: BigDecimal,
    val volumeType: CoinMarketsModule.VolumeMenuType,
    val tradeUrl: String?,
    val verified: Boolean
)

enum class VerifiedType: WithTranslatableTitle  {
    Verified, All;

    override val title: TranslatableString
        get() = when(this) {
            Verified -> TranslatableString.ResString(R.string.CoinPage_MarketsVerifiedMenu_Verified)
            All -> TranslatableString.ResString(R.string.CoinPage_MarketsVerifiedMenu_All)
        }

    fun next() = values()[if (ordinal == values().size - 1) 0 else ordinal + 1]
}
