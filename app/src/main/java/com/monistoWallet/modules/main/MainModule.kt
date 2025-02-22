package com.monistoWallet.modules.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.entities.Account
import com.monistoWallet.modules.walletconnect.version2.WC2Manager
import kotlinx.parcelize.Parcelize

object MainModule {

    class Factory(private val wcDeepLink: Uri?) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(
                App.pinComponent,
                App.rateAppManager,
                App.backupManager,
                App.termsManager,
                App.accountManager,
                App.releaseNotesManager,
                App.localStorage,
                App.wc2SessionManager,
                App.wc2Manager,
                wcDeepLink
            ) as T
        }
    }

    fun start(context: Context, data: Uri? = null) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = data
        context.startActivity(intent)
    }

    fun startAsNewTask(context: Activity) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        context.overridePendingTransition(0, 0)
    }

    sealed class BadgeType {
        object BadgeDot : BadgeType()
        class BadgeNumber(val number: Int) : BadgeType()
    }

    data class NavigationViewItem(
        val mainNavItem: MainNavigation,
        val selected: Boolean,
        val enabled: Boolean,
        val badge: BadgeType? = null
    )

    @Parcelize
    enum class MainNavigation(val iconRes: Int, val titleRes: Int) : Parcelable {
        Balance(R.drawable.ic_wallet_navigation_0x, R.string.Wallet_Title),
        Card(R.drawable.ic_card_navigation_0x, R.string.Card_Title),
        Settings(R.drawable.ic_account_navigation_0x, R.string.Account_Title);

        companion object {
            private val map = values().associateBy(MainNavigation::name)

            fun fromString(type: String?): MainNavigation? = map[type]
        }
    }

    data class UiState(
        val selectedTabIndex: Int,
        val deeplinkPage: DeeplinkPage?,
        val mainNavItems: List<NavigationViewItem>,
        val showRateAppDialog: Boolean,
        val contentHidden: Boolean,
        val showWhatsNew: Boolean,
        val activeWallet: Account?,
        val torEnabled: Boolean,
        val wcSupportState: WC2Manager.SupportState?
    )
}

data class DeeplinkPage(
    val navigationId: Int,
    val bundle: Bundle? = null
)
