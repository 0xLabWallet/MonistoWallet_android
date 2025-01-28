package com.monistoWallet.entities.nft

import androidx.room.Entity
import com.wallet0x.marketkit.models.BlockchainType

@Entity(primaryKeys = ["blockchainType", "accountId"])
data class NftMetadataSyncRecord(
    val blockchainType: BlockchainType,
    val accountId: String,
    val lastSyncTimestamp: Long
)
