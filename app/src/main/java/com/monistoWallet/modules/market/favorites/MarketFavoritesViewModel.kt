package com.monistoWallet.modules.market.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.DataState
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.market.MarketViewItem
import com.monistoWallet.modules.market.category.MarketItemWrapper
import com.monistoWallet.modules.market.favorites.MarketFavoritesModule.SelectorDialogState
import com.monistoWallet.modules.market.favorites.MarketFavoritesModule.ViewItem
import com.monistoWallet.ui.compose.Select
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MarketFavoritesViewModel(
    private val service: MarketFavoritesService,
    private val menuService: MarketFavoritesMenuService
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val marketFieldTypes = com.monistoWallet.modules.market.MarketField.values().toList()
    private var marketField: com.monistoWallet.modules.market.MarketField by menuService::marketField
    private var marketItemsWrapper: List<MarketItemWrapper> = listOf()

    private val marketFieldSelect: Select<com.monistoWallet.modules.market.MarketField>
        get() = Select(marketField, marketFieldTypes)

    private val sortingFieldSelect: Select<com.monistoWallet.modules.market.SortingField>
        get() = Select(service.sortingField, service.sortingFieldTypes)

    val viewStateLiveData = MutableLiveData<ViewState>(ViewState.Loading)
    val isRefreshingLiveData = MutableLiveData<Boolean>()
    val viewItemLiveData = MutableLiveData<ViewItem>()
    val sortingFieldSelectorStateLiveData = MutableLiveData<SelectorDialogState>()

    init {
        service.marketItemsObservable
            .subscribeIO { state ->
                when (state) {
                    is DataState.Success -> {
                        viewStateLiveData.postValue(ViewState.Success)
                        marketItemsWrapper = state.data
                        syncViewItem()
                    }
                    is DataState.Error -> {
                        viewStateLiveData.postValue(ViewState.Error(state.error))
                    }
                    DataState.Loading -> {}
                }
            }.let { disposables.add(it) }

        service.start()
    }

    private fun refreshWithMinLoadingSpinnerPeriod() {
        service.refresh()
        viewModelScope.launch {
            isRefreshingLiveData.postValue(true)
            delay(1000)
            isRefreshingLiveData.postValue(false)
        }
    }

    private fun syncViewItem() {
        viewItemLiveData.postValue(
            ViewItem(
                sortingFieldSelect,
                marketFieldSelect,
                marketItemsWrapper.map {
                    MarketViewItem.create(it.marketItem, marketField, true)
                }
            )
        )
    }

    fun refresh() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onErrorClick() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onClickSortingField() {
        sortingFieldSelectorStateLiveData.postValue(SelectorDialogState.Opened(sortingFieldSelect))
    }

    fun onSelectSortingField(sortingField: com.monistoWallet.modules.market.SortingField) {
        service.sortingField = sortingField
        sortingFieldSelectorStateLiveData.postValue(SelectorDialogState.Closed)
    }

    fun onSelectMarketField(marketField: com.monistoWallet.modules.market.MarketField) {
        this.marketField = marketField

        syncViewItem()
    }

    fun onSortingFieldDialogDismiss() {
        sortingFieldSelectorStateLiveData.postValue(SelectorDialogState.Closed)
    }

    override fun onCleared() {
        disposables.clear()
        service.stop()
    }

    fun removeFromFavorites(uid: String) {
        service.removeFavorite(uid)
    }
}
