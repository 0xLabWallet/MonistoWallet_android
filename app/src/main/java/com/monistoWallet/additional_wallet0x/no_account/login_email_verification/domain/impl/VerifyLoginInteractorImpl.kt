package com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.impl

import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.api.VerifyLoginInteractor
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.api.VerifyLoginRepository
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.model.VerifyLoginAccount

class VerifyLoginInteractorImpl(val repository: VerifyLoginRepository): VerifyLoginInteractor {
    override fun verify(
        email: String,
        code: String,
        onResponse: (VerifyLoginAccount) -> Unit
    ) {
        repository.verify(email, code, onResponse)
    }
}