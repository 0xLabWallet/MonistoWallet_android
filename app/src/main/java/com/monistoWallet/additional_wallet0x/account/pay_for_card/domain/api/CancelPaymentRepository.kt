package com.monistoWallet.additional_wallet0x.account.pay_for_card.domain.api

import com.monistoWallet.additional_wallet0x.account.pay_for_card.domain.model.CancelPaymentResponse

interface CancelPaymentRepository {
    fun cancelPayment(accessToken: String, onResponse: (CancelPaymentResponse) -> Unit)
}