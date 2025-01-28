package com.monistoWallet.modules.market

import com.monistoWallet.core.ILocalStorage
import com.monistoWallet.core.IMarketStorage
import com.monistoWallet.entities.LaunchPage

class MarketService(
    private val storage: IMarketStorage,
    private val localStorage: ILocalStorage,
) {
    val launchPage: LaunchPage?
        get() = localStorage.launchPage

    var currentTab: com.monistoWallet.modules.market.MarketModule.Tab?
        get() = storage.currentMarketTab
        set(value) {
            storage.currentMarketTab = value
        }

}
