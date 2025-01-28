package com.monistoWallet.modules.market.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.wallet0x.marketkit.models.CoinCategory
import com.wallet0x.marketkit.models.FullCoin
import java.math.BigDecimal
import javax.annotation.concurrent.Immutable

object MarketSearchModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MarketSearchViewModel(
                com.monistoWallet.core.App.marketFavoritesManager,
                MarketSearchService(com.monistoWallet.core.App.marketKit),
                MarketDiscoveryService(com.monistoWallet.core.App.marketKit, com.monistoWallet.core.App.localStorage),
            ) as T
        }
    }

    sealed class DiscoveryItem {
        object TopCoins : DiscoveryItem()

        class Category(
            val coinCategory: CoinCategory,
            val marketData: CategoryMarketData? = null
        ) : DiscoveryItem()
    }

    @Immutable
    class CoinItem(val fullCoin: FullCoin, val favourited: Boolean)

    data class CategoryMarketData(
        val marketCap: String? = null,
        val diff: BigDecimal? = null
    )

}
