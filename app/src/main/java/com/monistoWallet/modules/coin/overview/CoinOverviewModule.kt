package com.monistoWallet.modules.coin.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.chart.ChartCurrencyValueFormatterSignificant
import com.monistoWallet.modules.chart.ChartModule
import com.monistoWallet.modules.chart.ChartViewModel
import com.monistoWallet.modules.coin.*
import com.wallet0x.marketkit.models.FullCoin
import com.wallet0x.marketkit.models.MarketInfoOverview
import com.wallet0x.marketkit.models.Token

object CoinOverviewModule {

    class Factory(private val fullCoin: FullCoin, private val apiTag: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            return when (modelClass) {
                CoinOverviewViewModel::class.java -> {
                    val currency = com.monistoWallet.core.App.currencyManager.baseCurrency
                    val service = CoinOverviewService(
                        fullCoin,
                        apiTag,
                        com.monistoWallet.core.App.marketKit,
                        com.monistoWallet.core.App.currencyManager,
                        com.monistoWallet.core.App.appConfigProvider,
                        com.monistoWallet.core.App.languageManager
                    )

                    CoinOverviewViewModel(
                        service,
                        CoinViewFactory(currency, com.monistoWallet.core.App.numberFormatter),
                        com.monistoWallet.core.App.walletManager,
                        com.monistoWallet.core.App.accountManager,
                        com.monistoWallet.core.App.chartIndicatorManager
                    ) as T
                }
                ChartViewModel::class.java -> {
                    val chartService = CoinOverviewChartService(
                        com.monistoWallet.core.App.marketKit,
                        com.monistoWallet.core.App.currencyManager,
                        fullCoin.coin.uid,
                        com.monistoWallet.core.App.chartIndicatorManager
                    )
                    val chartNumberFormatter = ChartCurrencyValueFormatterSignificant()
                    ChartModule.createViewModel(chartService, chartNumberFormatter) as T
                }
                else -> throw IllegalArgumentException()
            }
        }

    }
}

data class CoinOverviewItem(
    val coinCode: String,
    val marketInfoOverview: MarketInfoOverview,
    val guideUrl: String?,
)

data class TokenVariant(
    val value: String,
    val copyValue: String?,
    val imgUrl: String,
    val explorerUrl: String?,
    val name: String?,
    val token: Token,
    val canAddToWallet: Boolean,
    val inWallet: Boolean,
) {
}

data class HudMessage(
    val text: Int,
    val type: HudMessageType,
    val iconRes: Int? = null
)

enum class HudMessageType{
    Success, Error
}

data class CoinOverviewViewItem(
    val roi: List<RoiViewItem>,
    val links: List<CoinLink>,
    val about: String,
    val marketData: MutableList<CoinDataItem>,
    val marketCapRank: Int?
)