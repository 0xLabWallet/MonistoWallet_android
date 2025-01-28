package com.monistoWallet.entities.transactionrecords.evm

import com.monistoWallet.entities.TransactionValue
import com.monistoWallet.modules.transactions.TransactionSource
import com.wallet0x.ethereumkit.models.Transaction
import com.wallet0x.marketkit.models.Token

class SwapTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val exchangeAddress: String,
    val amountIn: Amount,
    val amountOut: Amount?,
    val recipient: String?
) : EvmTransactionRecord(transaction, baseToken, source) {

    sealed class Amount(val value: TransactionValue) {
        class Exact(value: TransactionValue) : Amount(value)
        class Extremum(value: TransactionValue) : Amount(value)
    }

    val valueIn: TransactionValue
        get() = amountIn.value

    val valueOut: TransactionValue?
        get() = amountOut?.value

}
