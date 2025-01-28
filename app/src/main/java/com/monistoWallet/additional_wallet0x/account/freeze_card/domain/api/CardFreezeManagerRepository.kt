package com.monistoWallet.additional_wallet0x.account.freeze_card.domain.api

import com.monistoWallet.additional_wallet0x.account.freeze_card.domain.model.CardFreezeResponse
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card

interface CardFreezeManagerRepository {
    fun freezeCard(accessToken: String, card: Card, onResponse: (CardFreezeResponse) -> Unit)
    fun unfreezeCard(accessToken: String, card: Card, onResponse: (CardFreezeResponse) -> Unit)
}