package com.monistoWallet.modules.coin.reports

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.DataState
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.coin.reports.CoinReportsModule.ReportViewItem
import com.monistoWallet.core.helpers.DateHelper
import com.wallet0x.marketkit.models.CoinReport
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CoinReportsViewModel(
    private val service: CoinReportsService
) : ViewModel() {
    private val disposables = CompositeDisposable()

    val viewStateLiveData = MutableLiveData<ViewState>(ViewState.Loading)
    val isRefreshingLiveData = MutableLiveData<Boolean>()
    val reportViewItemsLiveData = MutableLiveData<List<ReportViewItem>>()

    init {
        service.stateObservable
            .subscribeIO({ state ->
                when (state) {
                    is DataState.Success -> {
                        viewStateLiveData.postValue(ViewState.Success)

                        sync(state.data)
                    }
                    is DataState.Error -> {
                        viewStateLiveData.postValue(ViewState.Error(state.error))
                    }
                    DataState.Loading -> {}
                }
            }, {
                viewStateLiveData.postValue(ViewState.Error(it))
            }).let {
                disposables.add(it)
            }

        service.start()
    }

    fun refresh() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onErrorClick() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    override fun onCleared() {
        disposables.clear()
        service.stop()
    }

    private fun refreshWithMinLoadingSpinnerPeriod() {
        service.refresh()
        viewModelScope.launch {
            isRefreshingLiveData.postValue(true)
            delay(1000)
            isRefreshingLiveData.postValue(false)
        }
    }

    private fun sync(reports: List<CoinReport>) {
        reportViewItemsLiveData.postValue(reports.map { viewItem(it) })
    }

    private fun viewItem(report: CoinReport): ReportViewItem {
        return ReportViewItem(
            author = report.author,
            title = report.title,
            body = report.body,
            date = DateHelper.formatDate(report.date, "MMM dd, yyyy"),
            url = report.url
        )
    }
}
