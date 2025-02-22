package com.monistoWallet.modules.coin.treasuries

import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.modules.coin.treasuries.CoinTreasuriesModule.TreasuryTypeFilter
import com.wallet0x.marketkit.models.CoinTreasury
import com.wallet0x.marketkit.models.CoinTreasury.TreasuryType
import io.reactivex.Single

class CoinTreasuriesRepository(
    private val marketKit: MarketKitWrapper
) {
    private var cache: List<CoinTreasury> = listOf()

    private fun getCoinTreasuries(coinUid: String, currencyCode: String, forceRefresh: Boolean): List<CoinTreasury> {
        if (forceRefresh || cache.isEmpty()) {
            cache = marketKit.treasuriesSingle(coinUid, currencyCode).blockingGet()
        }
        return cache
    }

    fun coinTreasuriesSingle(
        coinUid: String,
        currencyCode: String,
        treasuryType: TreasuryTypeFilter,
        sortDescending: Boolean,
        forceRefresh: Boolean
    ): Single<List<CoinTreasury>> =
        Single.create { emitter ->
            try {
                val treasuries = getCoinTreasuries(coinUid, currencyCode, forceRefresh)
                val filteredTreasuries = treasuries.filter {
                    when (treasuryType) {
                        TreasuryTypeFilter.All -> true
                        TreasuryTypeFilter.Public -> it.type == TreasuryType.Public
                        TreasuryTypeFilter.Private -> it.type == TreasuryType.Private
                        TreasuryTypeFilter.ETF -> it.type == TreasuryType.Etf
                    }
                }
                val sortedTreasuries = if (sortDescending) {
                    filteredTreasuries.sortedByDescending { it.amount }
                } else {
                    filteredTreasuries.sortedBy { it.amount }
                }
                emitter.onSuccess(sortedTreasuries)
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }
}
