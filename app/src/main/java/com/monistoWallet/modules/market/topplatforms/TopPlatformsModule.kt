package com.monistoWallet.modules.market.topplatforms

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.iconUrl
import com.monistoWallet.modules.market.MarketField
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.TimeDuration
import com.monistoWallet.ui.compose.Select
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

object TopPlatformsModule {

    class Factory(private val timeDuration: com.monistoWallet.modules.market.TimeDuration?) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = TopPlatformsRepository(com.monistoWallet.core.App.marketKit, com.monistoWallet.core.App.currencyManager, "market_top_platforms")
            val service = TopPlatformsService(repository, com.monistoWallet.core.App.currencyManager)
            return TopPlatformsViewModel(service, timeDuration) as T
        }
    }

    data class Menu(
        val sortingFieldSelect: Select<com.monistoWallet.modules.market.SortingField>,
        val marketFieldSelect: Select<com.monistoWallet.modules.market.MarketField>
    )

}

@Parcelize
data class Platform(
    val uid: String,
    val name: String,
): Parcelable

data class TopPlatformItem(
    val platform: Platform,
    val rank: Int,
    val protocols: Int,
    val marketCap: BigDecimal,
    val rankDiff: Int?,
    val changeDiff: BigDecimal?
)

@Immutable
data class TopPlatformViewItem(
    val platform: Platform,
    val subtitle: String,
    val marketCap: String,
    val marketCapDiff: BigDecimal?,
    val rank: String?,
    val rankDiff: Int?,
) {


    val iconUrl: String
        get() = platform.iconUrl

    val iconPlaceHolder: Int
        get() = R.drawable.ic_platform_placeholder_24

}
