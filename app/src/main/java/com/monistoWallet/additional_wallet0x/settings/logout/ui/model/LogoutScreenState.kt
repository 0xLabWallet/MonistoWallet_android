package com.monistoWallet.additional_wallet0x.settings.logout.ui.model

interface LogoutScreenState {
    class Error(val message: String) : LogoutScreenState
    class Result(val model: String) : LogoutScreenState
    object Loading : LogoutScreenState
    object Null : LogoutScreenState
}