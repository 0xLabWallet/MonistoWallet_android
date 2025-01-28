package com.monistoWallet.additional_wallet0x.settings.change_email.domain.model

import com.monistoWallet.additional_wallet0x.root.model.GetCodeSuccessModel

interface ChangeEmailResponse {
    class Success(val model: GetCodeSuccessModel): ChangeEmailResponse
    class Error(val message: String): ChangeEmailResponse
}