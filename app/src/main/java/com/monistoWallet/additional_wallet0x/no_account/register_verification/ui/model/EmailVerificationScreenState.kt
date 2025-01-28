package com.monistoWallet.additional_wallet0x.no_account.register_verification.ui.model

import com.monistoWallet.additional_wallet0x.root.model.GetCodeSuccessModel

interface EmailVerificationScreenState {
    object Loading : EmailVerificationScreenState
    class Error(val message: String) : EmailVerificationScreenState
    object Null : EmailVerificationScreenState

    class Success(val data: GetCodeSuccessModel) : EmailVerificationScreenState
}