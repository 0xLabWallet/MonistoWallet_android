package com.monistoWallet.modules.market.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.chart.ChartCurrencyValueFormatterShortened
import com.monistoWallet.modules.chart.ChartModule
import com.monistoWallet.modules.chart.ChartViewModel
import com.monistoWallet.modules.market.MarketField
import com.monistoWallet.modules.market.MarketItem
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.TopMarket
import com.monistoWallet.ui.compose.Select
import com.wallet0x.marketkit.models.CoinCategory

object MarketCategoryModule {

    class Factory(
        private val coinCategory: CoinCategory
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                MarketCategoryViewModel::class.java -> {
                    val marketCategoryRepository = MarketCategoryRepository(com.monistoWallet.core.App.marketKit)
                    val service = MarketCategoryService(
                        marketCategoryRepository,
                        com.monistoWallet.core.App.currencyManager,
                        com.monistoWallet.core.App.languageManager,
                        com.monistoWallet.core.App.marketFavoritesManager,
                        coinCategory,
                        defaultTopMarket,
                        defaultSortingField
                    )
                    MarketCategoryViewModel(service) as T
                }

                ChartViewModel::class.java -> {
                    val chartService = CoinCategoryMarketDataChartService(
                        com.monistoWallet.core.App.currencyManager,
                        com.monistoWallet.core.App.marketKit,
                        coinCategory.uid
                    )
                    val chartNumberFormatter = ChartCurrencyValueFormatterShortened()
                    ChartModule.createViewModel(chartService, chartNumberFormatter) as T
                }
                else -> throw IllegalArgumentException()
            }
        }

        companion object {
            val defaultSortingField = com.monistoWallet.modules.market.SortingField.HighestCap
            val defaultTopMarket = com.monistoWallet.modules.market.TopMarket.Top100
        }
    }

    data class Menu(
        val sortingFieldSelect: Select<com.monistoWallet.modules.market.SortingField>,
        val marketFieldSelect: Select<com.monistoWallet.modules.market.MarketField>
    )

}

data class MarketItemWrapper(
    val marketItem: com.monistoWallet.modules.market.MarketItem,
    val favorited: Boolean,
)