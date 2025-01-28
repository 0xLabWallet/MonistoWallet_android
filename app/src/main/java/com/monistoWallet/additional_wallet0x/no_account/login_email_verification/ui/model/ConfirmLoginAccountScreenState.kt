package com.monistoWallet.additional_wallet0x.no_account.login_email_verification.ui.model

import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel

interface ConfirmLoginAccountScreenState {
    object Loading : ConfirmLoginAccountScreenState
    class Error(val message: String) : ConfirmLoginAccountScreenState
    object Null : ConfirmLoginAccountScreenState

    class Success(val data: VerificationSuccessModel) : ConfirmLoginAccountScreenState
}