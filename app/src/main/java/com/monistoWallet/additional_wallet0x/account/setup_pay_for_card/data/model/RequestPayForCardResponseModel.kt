package com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.data.model

data class RequestPayForCardResponseModel(
    val address: String,
    val qr: String,
    val timestamp: Int
)