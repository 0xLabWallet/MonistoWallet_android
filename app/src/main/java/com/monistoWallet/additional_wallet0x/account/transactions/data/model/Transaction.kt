package com.monistoWallet.additional_wallet0x.account.transactions.data.model

data class Transaction(
    val change_amount: Double,
    val created: String,
    val description: String,
    val fee: Double,
    val id: Int,
    val merchant_name: String,
    val provider_tx_id: String,
    val status: String?,
    val title: String,
    val type: String
)