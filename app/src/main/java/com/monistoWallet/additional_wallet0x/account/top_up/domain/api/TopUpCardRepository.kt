package com.monistoWallet.additional_wallet0x.account.top_up.domain.api

import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.model.RequestPayApplyResponse

interface TopUpCardRepository {
    fun topUpCard(accessToken: String, cardProviderId: String, network: String, token: String, onResult: (RequestPayApplyResponse) -> Unit)
}