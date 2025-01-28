package com.monistoWallet.modules.market.platform

import com.monistoWallet.core.managers.CurrencyManager
import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.modules.market.MarketItem
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.sort
import com.monistoWallet.modules.market.topplatforms.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class MarketPlatformCoinsRepository(
    private val platform: Platform,
    private val marketKit: MarketKitWrapper,
    private val currencyManager: CurrencyManager
) {
    private var itemsCache: List<com.monistoWallet.modules.market.MarketItem>? = null

    suspend fun get(
        sortingField: com.monistoWallet.modules.market.SortingField,
        forceRefresh: Boolean,
        limit: Int? = null,
    ) = withContext(Dispatchers.IO) {
        val currentCache = itemsCache

        val items = if (forceRefresh || currentCache == null) {
            val marketInfoItems = marketKit
                .topPlatformCoinListSingle(platform.uid, currencyManager.baseCurrency.code, "market_top_platforms")
                .await()

            marketInfoItems.map { marketInfo ->
                com.monistoWallet.modules.market.MarketItem.createFromCoinMarket(marketInfo, currencyManager.baseCurrency)
            }
        } else {
            currentCache
        }

        itemsCache = items

        itemsCache?.sort(sortingField)?.let { sortedList ->
            limit?.let { sortedList.take(it) } ?: sortedList
        }
    }

}
