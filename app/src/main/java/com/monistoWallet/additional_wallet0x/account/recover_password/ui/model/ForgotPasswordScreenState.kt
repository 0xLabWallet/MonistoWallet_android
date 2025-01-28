package com.monistoWallet.additional_wallet0x.account.recover_password.ui.model

interface ForgotPasswordScreenState {
    object Null : ForgotPasswordScreenState
    object Loading : ForgotPasswordScreenState
    class Error(val message: String) : ForgotPasswordScreenState
    class Result(val data: String) : ForgotPasswordScreenState
}