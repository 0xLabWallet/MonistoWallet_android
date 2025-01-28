package com.monistoWallet.additional_wallet0x.root.model

import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.data.model.RequestPayForCardResponseModel

data class RechargeSettings(
    val payModel: RequestPayForCardResponseModel,
    val selectedNetwork: String,
    val selectedCurrency: String,
    val minPay: Int,
    val maxPay: Int,
)