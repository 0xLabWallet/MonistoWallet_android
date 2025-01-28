package com.monistoWallet.additional_wallet0x.root.get_card_data.domain.api

import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.model.CardSecretDataResponse

interface GetCardDataRepository {
    fun loadCardData(
        accessToken: String,
        cardId: String,
        onResponse: (CardSecretDataResponse) -> Unit
    )
}