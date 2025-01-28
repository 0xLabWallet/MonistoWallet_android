package com.monistoWallet.additional_wallet0x.account.card_variants.domain.model

import com.monistoWallet.additional_wallet0x.account.card_variants.data.model.CardVariantsModel

interface CardsListVariantsResponse {
    class Error(val message: String) : CardsListVariantsResponse
    class Success(val model: CardVariantsModel) : CardsListVariantsResponse
}