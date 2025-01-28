package com.monistoWallet.additional_wallet0x.account.card_variants.data.model

data class CardVariantsModel(
    val available_networks: List<String>,
    val available_tokens: List<String>,
    val card_layouts: List<CardLayout>,
    val monthly_fee: Int,
    val subscription_debt: Int,
    val card_limit_reached: Boolean
) {
}