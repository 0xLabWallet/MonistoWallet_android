package com.monistoWallet.modules.market.topplatforms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.TimeDuration
import com.monistoWallet.modules.market.topcoins.SelectorDialogState
import com.monistoWallet.ui.compose.Select
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TopPlatformsViewModel(
    private val service: TopPlatformsService,
    timeDuration: com.monistoWallet.modules.market.TimeDuration?,
) : ViewModel() {

    private val sortingFields = listOf(
        com.monistoWallet.modules.market.SortingField.HighestCap,
        com.monistoWallet.modules.market.SortingField.LowestCap,
        com.monistoWallet.modules.market.SortingField.TopGainers,
        com.monistoWallet.modules.market.SortingField.TopLosers
    )

    var sortingField: com.monistoWallet.modules.market.SortingField = com.monistoWallet.modules.market.SortingField.HighestCap
        private set

    val periodOptions = com.monistoWallet.modules.market.TimeDuration.values().toList()

    var timePeriod = timeDuration ?: com.monistoWallet.modules.market.TimeDuration.OneDay
        private set

    val timePeriodSelect = Select(timePeriod, periodOptions)

    var sortingSelect by mutableStateOf(Select(sortingField, sortingFields))
        private set

    var viewItems by mutableStateOf<List<TopPlatformViewItem>>(listOf())
        private set

    var viewState by mutableStateOf<ViewState>(ViewState.Loading)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var selectorDialogState by mutableStateOf<SelectorDialogState>(SelectorDialogState.Closed)
        private set


    init {
        viewModelScope.launch {
            sync(false)
        }
    }

    private fun sync(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                val topPlatformItems =
                    service.getTopPlatforms(sortingField, timePeriod, forceRefresh)
                viewItems = getViewItems(topPlatformItems)
                viewState = ViewState.Success
            } catch (e: Throwable) {
                viewState = ViewState.Error(e)
            }
        }
    }

    private fun getViewItems(topPlatformItems: List<TopPlatformItem>): List<TopPlatformViewItem> {
        return topPlatformItems.map { item ->
            TopPlatformViewItem(
                platform = item.platform,
                subtitle = Translator.getString(
                    R.string.MarketTopPlatforms_Protocols,
                    item.protocols
                ),
                marketCap = com.monistoWallet.core.App.numberFormatter.formatFiatShort(
                    item.marketCap,
                    service.baseCurrency.symbol,
                    2
                ),
                marketCapDiff = item.changeDiff,
                rank = item.rank.toString(),
                rankDiff = item.rankDiff,
            )
        }
    }

    private fun refreshWithMinLoadingSpinnerPeriod() {
        viewModelScope.launch {
            isRefreshing = true
            sync(true)
            delay(1000)
            isRefreshing = false
        }
    }

    fun onSelectSortingField(sortingField: com.monistoWallet.modules.market.SortingField) {
        this.sortingField = sortingField
        sortingSelect = Select(sortingField, sortingFields)
        selectorDialogState = SelectorDialogState.Closed
        sync()
    }

    fun onSelectorDialogDismiss() {
        selectorDialogState = SelectorDialogState.Closed
    }

    fun showSelectorMenu() {
        selectorDialogState = SelectorDialogState.Opened(
            Select(sortingSelect.selected, sortingSelect.options)
        )
    }

    fun refresh() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onErrorClick() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onTimePeriodSelect(timePeriod: com.monistoWallet.modules.market.TimeDuration) {
        this.timePeriod = timePeriod
        sync()
    }
}
