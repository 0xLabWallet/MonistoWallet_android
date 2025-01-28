package com.monistoWallet.additional_wallet0x.account.card_variants.domain.impl

import com.monistoWallet.additional_wallet0x.account.card_variants.domain.api.CurrentCardListInteractor
import com.monistoWallet.additional_wallet0x.account.card_variants.domain.api.CurrentCardListRepository
import com.monistoWallet.additional_wallet0x.account.card_variants.domain.model.CardsListVariantsResponse

class CurrentCardListInteractorImpl(val repository: CurrentCardListRepository) : CurrentCardListInteractor {
    override fun getAllCards(accessToken: String, onResponse: (CardsListVariantsResponse) -> Unit) {
        repository.getAllCards(accessToken, onResponse)
    }
}