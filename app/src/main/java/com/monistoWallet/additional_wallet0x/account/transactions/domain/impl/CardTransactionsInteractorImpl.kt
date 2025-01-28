package com.monistoWallet.additional_wallet0x.account.transactions.domain.impl

import com.monistoWallet.additional_wallet0x.account.transactions.domain.api.CardTransactionsInteractor
import com.monistoWallet.additional_wallet0x.account.transactions.domain.api.CardTransactionsRepository
import com.monistoWallet.additional_wallet0x.account.transactions.domain.model.CardListTransactionsResponse
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card

class CardTransactionsInteractorImpl(private val cardTransactionsRepository: CardTransactionsRepository): CardTransactionsInteractor {
    override fun getTransactionsList(
        accessToken: String,
        card: Card,
        onResponse: (CardListTransactionsResponse) -> Unit
    ) {
        cardTransactionsRepository.getTransactionsList(accessToken, card, onResponse)
    }
}