package com.monistoWallet.additional_wallet0x.account.recover_password.ui.model

import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel

interface VerifyRecoverPasswordScreenState {
    object Null : VerifyRecoverPasswordScreenState
    object Loading : VerifyRecoverPasswordScreenState
    class Error(val message: String) : VerifyRecoverPasswordScreenState
    class Result(val data: VerificationSuccessModel) : VerifyRecoverPasswordScreenState
}