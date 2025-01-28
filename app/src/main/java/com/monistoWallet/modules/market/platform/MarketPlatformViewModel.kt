package com.monistoWallet.modules.market.platform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.R
import com.monistoWallet.core.iconUrl
import com.monistoWallet.core.managers.MarketFavoritesManager
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.market.*
import com.monistoWallet.modules.market.topcoins.SelectorDialogState
import com.monistoWallet.modules.market.topplatforms.Platform
import com.monistoWallet.ui.compose.Select
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MarketPlatformViewModel(
    platform: Platform,
    private val repository: MarketPlatformCoinsRepository,
    private val favoritesManager: MarketFavoritesManager,
) : ViewModel() {

    private val sortingFields = com.monistoWallet.modules.market.SortingField.values().toList()

    private val marketFields = com.monistoWallet.modules.market.MarketField.values().toList()

    var sortingField: com.monistoWallet.modules.market.SortingField = com.monistoWallet.modules.market.SortingField.HighestCap
        private set

    var marketField: com.monistoWallet.modules.market.MarketField = com.monistoWallet.modules.market.MarketField.PriceDiff
        private set

    var viewState by mutableStateOf<ViewState>(ViewState.Loading)
        private set

    var viewItems by mutableStateOf<List<MarketViewItem>>(listOf())
        private set

    val header = com.monistoWallet.modules.market.MarketModule.Header(
        Translator.getString(R.string.MarketPlatformCoins_PlatformEcosystem, platform.name),
        Translator.getString(
            R.string.MarketPlatformCoins_PlatformEcosystemDescription,
            platform.name
        ),
        com.monistoWallet.modules.market.ImageSource.Remote(platform.iconUrl)
    )

    var isRefreshing by mutableStateOf(false)
        private set

    var selectorDialogState by mutableStateOf<SelectorDialogState>(SelectorDialogState.Closed)
        private set

    var menu by mutableStateOf(
        MarketPlatformModule.Menu(
            sortingFieldSelect = Select(sortingField, sortingFields),
            marketFieldSelect = Select(marketField, marketFields)
        )
    )
        private set

    init {
        sync()
    }

    fun refresh() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onErrorClick() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onSelectSortingField(sortingField: com.monistoWallet.modules.market.SortingField) {
        this.sortingField = sortingField
        selectorDialogState = SelectorDialogState.Closed
        sync()
        updateMenu()
    }

    fun onSelectMarketField(marketField: com.monistoWallet.modules.market.MarketField) {
        this.marketField = marketField
        sync()
        updateMenu()
    }

    fun onSelectorDialogDismiss() {
        selectorDialogState = SelectorDialogState.Closed
    }

    fun showSelectorMenu() {
        selectorDialogState = SelectorDialogState.Opened(
            Select(sortingField, sortingFields)
        )
    }

    fun onAddFavorite(coinUid: String) {
        favoritesManager.add(coinUid)
        sync()
    }

    fun onRemoveFavorite(coinUid: String) {
        favoritesManager.remove(coinUid)
        sync()
    }

    private fun updateMenu() {
        menu = MarketPlatformModule.Menu(
            sortingFieldSelect = Select(sortingField, sortingFields),
            marketFieldSelect = Select(marketField, marketFields)
        )
    }

    private fun sync(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                val marketItems = repository.get(sortingField, forceRefresh)
                val favorites = favoritesManager.getAll().map { it.coinUid }
                viewItems = marketItems?.map {
                    MarketViewItem.create(
                        it,
                        marketField,
                        favorites.contains(it.fullCoin.coin.uid)
                    )
                } ?: listOf()
                viewState = ViewState.Success
            } catch (e: Throwable) {
                viewState = ViewState.Error(e)
            }
        }
    }

    private fun refreshWithMinLoadingSpinnerPeriod() {
        viewModelScope.launch {
            sync(true)

            isRefreshing = true
            delay(1000)
            isRefreshing = false
        }
    }
}
