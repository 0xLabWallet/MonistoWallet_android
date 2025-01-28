package com.monistoWallet.modules.market

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberAsyncImagePainter
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.entities.Currency
import com.monistoWallet.entities.CurrencyValue
import com.monistoWallet.modules.market.filters.TimePeriod
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.WithTranslatableTitle
import com.wallet0x.marketkit.models.FullCoin
import com.wallet0x.marketkit.models.MarketInfo
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

object MarketModule {

    class Factory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = com.monistoWallet.modules.market.MarketService(
                com.monistoWallet.core.App.marketStorage,
                com.monistoWallet.core.App.localStorage
            )
            return com.monistoWallet.modules.market.MarketViewModel(service) as T
        }

    }

    enum class Tab(@StringRes val titleResId: Int) {
        Overview(R.string.Market_Tab_Overview),
        Posts(R.string.Market_Tab_Posts),
        Watchlist(R.string.Market_Tab_Watchlist);

        companion object {
            private val map = values().associateBy(com.monistoWallet.modules.market.MarketModule.Tab::name)

            fun fromString(type: String?): com.monistoWallet.modules.market.MarketModule.Tab? = com.monistoWallet.modules.market.MarketModule.Tab.Companion.map[type]
        }
    }

    enum class ListType(val sortingField: com.monistoWallet.modules.market.SortingField, val marketField: com.monistoWallet.modules.market.MarketField) {
        TopGainers(
            com.monistoWallet.modules.market.SortingField.TopGainers,
            com.monistoWallet.modules.market.MarketField.PriceDiff
        ),
        TopLosers(
            com.monistoWallet.modules.market.SortingField.TopLosers,
            com.monistoWallet.modules.market.MarketField.PriceDiff
        ),
    }

    data class Header(
        val title: String,
        val description: String,
        val icon: com.monistoWallet.modules.market.ImageSource,
    )
}

data class MarketItem(
    val fullCoin: FullCoin,
    val volume: CurrencyValue,
    val rate: CurrencyValue,
    val diff: BigDecimal?,
    val marketCap: CurrencyValue,
    val rank: Int?
) {
    companion object {
        fun createFromCoinMarket(
            marketInfo: MarketInfo,
            currency: Currency,
            pricePeriod: TimePeriod = TimePeriod.TimePeriod_1D
        ): com.monistoWallet.modules.market.MarketItem {
            return com.monistoWallet.modules.market.MarketItem(
                fullCoin = marketInfo.fullCoin,
                volume = CurrencyValue(currency, marketInfo.totalVolume ?: BigDecimal.ZERO),
                rate = CurrencyValue(currency, marketInfo.price ?: BigDecimal.ZERO),
                diff = marketInfo.priceChangeValue(pricePeriod),
                marketCap = CurrencyValue(currency, marketInfo.marketCap ?: BigDecimal.ZERO),
                rank = marketInfo.marketCapRank
            )
        }
    }
}

fun List<com.monistoWallet.modules.market.MarketItem>.sort(sortingField: com.monistoWallet.modules.market.SortingField) = when (sortingField) {
    com.monistoWallet.modules.market.SortingField.HighestCap -> sortedByDescendingNullLast { it.marketCap.value }
    com.monistoWallet.modules.market.SortingField.LowestCap -> sortedByNullLast { it.marketCap.value }
    com.monistoWallet.modules.market.SortingField.HighestVolume -> sortedByDescendingNullLast { it.volume.value }
    com.monistoWallet.modules.market.SortingField.LowestVolume -> sortedByNullLast { it.volume.value }
    com.monistoWallet.modules.market.SortingField.TopGainers -> sortedByDescendingNullLast { it.diff }
    com.monistoWallet.modules.market.SortingField.TopLosers -> sortedByNullLast { it.diff }
}

@Parcelize
enum class SortingField(@StringRes val titleResId: Int) : WithTranslatableTitle, Parcelable {
    HighestCap(R.string.Market_Field_HighestCap), LowestCap(R.string.Market_Field_LowestCap),
    HighestVolume(R.string.Market_Field_HighestVolume), LowestVolume(R.string.Market_Field_LowestVolume),
    TopGainers(R.string.RateList_TopGainers), TopLosers(R.string.RateList_TopLosers);

    override val title: TranslatableString
        get() = TranslatableString.ResString(titleResId)

    companion object {
        val map = values().associateBy(com.monistoWallet.modules.market.SortingField::name)
        fun fromString(type: String?): com.monistoWallet.modules.market.SortingField? = com.monistoWallet.modules.market.SortingField.Companion.map[type]
    }
}

@Parcelize
enum class MarketField(@StringRes val titleResId: Int) : WithTranslatableTitle, Parcelable {
    PriceDiff(R.string.Market_Field_PriceDiff),
    MarketCap(R.string.Market_Field_MarketCap),
    Volume(R.string.Market_Field_Volume);

    fun next() = values()[if (ordinal == values().size - 1) 0 else ordinal + 1]

    override val title: TranslatableString
        get() = TranslatableString.ResString(titleResId)

    companion object {
        val map = values().associateBy(com.monistoWallet.modules.market.MarketField::name)
        fun fromString(type: String?): com.monistoWallet.modules.market.MarketField? = com.monistoWallet.modules.market.MarketField.Companion.map[type]
    }
}

@Parcelize
enum class TopMarket(val value: Int) : WithTranslatableTitle, Parcelable {
    Top100(100), Top200(200), Top300(300);

    fun next() = values()[if (ordinal == values().size - 1) 0 else ordinal + 1]

    override val title: TranslatableString
        get() = TranslatableString.PlainString(value.toString())
}

sealed class ImageSource {
    class Local(@DrawableRes val resId: Int) : com.monistoWallet.modules.market.ImageSource()
    class Remote(val url: String, @DrawableRes val placeholder: Int = R.drawable.ic_placeholder) : com.monistoWallet.modules.market.ImageSource()

    @Composable
    fun painter(): Painter = when (this) {
        is com.monistoWallet.modules.market.ImageSource.Local -> painterResource(resId)
        is com.monistoWallet.modules.market.ImageSource.Remote -> rememberAsyncImagePainter(
            model = url,
            error = painterResource(placeholder)
        )
    }
}

sealed class Value {
    class Percent(val percent: BigDecimal) : com.monistoWallet.modules.market.Value()
    class Currency(val currencyValue: CurrencyValue) : com.monistoWallet.modules.market.Value()

    fun raw() = when (this) {
        is com.monistoWallet.modules.market.Value.Currency -> currencyValue.value
        is com.monistoWallet.modules.market.Value.Percent -> percent
    }
}

sealed class MarketDataValue {
    class MarketCap(val value: String) : com.monistoWallet.modules.market.MarketDataValue()
    class Volume(val value: String) : com.monistoWallet.modules.market.MarketDataValue()
    class Diff(val value: BigDecimal?) : com.monistoWallet.modules.market.MarketDataValue()
    class DiffNew(val value: com.monistoWallet.modules.market.Value) : com.monistoWallet.modules.market.MarketDataValue()
}

inline fun <T, R : Comparable<R>> Iterable<T>.sortedByDescendingNullLast(crossinline selector: (T) -> R?): List<T> {
    return sortedWith(compareBy(nullsFirst(), selector)).sortedByDescending(selector)
}

inline fun <T, R : Comparable<R>> Iterable<T>.sortedByNullLast(crossinline selector: (T) -> R?): List<T> {
    return sortedWith(compareBy(nullsLast(), selector))
}

fun MarketInfo.priceChangeValue(period: TimePeriod) = when (period) {
    TimePeriod.TimePeriod_1D -> priceChange24h
    TimePeriod.TimePeriod_1W -> priceChange7d
    TimePeriod.TimePeriod_2W -> priceChange14d
    TimePeriod.TimePeriod_1M -> priceChange30d
    TimePeriod.TimePeriod_6M -> priceChange200d
    TimePeriod.TimePeriod_1Y -> priceChange1y
}

@Parcelize
enum class TimeDuration(val titleResId: Int) : WithTranslatableTitle, Parcelable {
    OneDay(R.string.CoinPage_TimeDuration_Day),
    SevenDay(R.string.CoinPage_TimeDuration_Week),
    ThirtyDay(R.string.CoinPage_TimeDuration_Month),
    ThreeMonths(R.string.CoinPage_TimeDuration_Month3);

    @IgnoredOnParcel
    override val title = TranslatableString.ResString(titleResId)
}
