package com.monistoWallet.modules.market.favorites

import com.monistoWallet.core.ILocalStorage
import com.monistoWallet.modules.market.MarketField
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.widgets.MarketWidgetManager

class MarketFavoritesMenuService(
    private val localStorage: ILocalStorage,
    private val marketWidgetManager: MarketWidgetManager
) {

    var sortingField: com.monistoWallet.modules.market.SortingField
        get() = localStorage.marketFavoritesSortingField ?: com.monistoWallet.modules.market.SortingField.HighestCap
        set(value) {
            localStorage.marketFavoritesSortingField = value
            marketWidgetManager.updateWatchListWidgets()
        }

    var marketField: com.monistoWallet.modules.market.MarketField
        get() = localStorage.marketFavoritesMarketField ?: com.monistoWallet.modules.market.MarketField.PriceDiff
        set(value) {
            localStorage.marketFavoritesMarketField = value
            marketWidgetManager.updateWatchListWidgets()
        }
}
