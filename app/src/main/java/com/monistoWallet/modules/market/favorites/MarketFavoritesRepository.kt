package com.monistoWallet.modules.market.favorites

import com.monistoWallet.core.managers.MarketFavoritesManager
import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.entities.Currency
import com.monistoWallet.modules.market.MarketItem
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.sort
import io.reactivex.Single

class MarketFavoritesRepository(
    private val marketKit: MarketKitWrapper,
    private val manager: MarketFavoritesManager
) {
    private var cache: List<com.monistoWallet.modules.market.MarketItem> = listOf()

    val dataUpdatedObservable by manager::dataUpdatedAsync

    private fun getFavorites(
        forceRefresh: Boolean,
        currency: Currency
    ): List<com.monistoWallet.modules.market.MarketItem> =
        if (forceRefresh) {
            val favoriteCoins = manager.getAll()
            var marketItems = listOf<com.monistoWallet.modules.market.MarketItem>()
            if (favoriteCoins.isNotEmpty()) {
                val favoriteCoinUids = favoriteCoins.map { it.coinUid }
                marketItems = marketKit.marketInfosSingle(favoriteCoinUids, currency.code, "watchlist").blockingGet()
                    .map { marketInfo ->
                        com.monistoWallet.modules.market.MarketItem.createFromCoinMarket(marketInfo, currency)
                    }
            }
            cache = marketItems
            marketItems
        } else {
            cache
        }

    fun get(
        sortingField: com.monistoWallet.modules.market.SortingField,
        currency: Currency,
        forceRefresh: Boolean
    ): Single<List<com.monistoWallet.modules.market.MarketItem>> =
        Single.create { emitter ->
            try {
                val marketItems = getFavorites(forceRefresh, currency)
                emitter.onSuccess(
                    marketItems.sort(sortingField)
                )
            } catch (error: Throwable) {
                emitter.onError(error)
            }
        }

    fun removeFavorite(uid: String) {
        manager.remove(uid)
    }
}
