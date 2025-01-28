package com.monistoWallet.entities.nft

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.monistoWallet.core.storage.AccountRecord
import com.wallet0x.marketkit.models.BlockchainType

@Entity(
    primaryKeys = ["blockchainType", "accountId", "uid"],
    foreignKeys = [ForeignKey(
        entity = AccountRecord::class,
        parentColumns = ["id"],
        childColumns = ["accountId"],
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )],
    indices = [
        Index(value = ["accountId"], name = "index_accountId"),
        Index(value = ["blockchainType"], name = "index_blockchainType") // Рекомендуется также создать индекс для других часто запрашиваемых столбцов
    ]
)
data class NftCollectionRecord(
    val blockchainType: BlockchainType,
    val accountId: String,
    val uid: String,
    val name: String,
    val imageUrl: String?,

    @Embedded(prefix = "averagePrice7d_")
    val averagePrice7d: NftPriceRecord?,

    @Embedded(prefix = "averagePrice30d_")
    val averagePrice30d: NftPriceRecord?
)
