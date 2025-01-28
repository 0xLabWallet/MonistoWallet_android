package com.monistoWallet.modules.market.topnftcollections

import com.monistoWallet.core.IAppNumberFormatter
import com.monistoWallet.entities.CoinValue
import com.monistoWallet.modules.market.TimeDuration
import com.monistoWallet.modules.nft.NftCollectionItem
import java.math.BigDecimal

class TopNftCollectionsViewItemFactory(
    private val numberFormatter: IAppNumberFormatter
) {

    fun viewItem(
        collection: com.monistoWallet.modules.nft.NftCollectionItem,
        timeDuration: com.monistoWallet.modules.market.TimeDuration,
        order: Int
    ): TopNftCollectionViewItem {
        val volume: CoinValue?
        val volumeDiff: BigDecimal?
        when (timeDuration) {
            com.monistoWallet.modules.market.TimeDuration.OneDay -> {
                volume = collection.oneDayVolume
                volumeDiff = collection.oneDayVolumeDiff
            }
            com.monistoWallet.modules.market.TimeDuration.SevenDay -> {
                volume = collection.sevenDayVolume
                volumeDiff = collection.sevenDayVolumeDiff
            }
            com.monistoWallet.modules.market.TimeDuration.ThirtyDay -> {
                volume = collection.thirtyDayVolume
                volumeDiff = collection.thirtyDayVolumeDiff
            }

            com.monistoWallet.modules.market.TimeDuration.ThreeMonths -> {
                volume = null
                volumeDiff = null
            }
        }
        val volumeFormatted = volume?.let { numberFormatter.formatCoinShort(it.value, it.coin.code, 2) } ?: "---"
        val floorPriceFormatted = collection.floorPrice?.let {
            "Floor: " + numberFormatter.formatCoinShort(it.value, it.coin.code, 2)
        } ?: "---"

        return TopNftCollectionViewItem(
            blockchainType = collection.blockchainType,
            uid = collection.uid,
            name = collection.name,
            imageUrl = collection.imageUrl,
            volume = volumeFormatted,
            volumeDiff = volumeDiff ?: BigDecimal.ZERO,
            order = order,
            floorPrice = floorPriceFormatted
        )
    }

}
