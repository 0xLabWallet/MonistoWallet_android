package com.monistoWallet.modules.nft

import com.monistoWallet.entities.CoinValue
import com.monistoWallet.modules.market.overview.coinValue
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.HsTimePeriod
import com.wallet0x.marketkit.models.NftTopCollection
import java.math.BigDecimal

data class NftCollectionItem(
    val blockchainType: BlockchainType,
    val uid: String,
    val name: String,
    val imageUrl: String?,
    val floorPrice: CoinValue?,
    val oneDayVolume: CoinValue?,
    val oneDayVolumeDiff: BigDecimal?,
    val sevenDayVolume: CoinValue?,
    val sevenDayVolumeDiff: BigDecimal?,
    val thirtyDayVolume: CoinValue?,
    val thirtyDayVolumeDiff: BigDecimal?
)

val NftTopCollection.nftCollectionItem: com.monistoWallet.modules.nft.NftCollectionItem
    get() = com.monistoWallet.modules.nft.NftCollectionItem(
        blockchainType = blockchainType,
        uid = providerUid,
        name = name,
        imageUrl = thumbnailImageUrl,
        floorPrice = floorPrice?.coinValue,
        oneDayVolume = volumes[HsTimePeriod.Day1]?.coinValue,
        oneDayVolumeDiff = changes[HsTimePeriod.Day1],
        sevenDayVolume = volumes[HsTimePeriod.Week1]?.coinValue,
        sevenDayVolumeDiff = changes[HsTimePeriod.Week1],
        thirtyDayVolume = volumes[HsTimePeriod.Month1]?.coinValue,
        thirtyDayVolumeDiff = changes[HsTimePeriod.Month1]
    )