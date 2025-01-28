package com.monistoWallet.additional_wallet0x.account.pay_for_card.domain.impl

import com.monistoWallet.additional_wallet0x.account.pay_for_card.domain.api.CancelPaymentInteractor
import com.monistoWallet.additional_wallet0x.account.pay_for_card.domain.api.CancelPaymentRepository
import com.monistoWallet.additional_wallet0x.account.pay_for_card.domain.model.CancelPaymentResponse

class CancelPaymentInteractorImpl(val repository: CancelPaymentRepository) : CancelPaymentInteractor {
    override fun cancelPayment(accessToken: String, onResponse: (CancelPaymentResponse) -> Unit) {
        repository.cancelPayment(accessToken, onResponse)
    }
}