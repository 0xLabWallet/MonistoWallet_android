package com.monistoWallet.modules.market.tvl

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.market.tvl.TvlModule.SelectorDialogState
import com.monistoWallet.modules.market.tvl.TvlModule.TvlDiffType
import com.monistoWallet.modules.metricchart.MetricsType
import com.monistoWallet.ui.compose.Select
import com.wallet0x.marketkit.models.HsTimePeriod
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TvlViewModel(
    private val service: TvlService,
    private val tvlViewItemFactory: TvlViewItemFactory,
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private var tvlDiffType: TvlDiffType = TvlDiffType.Percent
        set(value) {
            field = value
            tvlDiffTypeLiveData.postValue(value)
        }
    private var tvlItems: List<TvlModule.MarketTvlItem> = listOf()
    private val metricsType = MetricsType.TvlInDefi

    val isRefreshingLiveData = MutableLiveData<Boolean>()
    val tvlLiveData = MutableLiveData<TvlModule.TvlData>()
    val tvlDiffTypeLiveData = MutableLiveData(tvlDiffType)
    val viewStateLiveData = MutableLiveData<ViewState>(ViewState.Loading)
    val chainSelectorDialogStateLiveData = MutableLiveData<SelectorDialogState>()

    var header = com.monistoWallet.modules.market.MarketModule.Header(
        title = Translator.getString(metricsType.title),
        description = Translator.getString(metricsType.description),
        icon = metricsType.headerIcon
    )

    init {
        service.marketTvlItemsObservable
            .subscribeIO { tvlItemsDataState ->
                tvlItemsDataState.viewState?.let {
                    viewStateLiveData.postValue(it)
                }

                tvlItemsDataState.dataOrNull?.let {
                    tvlItems = it
                    syncTvlItems(it)
                }
            }
            .let { disposables.add(it) }

        service.start()
    }

    private fun syncTvlItems(tvlItems: List<TvlModule.MarketTvlItem>) {
        tvlLiveData.postValue(
            tvlViewItemFactory.tvlData(service.chain, service.chains, service.sortDescending, tvlItems)
        )
    }

    private fun refreshWithMinLoadingSpinnerPeriod() {
        service.refresh()
        viewModelScope.launch {
            isRefreshingLiveData.postValue(true)
            delay(1000)
            isRefreshingLiveData.postValue(false)
        }
    }

    fun onSelectChain(chain: TvlModule.Chain) {
        service.chain = chain
        chainSelectorDialogStateLiveData.postValue(SelectorDialogState.Closed)
    }

    fun onToggleSortType() {
        service.sortDescending = !service.sortDescending
    }

    fun onToggleTvlDiffType() {
        tvlDiffType = if (tvlDiffType == TvlDiffType.Percent) TvlDiffType.Currency else TvlDiffType.Percent
    }

    fun onClickChainSelector() {
        chainSelectorDialogStateLiveData.postValue(
            SelectorDialogState.Opened(Select(service.chain, service.chains))
        )
    }

    fun onChainSelectorDialogDismiss() {
        chainSelectorDialogStateLiveData.postValue(SelectorDialogState.Closed)
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

    fun onSelectChartInterval(chartInterval: HsTimePeriod?) {
        service.updateChartInterval(chartInterval)
    }
}
