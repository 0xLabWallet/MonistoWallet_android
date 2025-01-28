package com.monistoWallet.modules.market.topcoins

import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.entities.Currency
import com.monistoWallet.modules.market.MarketField
import com.monistoWallet.modules.market.MarketItem
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.sort
import com.wallet0x.marketkit.models.TopMovers
import io.reactivex.Single
import java.lang.Integer.min

class MarketTopMoversRepository(
    private val marketKit: MarketKitWrapper
) {

    fun getTopMovers(baseCurrency: Currency): Single<TopMovers> =
        marketKit.topMoversSingle(baseCurrency.code)

    fun get(
        size: Int,
        sortingField: com.monistoWallet.modules.market.SortingField,
        limit: Int,
        baseCurrency: Currency,
        marketField: com.monistoWallet.modules.market.MarketField
    ): Single<List<com.monistoWallet.modules.market.MarketItem>> =
        Single.create { emitter ->
            try {
                val appTag = "market_top_${size}_${sortingField.name}_${marketField.name}"
                val marketInfoList = marketKit.marketInfosSingle(size, baseCurrency.code, false, appTag).blockingGet()
                val marketItemList = marketInfoList.map { marketInfo ->
                    com.monistoWallet.modules.market.MarketItem.createFromCoinMarket(
                        marketInfo,
                        baseCurrency,
                    )
                }

                val sortedMarketItems = marketItemList
                    .subList(0, min(marketInfoList.size, size))
                    .sort(sortingField)
                    .subList(0, min(marketInfoList.size, limit))

                emitter.onSuccess(sortedMarketItems)
            } catch (error: Throwable) {
                emitter.onError(error)
            }
        }

}
