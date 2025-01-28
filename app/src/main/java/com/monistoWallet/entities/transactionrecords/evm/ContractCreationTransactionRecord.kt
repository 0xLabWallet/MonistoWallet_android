package com.monistoWallet.entities.transactionrecords.evm

import com.monistoWallet.modules.transactions.TransactionSource
import com.wallet0x.ethereumkit.models.Transaction
import com.wallet0x.marketkit.models.Token

class ContractCreationTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource
) : EvmTransactionRecord(transaction, baseToken, source)
