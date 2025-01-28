package com.monistoWallet.additional_wallet0x.settings.change_email.ui.model

interface VerifyChangeEmailScreenState {
    class Error(val message: String) : VerifyChangeEmailScreenState
    class Result(val model: String) : VerifyChangeEmailScreenState
    object Loading : VerifyChangeEmailScreenState
    object Null : VerifyChangeEmailScreenState
}