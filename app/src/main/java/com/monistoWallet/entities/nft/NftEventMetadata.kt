package com.monistoWallet.entities.nft

import com.wallet0x.marketkit.models.NftPrice
import java.util.*

data class NftEventMetadata(
    val assetMetadata: NftAssetMetadata,
    val eventType: EventType?,
    val date: Date?,
    val amount: NftPrice?
) {
    enum class EventType {
        All,
        List,
        Sale,
        OfferEntered,
        BidEntered,
        BidWithdrawn,
        Transfer,
        Approve,
        Custom,
        Payout,
        Cancel,
        BulkCancel,
        Mint,
        Unknown;
    }
}
