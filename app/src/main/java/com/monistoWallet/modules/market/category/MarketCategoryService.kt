package com.monistoWallet.modules.market.category

import com.monistoWallet.core.imageUrl
import com.monistoWallet.core.managers.CurrencyManager
import com.monistoWallet.core.managers.LanguageManager
import com.monistoWallet.core.managers.MarketFavoritesManager
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.DataState
import com.monistoWallet.modules.market.MarketItem
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.TopMarket
import com.wallet0x.marketkit.models.CoinCategory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class MarketCategoryService(
    private val marketCategoryRepository: MarketCategoryRepository,
    private val currencyManager: CurrencyManager,
    private val languageManager: LanguageManager,
    private val favoritesManager: MarketFavoritesManager,
    private val coinCategory: CoinCategory,
    topMarket: com.monistoWallet.modules.market.TopMarket = com.monistoWallet.modules.market.TopMarket.Top100,
    sortingField: com.monistoWallet.modules.market.SortingField = com.monistoWallet.modules.market.SortingField.HighestCap,
) {
    private var disposables = CompositeDisposable()
    private var favoriteDisposables = CompositeDisposable()

    private var marketItems: List<com.monistoWallet.modules.market.MarketItem> = listOf()

    val stateObservable: BehaviorSubject<DataState<List<MarketItemWrapper>>> = BehaviorSubject.create()

    var topMarket: com.monistoWallet.modules.market.TopMarket = topMarket
        private set

    val sortingFields = com.monistoWallet.modules.market.SortingField.values().toList()
    var sortingField: com.monistoWallet.modules.market.SortingField = sortingField
        private set

    val coinCategoryName: String get() = coinCategory.name
    val coinCategoryDescription: String get() = coinCategory.description[languageManager.currentLocaleTag]
        ?: coinCategory.description["en"]
        ?: coinCategory.description.keys.firstOrNull()
        ?: ""
    val coinCategoryImageUrl: String get() = coinCategory.imageUrl

    fun setSortingField(sortingField: com.monistoWallet.modules.market.SortingField) {
        this.sortingField = sortingField
        sync(false)
    }

    private fun sync(forceRefresh: Boolean) {
        disposables.clear()

        marketCategoryRepository
            .get(
                coinCategory.uid,
                topMarket.value,
                sortingField,
                topMarket.value,
                currencyManager.baseCurrency,
                forceRefresh
            )
            .subscribeIO({ items ->
                marketItems = items
                syncItems()
            }, {
                stateObservable.onNext(DataState.Error(it))
            }).let {
                disposables.add(it)
            }
    }

    private fun syncItems() {
        val favorites = favoritesManager.getAll().map { it.coinUid }
        val items = marketItems.map { MarketItemWrapper(it, favorites.contains(it.fullCoin.coin.uid)) }
        stateObservable.onNext(DataState.Success(items))
    }

    fun start() {
        sync(true)

        favoritesManager.dataUpdatedAsync
            .subscribeIO {
                syncItems()
            }.let {
                favoriteDisposables.add(it)
            }
    }

    fun refresh() {
        sync(true)
    }

    fun stop() {
        disposables.clear()
        favoriteDisposables.clear()
    }

    fun addFavorite(coinUid: String) {
        favoritesManager.add(coinUid)
    }

    fun removeFavorite(coinUid: String) {
        favoritesManager.remove(coinUid)
    }
}
