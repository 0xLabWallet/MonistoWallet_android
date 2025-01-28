package com.monistoWallet.additional_wallet0x.no_account.register_verification.ui.model

import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel

interface ConfirmCreateAccountScreenState {
    object Loading : ConfirmCreateAccountScreenState
    class Error(val message: String) : ConfirmCreateAccountScreenState
    object Null : ConfirmCreateAccountScreenState

    class Success(val data: VerificationSuccessModel) : ConfirmCreateAccountScreenState
}