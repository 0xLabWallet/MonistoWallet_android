package com.monistoWallet.modules.market.metricspage

import com.monistoWallet.core.managers.CurrencyManager
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.DataState
import com.monistoWallet.modules.market.tvl.GlobalMarketRepository
import com.monistoWallet.modules.metricchart.MetricsType
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class MetricsPageService(
    val metricsType: MetricsType,
    private val currencyManager: CurrencyManager,
    private val globalMarketRepository: GlobalMarketRepository
) {
    private var currencyManagerDisposable: Disposable? = null
    private var globalMarketPointsDisposable: Disposable? = null
    private var marketDataDisposable: Disposable? = null

    val currency by currencyManager::baseCurrency

    val marketItemsObservable: BehaviorSubject<DataState<List<com.monistoWallet.modules.market.MarketItem>>> =
        BehaviorSubject.create()

    var sortDescending: Boolean = true
        set(value) {
            field = value
            syncMarketItems()
        }

    private fun sync() {
        syncMarketItems()
    }

    private fun syncMarketItems() {
        marketDataDisposable?.dispose()
        globalMarketRepository.getMarketItems(currency, sortDescending, metricsType)
            .subscribeIO({
                marketItemsObservable.onNext(DataState.Success(it))
            }, {
                marketItemsObservable.onNext(DataState.Error(it))
            })
            .let { marketDataDisposable = it }
    }

    fun start() {
        currencyManager.baseCurrencyUpdatedSignal
            .subscribeIO { sync() }
            .let { currencyManagerDisposable = it }

        sync()
    }

    fun refresh() {
        sync()
    }

    fun stop() {
        currencyManagerDisposable?.dispose()
        globalMarketPointsDisposable?.dispose()
        marketDataDisposable?.dispose()
    }
}
