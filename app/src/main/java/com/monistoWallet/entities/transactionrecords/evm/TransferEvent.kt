package com.monistoWallet.entities.transactionrecords.evm

import com.monistoWallet.entities.TransactionValue

data class TransferEvent(
    val address: String?,
    val value: TransactionValue
)
