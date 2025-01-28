package com.monistoWallet.additional_wallet0x.account.card_variants.data.model

import com.monistoWallet.R

data class CardLayout(
    val activate_min_amount: Int,
    val card_type: String,
    val description: String,
    val parameters: List<String>,
    val pay_system: String,
    val price: Double,
    val provider_id: String,
    val recharge_fee: Int,
    val recharge_max_amount: Int,
    val recharge_min_amount: Int
) {
    companion object {
        enum class CardTag(val tagId: Int) {
            KYC(R.drawable.card_tag_kyc),
            NOKYC(R.drawable.card_tag_no_kyc),
            GPAY(R.drawable.card_tag_apple_pay),
            APAY(R.drawable.card_tag_apple_pay),
            ATM(R.drawable.card_tag_atm);
        }
    }
}