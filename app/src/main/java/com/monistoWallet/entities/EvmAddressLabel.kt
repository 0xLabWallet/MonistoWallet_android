package com.monistoWallet.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EvmAddressLabel(
    @PrimaryKey
    val address: String,
    val label: String
)
