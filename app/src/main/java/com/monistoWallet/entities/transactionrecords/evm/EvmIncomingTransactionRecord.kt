package com.monistoWallet.entities.transactionrecords.evm

import com.monistoWallet.entities.TransactionValue
import com.monistoWallet.modules.transactions.TransactionSource
import com.wallet0x.ethereumkit.models.Transaction
import com.wallet0x.marketkit.models.Token

class EvmIncomingTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val from: String,
    val value: TransactionValue
) : EvmTransactionRecord(transaction, baseToken, source, true) {

    override val mainValue = value

}
