package com.monistoWallet.modules.market.topcoins

import com.monistoWallet.core.managers.CurrencyManager
import com.monistoWallet.core.managers.MarketFavoritesManager
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.DataState
import com.monistoWallet.modules.market.category.MarketItemWrapper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class MarketTopCoinsService(
    private val marketTopMoversRepository: MarketTopMoversRepository,
    private val currencyManager: CurrencyManager,
    private val favoritesManager: MarketFavoritesManager,
    topMarket: com.monistoWallet.modules.market.TopMarket = com.monistoWallet.modules.market.TopMarket.Top100,
    sortingField: com.monistoWallet.modules.market.SortingField = com.monistoWallet.modules.market.SortingField.HighestCap,
    private val marketField: com.monistoWallet.modules.market.MarketField,
) {
    private var disposables = CompositeDisposable()

    private var marketItems: List<com.monistoWallet.modules.market.MarketItem> = listOf()

    val stateObservable: BehaviorSubject<DataState<List<MarketItemWrapper>>> =
        BehaviorSubject.create()

    val topMarkets = com.monistoWallet.modules.market.TopMarket.values().toList()
    var topMarket: com.monistoWallet.modules.market.TopMarket = topMarket
        private set

    val sortingFields = com.monistoWallet.modules.market.SortingField.values().toList()
    var sortingField: com.monistoWallet.modules.market.SortingField = sortingField
        private set

    fun setSortingField(sortingField: com.monistoWallet.modules.market.SortingField) {
        this.sortingField = sortingField
        sync()
    }

    fun setTopMarket(topMarket: com.monistoWallet.modules.market.TopMarket) {
        this.topMarket = topMarket
        sync()
    }

    private fun sync() {
        disposables.clear()

        marketTopMoversRepository
            .get(
                topMarket.value,
                sortingField,
                topMarket.value,
                currencyManager.baseCurrency,
                marketField
            )
            .subscribeIO({
                marketItems = it
                syncItems()
            }, {
                stateObservable.onNext(DataState.Error(it))
            }).let {
                disposables.add(it)
            }
    }

    private fun syncItems() {
        val favorites = favoritesManager.getAll().map { it.coinUid }
        val items =
            marketItems.map { MarketItemWrapper(it, favorites.contains(it.fullCoin.coin.uid)) }
        stateObservable.onNext(DataState.Success(items))
    }

    fun start() {
        sync()

        favoritesManager.dataUpdatedAsync
            .subscribeIO {
                syncItems()
            }.let {
                disposables.add(it)
            }
    }

    fun refresh() {
        sync()
    }

    fun stop() {
        disposables.clear()
    }

    fun addFavorite(coinUid: String) {
        favoritesManager.add(coinUid)
    }

    fun removeFavorite(coinUid: String) {
        favoritesManager.remove(coinUid)
    }
}
