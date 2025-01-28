package com.monistoWallet.additional_wallet0x.account.transactions.domain.api

import com.monistoWallet.additional_wallet0x.account.transactions.domain.model.CardListTransactionsResponse
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card

interface CardTransactionsRepository {
    fun getTransactionsList(accessToken: String, card: Card, onResponse: (CardListTransactionsResponse) -> Unit)
}