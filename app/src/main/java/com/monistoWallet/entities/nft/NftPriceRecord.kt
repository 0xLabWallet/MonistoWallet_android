package com.monistoWallet.entities.nft

import com.wallet0x.marketkit.models.NftPrice
import java.math.BigDecimal

data class NftPriceRecord(
    val tokenQueryId: String,
    val value: BigDecimal
) {
    constructor(nftPrice: NftPrice) : this(nftPrice.token.tokenQuery.id, nftPrice.value)
}