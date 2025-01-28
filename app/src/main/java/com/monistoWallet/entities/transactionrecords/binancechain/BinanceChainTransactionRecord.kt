package com.monistoWallet.entities.transactionrecords.binancechain

import com.monistoWallet.core.adapters.BinanceAdapter
import com.monistoWallet.entities.TransactionValue
import com.monistoWallet.entities.transactionrecords.TransactionRecord
import com.monistoWallet.modules.transactions.TransactionSource
import com.wallet0x.binancechainkit.models.TransactionInfo
import com.wallet0x.marketkit.models.Token

abstract class BinanceChainTransactionRecord(
    transaction: TransactionInfo,
    feeToken: Token,
    source: TransactionSource
) : TransactionRecord(
    uid = transaction.hash,
    transactionHash = transaction.hash,
    transactionIndex = 0,
    blockHeight = transaction.blockNumber,
    confirmationsThreshold = BinanceAdapter.confirmationsThreshold,
    timestamp = transaction.date.time / 1000,
    failed = false,
    source = source
) {

    val fee = TransactionValue.CoinValue(feeToken, BinanceAdapter.transferFee)
    val memo = transaction.memo

}
