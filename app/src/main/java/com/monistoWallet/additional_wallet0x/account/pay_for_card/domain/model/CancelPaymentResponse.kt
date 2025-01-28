package com.monistoWallet.additional_wallet0x.account.pay_for_card.domain.model

interface CancelPaymentResponse {
    class Error(val message: String) : CancelPaymentResponse
    class Success(val model: Any) : CancelPaymentResponse
}