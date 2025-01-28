package com.monistoWallet.modules.market.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.market.MarketField
import com.monistoWallet.modules.market.MarketViewItem
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.ui.compose.Select
import javax.annotation.concurrent.Immutable

object MarketFavoritesModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = MarketFavoritesRepository(com.monistoWallet.core.App.marketKit, com.monistoWallet.core.App.marketFavoritesManager)
            val menuService = MarketFavoritesMenuService(com.monistoWallet.core.App.localStorage, com.monistoWallet.core.App.marketWidgetManager)
            val service = MarketFavoritesService(repository, menuService, com.monistoWallet.core.App.currencyManager, com.monistoWallet.core.App.backgroundManager)
            return MarketFavoritesViewModel(service, menuService) as T
        }
    }

    @Immutable
    data class ViewItem(
        val sortingFieldSelect: Select<com.monistoWallet.modules.market.SortingField>,
        val marketFieldSelect: Select<com.monistoWallet.modules.market.MarketField>,
        val marketItems: List<MarketViewItem>
    )

    sealed class SelectorDialogState {
        object Closed : SelectorDialogState()
        class Opened(val select: Select<com.monistoWallet.modules.market.SortingField>) : SelectorDialogState()
    }
}
