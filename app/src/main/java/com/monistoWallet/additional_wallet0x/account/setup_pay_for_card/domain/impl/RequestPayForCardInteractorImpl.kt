package com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.impl

import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.api.RequestPayForCardInteractor
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.api.RequestPayForCardRepository
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.model.RequestPayApplyResponse

class RequestPayForCardInteractorImpl(val repository: RequestPayForCardRepository) :
    RequestPayForCardInteractor {
    override fun requestPayApply(
        accessToken: String,
        cardLayoutId: String,
        chain: String,
        tokenSymbol: String,
        onResponse: (RequestPayApplyResponse) -> Unit
    ) {
        repository.requestPayApply(accessToken, cardLayoutId, chain, tokenSymbol, onResponse)
    }
}