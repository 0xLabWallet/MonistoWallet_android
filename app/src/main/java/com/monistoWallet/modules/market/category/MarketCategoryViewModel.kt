package com.monistoWallet.modules.market.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.DataState
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.market.*
import com.monistoWallet.modules.market.topcoins.SelectorDialogState
import com.monistoWallet.ui.compose.Select
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MarketCategoryViewModel(
    private val service: MarketCategoryService,
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val marketFields = com.monistoWallet.modules.market.MarketField.values().toList()
    private var marketItems: List<MarketItemWrapper> = listOf()
    private var marketField = com.monistoWallet.modules.market.MarketField.PriceDiff

    val headerLiveData = MutableLiveData<com.monistoWallet.modules.market.MarketModule.Header>()
    val menuLiveData = MutableLiveData<MarketCategoryModule.Menu>()
    val viewStateLiveData = MutableLiveData<ViewState>()
    val viewItemsLiveData = MutableLiveData<List<MarketViewItem>>()
    val isRefreshingLiveData = MutableLiveData<Boolean>()
    val selectorDialogStateLiveData = MutableLiveData<SelectorDialogState>()

    init {
        syncHeader()
        syncMenu()

        service.stateObservable
            .subscribeIO {
                syncState(it)
            }.let {
                disposables.add(it)
            }

        service.start()
    }

    private fun syncState(state: DataState<List<MarketItemWrapper>>) {
        viewStateLiveData.postValue(state.viewState)

        state.dataOrNull?.let {
            marketItems = it

            syncMarketViewItems()
        }

        syncMenu()
    }

    private fun syncHeader() {
        headerLiveData.postValue(
            com.monistoWallet.modules.market.MarketModule.Header(
                service.coinCategoryName,
                service.coinCategoryDescription,
                com.monistoWallet.modules.market.ImageSource.Remote(service.coinCategoryImageUrl)
            )
        )
    }

    private fun syncMenu() {
        menuLiveData.postValue(
            MarketCategoryModule.Menu(
                Select(service.sortingField, service.sortingFields),
                Select(marketField, marketFields)
            )
        )
    }

    private fun syncMarketViewItems() {
        viewItemsLiveData.postValue(
            marketItems.map {
                MarketViewItem.create(it.marketItem, marketField, it.favorited)
            }
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

    fun onSelectSortingField(sortingField: com.monistoWallet.modules.market.SortingField) {
        service.setSortingField(sortingField)
        selectorDialogStateLiveData.postValue(SelectorDialogState.Closed)
    }

    fun onSelectMarketField(marketField: com.monistoWallet.modules.market.MarketField) {
        this.marketField = marketField

        syncMarketViewItems()
        syncMenu()
    }

    fun onSelectorDialogDismiss() {
        selectorDialogStateLiveData.postValue(SelectorDialogState.Closed)
    }

    fun showSelectorMenu() {
        selectorDialogStateLiveData.postValue(
            SelectorDialogState.Opened(Select(service.sortingField, service.sortingFields))
        )
    }

    fun refresh(){
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onErrorClick() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    override fun onCleared() {
        service.stop()
        disposables.clear()
    }

    fun onAddFavorite(uid: String) {
        service.addFavorite(uid)
    }

    fun onRemoveFavorite(uid: String) {
        service.removeFavorite(uid)
    }
}
