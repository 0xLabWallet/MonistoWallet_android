package com.monistoWallet.additional_wallet0x.root.model

data class BaseRechargeSettings(
    val selectedNetwork: String,
    val selectedCurrency: String,
    val minPay: Int,
    val maxPay: Int,
)