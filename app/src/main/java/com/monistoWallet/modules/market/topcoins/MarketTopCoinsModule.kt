package com.monistoWallet.modules.market.topcoins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.market.MarketField
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.TopMarket
import com.monistoWallet.ui.compose.Select

object MarketTopCoinsModule {

    class Factory(
        private val topMarket: com.monistoWallet.modules.market.TopMarket? = null,
        private val sortingField: com.monistoWallet.modules.market.SortingField? = null,
        private val marketField: com.monistoWallet.modules.market.MarketField? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val topMarketsRepository = MarketTopMoversRepository(com.monistoWallet.core.App.marketKit)
            val service = MarketTopCoinsService(
                topMarketsRepository,
                com.monistoWallet.core.App.currencyManager,
                com.monistoWallet.core.App.marketFavoritesManager,
                topMarket ?: defaultTopMarket,
                sortingField ?: defaultSortingField,
                marketField ?: defaultMarketField
            )
            return MarketTopCoinsViewModel(
                service,
                marketField ?: defaultMarketField
            ) as T
        }

        companion object {
            val defaultSortingField = com.monistoWallet.modules.market.SortingField.HighestCap
            val defaultTopMarket = com.monistoWallet.modules.market.TopMarket.Top100
            val defaultMarketField = com.monistoWallet.modules.market.MarketField.PriceDiff
        }
    }

    data class Menu(
        val sortingFieldSelect: Select<com.monistoWallet.modules.market.SortingField>,
        val topMarketSelect: Select<com.monistoWallet.modules.market.TopMarket>?,
        val marketFieldSelect: Select<com.monistoWallet.modules.market.MarketField>
    )

}

sealed class SelectorDialogState() {
    object Closed : SelectorDialogState()
    class Opened(val select: Select<com.monistoWallet.modules.market.SortingField>) : SelectorDialogState()
}
