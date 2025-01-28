package com.monistoWallet.modules.market.category

import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.entities.Currency
import com.monistoWallet.modules.market.MarketItem
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.sort
import io.reactivex.Single
import kotlin.math.min

class MarketCategoryRepository(
    private val marketKit: MarketKitWrapper,
) {
    @Volatile
    private var cache: List<com.monistoWallet.modules.market.MarketItem> = listOf()

    @Volatile
    private var cacheTimestamp: Long = 0
    private val cacheValidPeriodInMillis = 5_000 // 5 seconds

    @Synchronized
    private fun getMarketItems(coinCategoryUid: String, forceRefresh: Boolean, baseCurrency: Currency): List<com.monistoWallet.modules.market.MarketItem> =
        if (forceRefresh && (cacheTimestamp + cacheValidPeriodInMillis < System.currentTimeMillis()) || cache.isEmpty()) {
            val marketInfoList = marketKit.marketInfosSingle(coinCategoryUid, baseCurrency.code, "market_category").blockingGet()

            val marketItems = marketInfoList.map { marketInfo ->
                com.monistoWallet.modules.market.MarketItem.createFromCoinMarket(marketInfo, baseCurrency)
            }
            cache = marketItems
            cacheTimestamp = System.currentTimeMillis()

            marketItems
        } else {
            cache
        }

    fun get(
        coinCategoryUid: String,
        size: Int,
        sortingField: com.monistoWallet.modules.market.SortingField,
        limit: Int,
        baseCurrency: Currency,
        forceRefresh: Boolean
    ): Single<List<com.monistoWallet.modules.market.MarketItem>> =
        Single.create { emitter ->

            try {
                val marketItems = getMarketItems(coinCategoryUid, forceRefresh, baseCurrency)
                val sortedMarketItems = marketItems
                    .subList(0, min(marketItems.size, size))
                    .sort(sortingField)
                    .subList(0, min(marketItems.size, limit))

                emitter.onSuccess(sortedMarketItems)
            } catch (error: Throwable) {
                emitter.onError(error)
            }
        }
}
