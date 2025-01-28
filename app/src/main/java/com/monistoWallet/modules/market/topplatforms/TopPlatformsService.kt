package com.monistoWallet.modules.market.topplatforms

import com.monistoWallet.core.managers.CurrencyManager
import com.monistoWallet.entities.Currency
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.TimeDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TopPlatformsService(
    private val repository: TopPlatformsRepository,
    private val currencyManager: CurrencyManager,
) {

    val baseCurrency: Currency
        get() = currencyManager.baseCurrency

    suspend fun getTopPlatforms(
        sortingField: com.monistoWallet.modules.market.SortingField,
        timeDuration: com.monistoWallet.modules.market.TimeDuration,
        forceRefresh: Boolean
    ): List<TopPlatformItem> = withContext(Dispatchers.IO) {
        repository.get(sortingField, timeDuration, forceRefresh)
    }

}
