package com.monistoWallet.additional_wallet0x.settings.change_password.ui.model

interface VerifyChangePasswordScreenState {
    class Error(val message: String) : VerifyChangePasswordScreenState
    class Result(val model: String) : VerifyChangePasswordScreenState
    object Loading : VerifyChangePasswordScreenState
    object Null : VerifyChangePasswordScreenState
}