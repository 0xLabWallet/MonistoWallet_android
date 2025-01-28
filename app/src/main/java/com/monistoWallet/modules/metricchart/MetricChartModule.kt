package com.monistoWallet.modules.metricchart

import android.os.Parcelable
import com.monistoWallet.R
import com.monistoWallet.modules.market.ImageSource
import kotlinx.parcelize.Parcelize

@Parcelize
enum class MetricsType : Parcelable {
    TotalMarketCap, BtcDominance, Volume24h, DefiCap, TvlInDefi;

    val title: Int
        get() = when (this) {
            TotalMarketCap -> R.string.MarketGlobalMetrics_TotalMarketCap
            BtcDominance -> R.string.MarketGlobalMetrics_BtcDominance
            Volume24h -> R.string.MarketGlobalMetrics_Volume
            DefiCap -> R.string.MarketGlobalMetrics_DefiCap
            TvlInDefi -> R.string.MarketGlobalMetrics_TvlInDefi
        }

    val description: Int
        get() = when (this) {
            TotalMarketCap -> R.string.MarketGlobalMetrics_TotalMarketCapDescription
            BtcDominance -> R.string.MarketGlobalMetrics_BtcDominanceDescription
            Volume24h -> R.string.MarketGlobalMetrics_VolumeDescription
            DefiCap -> R.string.MarketGlobalMetrics_DefiCapDescription
            TvlInDefi -> R.string.MarketGlobalMetrics_TvlInDefiDescription
        }

    val headerIcon: com.monistoWallet.modules.market.ImageSource
        get() {
            val imageName = when (this) {
                TotalMarketCap,
                BtcDominance ->  R.drawable.ic_total_mcap
                Volume24h -> R.drawable.ic_total_volume
                DefiCap -> R.drawable.ic_defi_cap
                TvlInDefi -> R.drawable.ic_tvl
            }
            return com.monistoWallet.modules.market.ImageSource.Local(imageName)
//            return ImageSource.Remote("https://cdn.blocksdecoded.com/header-images/$imageName@3x.png")
        }
}
