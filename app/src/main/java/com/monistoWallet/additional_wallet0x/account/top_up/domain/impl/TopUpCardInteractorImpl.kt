package com.monistoWallet.additional_wallet0x.account.top_up.domain.impl

import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.model.RequestPayApplyResponse
import com.monistoWallet.additional_wallet0x.account.top_up.domain.api.TopUpCardInteractor
import com.monistoWallet.additional_wallet0x.account.top_up.domain.api.TopUpCardRepository

class TopUpCardInteractorImpl(val repository: TopUpCardRepository) : TopUpCardInteractor {
    override fun topUpCard(
        accessToken: String,
        cardProviderId: String,
        network: String,
        token: String,
        onResult: (RequestPayApplyResponse) -> Unit
    ) {
        repository.topUpCard(accessToken, cardProviderId, network, token, onResult)
    }
}