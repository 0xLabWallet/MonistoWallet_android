package com.monistoWallet.modules.coin.audits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.ui.compose.TranslatableString
import com.wallet0x.marketkit.models.AuditReport
import java.util.*
import javax.annotation.concurrent.Immutable

object CoinAuditsModule {
    @Suppress("UNCHECKED_CAST")
    class Factory(private val addresses: List<String>) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = CoinAuditsService(addresses, com.monistoWallet.core.App.marketKit)
            return CoinAuditsViewModel(service) as T
        }
    }

    data class AuditorItem(
        val name: String,
        val logoUrl: String,
        val reports: List<AuditReport>,
        val latestDate: Date?
    )

    @Immutable
    data class AuditorViewItem(
        val name: String,
        val logoUrl: String,
        val auditViewItems: List<AuditViewItem>
    )

    @Immutable
    data class AuditViewItem(
        val date: String?,
        val name: String,
        val issues: TranslatableString,
        val reportUrl: String?
    )
}
