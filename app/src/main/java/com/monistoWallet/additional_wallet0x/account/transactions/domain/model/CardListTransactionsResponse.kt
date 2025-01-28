package com.monistoWallet.additional_wallet0x.account.transactions.domain.model

import com.monistoWallet.additional_wallet0x.account.transactions.data.model.TransactionsDataModel

interface CardListTransactionsResponse {
    class Error(val message: String) : CardListTransactionsResponse
    class Success(val data: TransactionsDataModel) : CardListTransactionsResponse
}