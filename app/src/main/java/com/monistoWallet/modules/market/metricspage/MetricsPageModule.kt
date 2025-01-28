package com.monistoWallet.modules.market.metricspage

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.chart.ChartCurrencyValueFormatterShortened
import com.monistoWallet.modules.chart.ChartModule
import com.monistoWallet.modules.chart.ChartViewModel
import com.monistoWallet.modules.market.MarketViewItem
import com.monistoWallet.modules.market.tvl.GlobalMarketRepository
import com.monistoWallet.modules.metricchart.MetricsType
import com.monistoWallet.ui.compose.Select

object MetricsPageModule {

    @Suppress("UNCHECKED_CAST")
    class Factory(private val metricsType: MetricsType) : ViewModelProvider.Factory {
        private val globalMarketRepository by lazy {
            GlobalMarketRepository(com.monistoWallet.core.App.marketKit, "market_metrics")
        }

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                MetricsPageViewModel::class.java -> {
                    val service = MetricsPageService(metricsType, com.monistoWallet.core.App.currencyManager, globalMarketRepository)
                    MetricsPageViewModel(service) as T
                }
                ChartViewModel::class.java -> {
                    val chartService = MetricsPageChartService(com.monistoWallet.core.App.currencyManager, metricsType, globalMarketRepository)
                    val chartNumberFormatter = ChartCurrencyValueFormatterShortened()
                    ChartModule.createViewModel(chartService, chartNumberFormatter) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    @Immutable
    data class MarketData(
        val menu: Menu,
        val marketViewItems: List<MarketViewItem>
    )

    @Immutable
    data class Menu(
        val sortDescending: Boolean,
        val marketFieldSelect: Select<com.monistoWallet.modules.market.MarketField>
    )
}

