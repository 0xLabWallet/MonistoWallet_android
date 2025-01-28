package com.monistoWallet.modules.market.platform

import com.monistoWallet.core.managers.CurrencyManager
import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.entities.Currency
import com.monistoWallet.modules.chart.AbstractChartService
import com.monistoWallet.modules.chart.ChartPointsWrapper
import com.monistoWallet.modules.market.topplatforms.Platform
import com.monistoWallet.ChartViewType
import com.monistoWallet.models.ChartPoint
import com.wallet0x.marketkit.models.HsTimePeriod
import io.reactivex.Single

class PlatformChartService(
    private val platform: Platform,
    override val currencyManager: CurrencyManager,
    private val marketKit: MarketKitWrapper,
) : AbstractChartService() {

    override val initialChartInterval = HsTimePeriod.Week1
    override val chartIntervals = listOf(HsTimePeriod.Week1, HsTimePeriod.Month1, HsTimePeriod.Month3)
    override val chartViewType = ChartViewType.Line

    override fun getItems(
        chartInterval: HsTimePeriod,
        currency: Currency
    ): Single<ChartPointsWrapper> = try {
        marketKit.topPlatformMarketCapPointsSingle(platform.uid, chartInterval, currency.code)
            .map { info -> info.map { ChartPoint(it.marketCap.toFloat(), it.timestamp) } }
            .map { ChartPointsWrapper(it) }
    } catch (e: Exception) {
        Single.error(e)
    }

}
