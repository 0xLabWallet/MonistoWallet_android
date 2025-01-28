package com.monistoWallet.additional_wallet0x.account.freeze_card.ui.model

interface FreezeCardScreenState {
    object Null : FreezeCardScreenState
    object Loading : FreezeCardScreenState
    class Error(val message: String) : FreezeCardScreenState
    class Success(val message: String) : FreezeCardScreenState
}