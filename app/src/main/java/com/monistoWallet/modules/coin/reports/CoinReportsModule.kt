package com.monistoWallet.modules.coin.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import javax.annotation.concurrent.Immutable

object CoinReportsModule {
    @Suppress("UNCHECKED_CAST")
    class Factory(private val coinUid: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = CoinReportsService(coinUid, com.monistoWallet.core.App.marketKit)
            return CoinReportsViewModel(service) as T
        }
    }

    @Immutable
    data class ReportViewItem(
        val author: String,
        val title: String,
        val body: String,
        val date: String,
        val url: String
    )
}
