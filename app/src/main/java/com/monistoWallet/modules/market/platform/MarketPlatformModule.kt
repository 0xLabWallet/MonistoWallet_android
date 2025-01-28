package com.monistoWallet.modules.market.platform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.chart.ChartCurrencyValueFormatterShortened
import com.monistoWallet.modules.chart.ChartModule
import com.monistoWallet.modules.chart.ChartViewModel
import com.monistoWallet.modules.market.topplatforms.Platform
import com.monistoWallet.ui.compose.Select

object MarketPlatformModule {

    class Factory(private val platform: Platform) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                MarketPlatformViewModel::class.java -> {
                    val repository =
                        MarketPlatformCoinsRepository(platform, com.monistoWallet.core.App.marketKit, com.monistoWallet.core.App.currencyManager)
                    MarketPlatformViewModel(platform, repository, com.monistoWallet.core.App.marketFavoritesManager) as T
                }

                ChartViewModel::class.java -> {
                    val chartService =
                        PlatformChartService(platform, com.monistoWallet.core.App.currencyManager, com.monistoWallet.core.App.marketKit)
                    val chartNumberFormatter = ChartCurrencyValueFormatterShortened()
                    ChartModule.createViewModel(chartService, chartNumberFormatter) as T
                }
                else -> throw IllegalArgumentException()
            }
        }

    }

    data class Menu(
        val sortingFieldSelect: Select<com.monistoWallet.modules.market.SortingField>,
        val marketFieldSelect: Select<com.monistoWallet.modules.market.MarketField>
    )

}
