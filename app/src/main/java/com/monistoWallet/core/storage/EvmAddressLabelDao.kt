package com.monistoWallet.core.storage

import androidx.room.*
import com.monistoWallet.entities.EvmAddressLabel

@Dao
interface EvmAddressLabelDao {

    @Query("SELECT * FROM EvmAddressLabel WHERE address = :address")
    fun get(address: String): EvmAddressLabel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(label: EvmAddressLabel)

    @Query("DELETE FROM EvmAddressLabel")
    fun clear()

    @Transaction
    fun update(labels: List<EvmAddressLabel>) {
        clear()
        labels.forEach { insert(it) }
    }

}
