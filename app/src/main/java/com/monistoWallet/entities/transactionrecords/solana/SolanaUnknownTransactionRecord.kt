package com.monistoWallet.entities.transactionrecords.solana

import com.monistoWallet.modules.transactions.TransactionSource
import com.wallet0x.marketkit.models.Token
import com.wallet0x.solanakit.models.Transaction

class SolanaUnknownTransactionRecord(
        transaction: Transaction,
        baseToken: Token,
        source: TransactionSource,
        val incomingTransfers: List<Transfer>,
        val outgoingTransfers: List<Transfer>
): SolanaTransactionRecord(transaction, baseToken, source)
