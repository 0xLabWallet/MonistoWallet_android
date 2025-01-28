package com.monistoWallet.modules.market.tvl

import com.monistoWallet.core.managers.CurrencyManager
import com.monistoWallet.entities.Currency
import com.monistoWallet.modules.chart.AbstractChartService
import com.monistoWallet.modules.chart.ChartPointsWrapper
import com.monistoWallet.ChartViewType
import com.wallet0x.marketkit.models.HsTimePeriod
import io.reactivex.Single

class TvlChartService(
    override val currencyManager: CurrencyManager,
    private val globalMarketRepository: GlobalMarketRepository
) : AbstractChartService() {

    override val initialChartInterval = HsTimePeriod.Day1

    override val chartIntervals = HsTimePeriod.values().toList()
    override val chartViewType = ChartViewType.Line

    var chain: TvlModule.Chain = TvlModule.Chain.All
        set(value) {
            field = value
            dataInvalidated()
        }

    override fun getItems(
        chartInterval: HsTimePeriod,
        currency: Currency
    ): Single<ChartPointsWrapper> {
        val chainParam = if (chain == TvlModule.Chain.All) "" else chain.name
        return globalMarketRepository.getTvlGlobalMarketPoints(
            chainParam,
            currency.code,
            chartInterval
        ).map {
            ChartPointsWrapper(it)
        }
    }
}
