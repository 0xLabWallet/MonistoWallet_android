package com.monistoWallet.additional_wallet0x.root.get_card_data.ui.model

interface CardDataScreenState {
    object Loading : CardDataScreenState
    object Null : CardDataScreenState
    class Error(val str: String) : CardDataScreenState
    class Success(val str: String) : CardDataScreenState
}