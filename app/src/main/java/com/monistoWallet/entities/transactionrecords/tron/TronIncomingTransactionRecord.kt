package com.monistoWallet.entities.transactionrecords.tron

import com.monistoWallet.entities.TransactionValue
import com.monistoWallet.modules.transactions.TransactionSource
import com.wallet0x.marketkit.models.Token
import com.wallet0x.tronkit.models.Transaction

class TronIncomingTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val from: String,
    val value: TransactionValue,
    spam: Boolean
) : TronTransactionRecord(transaction, baseToken, source, true, spam) {

    override val mainValue = value

}
