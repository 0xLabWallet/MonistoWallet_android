package com.monistoWallet.additional_wallet0x.settings.change_email.domain.api

import com.monistoWallet.additional_wallet0x.settings.change_email.domain.model.ChangeEmailResponse
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.model.VerifyChangeEmailResponse

interface ChangeEmailInteractor {
    fun changeEmail(
        accessToken: String,
        newEmail: String,
        onResponse: (ChangeEmailResponse) -> Unit
    )
    fun verifyChangeEmail(
        accessToken: String,
        newEmail: String,
        code: String,
        onResponse: (VerifyChangeEmailResponse) -> Unit
    )
}