package com.monistoWallet.additional_wallet0x.account.pay_for_card.ui.model

interface PayScreenState {
    object Null : PayScreenState
    object Loading : PayScreenState
    class Error(val message: String) : PayScreenState
    class Result(val model: Any) : PayScreenState
}