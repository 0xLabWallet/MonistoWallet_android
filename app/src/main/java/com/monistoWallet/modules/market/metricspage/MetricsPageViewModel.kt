package com.monistoWallet.modules.market.metricspage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.market.MarketViewItem
import com.monistoWallet.modules.metricchart.MetricsType
import com.monistoWallet.ui.compose.Select
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MetricsPageViewModel(
    private val service: MetricsPageService,
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val marketFields = com.monistoWallet.modules.market.MarketField.values().toList()
    private var marketField: com.monistoWallet.modules.market.MarketField
    private var marketItems: List<com.monistoWallet.modules.market.MarketItem> = listOf()

    val isRefreshingLiveData = MutableLiveData<Boolean>()
    val marketLiveData = MutableLiveData<MetricsPageModule.MarketData>()
    val viewStateLiveData = MutableLiveData<ViewState>(ViewState.Loading)
    var header = com.monistoWallet.modules.market.MarketModule.Header(
        title = Translator.getString(metricsType.title),
        description = Translator.getString(metricsType.description),
        icon = metricsType.headerIcon
    )

    val metricsType: MetricsType
        get() = service.metricsType

    init {
        marketField = when (metricsType) {
            MetricsType.Volume24h -> com.monistoWallet.modules.market.MarketField.Volume
            MetricsType.TotalMarketCap,
            MetricsType.DefiCap,
            MetricsType.BtcDominance,
            MetricsType.TvlInDefi -> com.monistoWallet.modules.market.MarketField.MarketCap
        }

        service.marketItemsObservable
            .subscribeIO { marketItemsDataState ->
                marketItemsDataState.viewState?.let {
                    viewStateLiveData.postValue(it)
                }

                marketItemsDataState?.dataOrNull?.let {
                    marketItems = it
                    syncMarketItems(it)
                }
            }
            .let { disposables.add(it) }

        service.start()
    }

    private fun syncMarketItems(marketItems: List<com.monistoWallet.modules.market.MarketItem>) {
        marketLiveData.postValue(marketData(marketItems))
    }

    private fun marketData(marketItems: List<com.monistoWallet.modules.market.MarketItem>): MetricsPageModule.MarketData {
        val menu = MetricsPageModule.Menu(service.sortDescending, Select(marketField, marketFields))
        val marketViewItems = marketItems.map { MarketViewItem.create(it, marketField) }
        return MetricsPageModule.MarketData(menu, marketViewItems)
    }

    private fun refreshWithMinLoadingSpinnerPeriod() {
        service.refresh()
        viewModelScope.launch {
            isRefreshingLiveData.postValue(true)
            delay(1000)
            isRefreshingLiveData.postValue(false)
        }
    }

    fun onToggleSortType() {
        service.sortDescending = !service.sortDescending
    }

    fun onSelectMarketField(marketField: com.monistoWallet.modules.market.MarketField) {
        this.marketField = marketField
        syncMarketItems(marketItems)
    }

    fun refresh() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onErrorClick() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    override fun onCleared() {
        service.stop()
        disposables.clear()
    }
}
