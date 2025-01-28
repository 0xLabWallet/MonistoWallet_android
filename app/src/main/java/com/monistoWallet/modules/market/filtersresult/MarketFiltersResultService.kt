package com.monistoWallet.modules.market.filtersresult

import com.monistoWallet.core.managers.MarketFavoritesManager
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.DataState
import com.monistoWallet.modules.market.category.MarketCategoryModule
import com.monistoWallet.modules.market.category.MarketItemWrapper
import com.monistoWallet.modules.market.filters.IMarketListFetcher
import com.monistoWallet.modules.market.sort
import com.monistoWallet.ui.compose.Select
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class MarketFiltersResultService(
    private val fetcher: IMarketListFetcher,
    private val favoritesManager: MarketFavoritesManager,
) {
    val stateObservable: BehaviorSubject<DataState<List<MarketItemWrapper>>> =
        BehaviorSubject.create()

    var marketItems: List<com.monistoWallet.modules.market.MarketItem> = listOf()

    val sortingFields = com.monistoWallet.modules.market.SortingField.values().toList()
    private val marketFields = com.monistoWallet.modules.market.MarketField.values().toList()
    var sortingField = com.monistoWallet.modules.market.SortingField.HighestCap
    var marketField = com.monistoWallet.modules.market.MarketField.PriceDiff

    val menu: MarketCategoryModule.Menu
        get() = MarketCategoryModule.Menu(
            Select(sortingField, sortingFields),
            Select(marketField, marketFields)
        )

    private var fetchDisposable: Disposable? = null
    private var favoriteDisposable: Disposable? = null

    fun start() {
        fetch()

        favoritesManager.dataUpdatedAsync
            .subscribeIO {
                syncItems()
            }.let {
                favoriteDisposable = it
            }
    }

    fun stop() {
        favoriteDisposable?.dispose()
        fetchDisposable?.dispose()
    }

    fun refresh() {
        fetch()
    }

    fun updateSortingField(sortingField: com.monistoWallet.modules.market.SortingField) {
        this.sortingField = sortingField
        syncItems()
    }

    fun addFavorite(coinUid: String) {
        favoritesManager.add(coinUid)
    }

    fun removeFavorite(coinUid: String) {
        favoritesManager.remove(coinUid)
    }

    private fun fetch() {
        fetchDisposable?.dispose()

        fetcher.fetchAsync()
            .subscribeIO({
                marketItems = it
                syncItems()
            }, {
                stateObservable.onNext(DataState.Error(it))
            }).let {
                fetchDisposable = it
            }
    }

    private fun syncItems() {
        val favorites = favoritesManager.getAll().map { it.coinUid }

        val items = marketItems
            .sort(sortingField)
            .map { MarketItemWrapper(it, favorites.contains(it.fullCoin.coin.uid)) }

        stateObservable.onNext(DataState.Success(items))
    }

}
