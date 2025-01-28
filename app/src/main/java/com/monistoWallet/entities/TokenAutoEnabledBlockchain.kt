package com.monistoWallet.entities

import androidx.room.Entity
import com.wallet0x.marketkit.models.BlockchainType

@Entity(primaryKeys = ["accountId", "blockchainType"])
data class TokenAutoEnabledBlockchain(
    val accountId: String,
    val blockchainType: BlockchainType
)
