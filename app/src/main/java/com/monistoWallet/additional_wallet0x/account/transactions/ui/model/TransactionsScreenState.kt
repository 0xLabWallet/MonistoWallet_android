package com.monistoWallet.additional_wallet0x.account.transactions.ui.model

import com.monistoWallet.additional_wallet0x.account.transactions.data.model.Transaction

interface TransactionsScreenState {
    object Null : TransactionsScreenState
    object Loading : TransactionsScreenState
    class Error(val message: String) : TransactionsScreenState
    object NotFound : TransactionsScreenState
    class Success(val list: List<Transaction>) : TransactionsScreenState
}