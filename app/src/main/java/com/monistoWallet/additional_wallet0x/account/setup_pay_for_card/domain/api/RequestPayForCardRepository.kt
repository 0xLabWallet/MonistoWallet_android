package com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.api

import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.model.RequestPayApplyResponse

interface RequestPayForCardRepository {
    fun requestPayApply(accessToken: String, cardLayoutId: String, chain: String, tokenSymbol: String, onResponse: (RequestPayApplyResponse) -> Unit)
}