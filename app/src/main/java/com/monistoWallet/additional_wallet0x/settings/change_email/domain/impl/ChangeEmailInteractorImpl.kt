package com.monistoWallet.additional_wallet0x.settings.change_email.domain.impl

import com.monistoWallet.additional_wallet0x.settings.change_email.domain.api.ChangeEmailInteractor
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.api.ChangeEmailRepository
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.model.ChangeEmailResponse
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.model.VerifyChangeEmailResponse

class ChangeEmailInteractorImpl(val repository: ChangeEmailRepository) : ChangeEmailInteractor {
    override fun changeEmail(
        accessToken: String,
        newEmail: String,
        onResponse: (ChangeEmailResponse) -> Unit
    ) {
        repository.changeEmail(accessToken, newEmail, onResponse)
    }

    override fun verifyChangeEmail(
        accessToken: String,
        newEmail: String,
        code: String,
        onResponse: (VerifyChangeEmailResponse) -> Unit
    ) {
        repository.verifyChangeEmail(accessToken, newEmail, code, onResponse)
    }
}