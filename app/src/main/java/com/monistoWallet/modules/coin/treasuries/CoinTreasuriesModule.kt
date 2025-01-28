package com.monistoWallet.modules.coin.treasuries

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.ui.compose.Select
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.WithTranslatableTitle
import com.wallet0x.marketkit.models.Coin

object CoinTreasuriesModule {
    @Suppress("UNCHECKED_CAST")
    class Factory(private val coin: Coin) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = CoinTreasuriesRepository(com.monistoWallet.core.App.marketKit)
            val service = CoinTreasuriesService(coin, repository, com.monistoWallet.core.App.currencyManager)
            return CoinTreasuriesViewModel(service, com.monistoWallet.core.App.numberFormatter) as T
        }
    }

    @Immutable
    data class CoinTreasuriesData(
        val treasuryTypeSelect: Select<TreasuryTypeFilter>,
        val sortDescending: Boolean,
        val coinTreasuries: List<CoinTreasuryItem>
    )

    @Immutable
    data class CoinTreasuryItem(
        val fund: String,
        val fundLogoUrl: String,
        val country: String,
        val amount: String,
        val amountInCurrency: String
    )

    enum class TreasuryTypeFilter : WithTranslatableTitle {
        All, Public, Private, ETF;

        override val title: TranslatableString
            get() = when (this) {
                All -> TranslatableString.ResString(R.string.MarketGlobalMetrics_ChainSelectorAll)
                else -> TranslatableString.PlainString(name)
            }
    }

    sealed class SelectorDialogState {
        object Closed : SelectorDialogState()
        class Opened(val select: Select<TreasuryTypeFilter>) : SelectorDialogState()
    }
}
