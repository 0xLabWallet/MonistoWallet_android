package com.monistoWallet.additional_wallet0x.account.freeze_card.domain.impl

import com.monistoWallet.additional_wallet0x.account.freeze_card.domain.api.CardFreezeManagerInteractor
import com.monistoWallet.additional_wallet0x.account.freeze_card.domain.api.CardFreezeManagerRepository
import com.monistoWallet.additional_wallet0x.account.freeze_card.domain.model.CardFreezeResponse
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card

class CardFreezeManagerInteractorImpl(val repository: CardFreezeManagerRepository) :
    CardFreezeManagerInteractor {
    override fun freezeCard(
        accessToken: String,
        card: Card,
        onResponse: (CardFreezeResponse) -> Unit
    ) {
        repository.freezeCard(accessToken, card, onResponse)
    }

    override fun unfreezeCard(
        accessToken: String,
        card: Card,
        onResponse: (CardFreezeResponse) -> Unit
    ) {
        repository.unfreezeCard(accessToken, card, onResponse)
    }
}