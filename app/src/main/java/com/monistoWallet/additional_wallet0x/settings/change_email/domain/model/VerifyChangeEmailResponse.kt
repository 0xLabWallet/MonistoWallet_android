package com.monistoWallet.additional_wallet0x.settings.change_email.domain.model

import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel

interface VerifyChangeEmailResponse {
    class Success(val model: VerificationSuccessModel): VerifyChangeEmailResponse
    class Error(val message: String): VerifyChangeEmailResponse
}