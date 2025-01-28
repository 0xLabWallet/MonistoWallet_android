package com.monistoWallet.entities.transactionrecords.solana

import com.monistoWallet.core.adapters.BaseSolanaAdapter
import com.monistoWallet.entities.TransactionValue
import com.monistoWallet.entities.transactionrecords.TransactionRecord
import com.monistoWallet.modules.transactions.TransactionSource
import com.wallet0x.marketkit.models.Token
import com.wallet0x.solanakit.models.Transaction

open class SolanaTransactionRecord(transaction: Transaction, baseToken: Token, source: TransactionSource, spam: Boolean = false) :
        TransactionRecord(
                uid = transaction.hash,
                transactionHash = transaction.hash,
                transactionIndex = 0,
                blockHeight = if (transaction.pending) null else 0,
                confirmationsThreshold = BaseSolanaAdapter.confirmationsThreshold,
                timestamp = transaction.timestamp,
                failed = transaction.error != null,
                spam = spam,
                source = source
        ) {

    data class Transfer(val address: String?, val value: TransactionValue)

    val fee: TransactionValue?

    init {
        fee = transaction.fee?.let { TransactionValue.CoinValue(baseToken, it) }
    }

}
