package com.monistoWallet.entities.transactionrecords.solana

import com.monistoWallet.entities.TransactionValue
import com.monistoWallet.modules.transactions.TransactionSource
import com.wallet0x.marketkit.models.Token
import com.wallet0x.solanakit.models.Transaction

class SolanaOutgoingTransactionRecord(
        transaction: Transaction,
        baseToken: Token,
        source: TransactionSource,
        val to: String?,
        val value: TransactionValue,
        val sentToSelf: Boolean
): SolanaTransactionRecord(transaction, baseToken, source) {

    override val mainValue = value

}
