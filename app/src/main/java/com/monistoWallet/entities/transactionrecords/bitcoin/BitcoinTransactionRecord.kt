package com.monistoWallet.entities.transactionrecords.bitcoin

import com.monistoWallet.entities.LastBlockInfo
import com.monistoWallet.entities.TransactionValue
import com.monistoWallet.entities.transactionrecords.TransactionRecord
import com.monistoWallet.modules.transactions.TransactionLockInfo
import com.monistoWallet.modules.transactions.TransactionSource
import java.util.*

abstract class BitcoinTransactionRecord(
    uid: String,
    transactionHash: String,
    transactionIndex: Int,
    blockHeight: Int?,
    confirmationsThreshold: Int?,
    timestamp: Long,
    val fee: TransactionValue?,
    failed: Boolean,
    val lockInfo: TransactionLockInfo?,
    val conflictingHash: String?,
    val showRawTransaction: Boolean,
    val memo: String?,
    source: TransactionSource
) : TransactionRecord(
    uid = uid,
    transactionHash = transactionHash,
    transactionIndex = transactionIndex,
    blockHeight = blockHeight,
    confirmationsThreshold = confirmationsThreshold,
    timestamp = timestamp,
    failed = failed,
    source = source
) {

    override fun changedBy(oldBlockInfo: LastBlockInfo?, newBlockInfo: LastBlockInfo?): Boolean {
        return super.changedBy(oldBlockInfo, newBlockInfo)
                || becomesUnlocked(oldBlockInfo?.timestamp, newBlockInfo?.timestamp)
    }

    fun lockState(lastBlockTimestamp: Long?): TransactionLockState? {
        val lockInfo = lockInfo ?: return null

        var locked = true

        lastBlockTimestamp?.let {
            locked = it < lockInfo.lockedUntil.time / 1000
        }

        return TransactionLockState(locked, lockInfo.lockedUntil)
    }

    private fun becomesUnlocked(oldTimestamp: Long?, newTimestamp: Long?): Boolean {
        val lockTime = lockInfo?.lockedUntil?.time?.div(1000) ?: return false
        newTimestamp ?: return false

        return lockTime > (oldTimestamp ?: 0L) && // was locked
                lockTime <= newTimestamp       // now unlocked
    }
}

data class TransactionLockState(val locked: Boolean, val date: Date)
