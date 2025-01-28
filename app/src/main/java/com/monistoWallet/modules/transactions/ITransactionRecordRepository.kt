package com.monistoWallet.modules.transactions

import com.monistoWallet.core.Clearable
import com.monistoWallet.entities.transactionrecords.TransactionRecord
import com.wallet0x.marketkit.models.Blockchain
import io.reactivex.Observable

interface ITransactionRecordRepository : Clearable {
    val itemsObservable: Observable<List<TransactionRecord>>

    fun setWallets(
        transactionWallets: List<TransactionWallet>,
        wallet: TransactionWallet?,
        transactionType: FilterTransactionType,
        blockchain: Blockchain?
    )
    fun setWalletAndBlockchain(transactionWallet: TransactionWallet?, blockchain: Blockchain?)
    fun setTransactionType(transactionType: FilterTransactionType)
    fun loadNext()
    fun reload()
}
