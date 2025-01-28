package com.monistoWallet.entities.nft

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.ForeignKey
import com.monistoWallet.core.storage.AccountRecord
import com.wallet0x.marketkit.models.BlockchainType

@Entity(
    primaryKeys = ["blockchainType", "accountId", "nftUid"],
    foreignKeys = [ForeignKey(
        entity = AccountRecord::class,
        parentColumns = ["id"],
        childColumns = ["accountId"],
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )],
    indices = [Index(value = ["accountId"])]
)
data class NftAssetRecord(
    val blockchainType: BlockchainType,
    val accountId: String,
    val nftUid: NftUid,
    val collectionUid: String,
    val name: String?,
    val imagePreviewUrl: String?,
    val onSale: Boolean,

    @Embedded(prefix = "lastSale_")
    val lastSale: NftPriceRecord?
)
