package com.monistoWallet.modules.coin.investments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import javax.annotation.concurrent.Immutable

object CoinInvestmentsModule {
    @Suppress("UNCHECKED_CAST")
    class Factory(private val coinUid: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = CoinInvestmentsService(coinUid, com.monistoWallet.core.App.marketKit, com.monistoWallet.core.App.currencyManager)
            return CoinInvestmentsViewModel(service, com.monistoWallet.core.App.numberFormatter) as T
        }
    }

    @Immutable
    data class ViewItem(
        val amount: String,
        val info: String,
        val fundViewItems: List<FundViewItem>
    )

    @Immutable
    data class FundViewItem(
        val name: String,
        val logoUrl: String,
        val isLead: Boolean,
        val url: String
    )
}
