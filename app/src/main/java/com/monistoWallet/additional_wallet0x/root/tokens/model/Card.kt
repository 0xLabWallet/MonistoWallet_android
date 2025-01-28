package com.monistoWallet.additional_wallet0x.root.tokens.model

data class Card(
    val balance: Double,
    val card_PIN: String,
    val card_cvv: String,
    val card_expires: String,
    val card_layout_provider_id: String,
    val card_number: String,
    val id: String,
    val pay_system: String,
    val status: String,
    val transactions: List<Transaction>,
    val type: String
) {
    fun getFormattedCardNumber(): String {
        val data = card_number.chunked(4)
        var result = ""
        data.forEach {
            result += "$it  "
        }
        return result
    }

    fun getHiddenCardNumber(): String {
        val data = card_number.chunked(4)
        return "****  ${data.last()}"
    }
}