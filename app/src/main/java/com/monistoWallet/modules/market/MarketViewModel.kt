package com.monistoWallet.modules.market

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.entities.LaunchPage

class MarketViewModel(private val service: MarketService) : ViewModel() {

    val tabs = com.monistoWallet.modules.market.MarketModule.Tab.values()
    var selectedTab by mutableStateOf(getInitialTab())
        private set

    fun onSelect(tab: com.monistoWallet.modules.market.MarketModule.Tab) {
        service.currentTab = tab
        selectedTab = tab
    }

    private fun getInitialTab() = when (service.launchPage) {
        LaunchPage.Cards -> com.monistoWallet.modules.market.MarketModule.Tab.Overview
        LaunchPage.Watchlist -> com.monistoWallet.modules.market.MarketModule.Tab.Watchlist
        else -> service.currentTab ?: com.monistoWallet.modules.market.MarketModule.Tab.Overview
    }
}
