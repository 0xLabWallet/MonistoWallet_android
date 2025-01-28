package com.monistoWallet.additional_wallet0x.account.card_variants.domain.api

import com.monistoWallet.additional_wallet0x.account.card_variants.domain.model.CardsListVariantsResponse

interface CurrentCardListInteractor {
    fun getAllCards(accessToken: String, onResponse: (CardsListVariantsResponse) -> Unit)
}