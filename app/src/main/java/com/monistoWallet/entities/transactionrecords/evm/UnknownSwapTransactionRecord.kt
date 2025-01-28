package com.monistoWallet.entities.transactionrecords.evm

import com.monistoWallet.entities.TransactionValue
import com.monistoWallet.modules.transactions.TransactionSource
import com.wallet0x.ethereumkit.models.Transaction
import com.wallet0x.marketkit.models.Token

class UnknownSwapTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val exchangeAddress: String,
    val valueIn: TransactionValue?,
    val valueOut: TransactionValue?,
) : EvmTransactionRecord(transaction, baseToken, source)
