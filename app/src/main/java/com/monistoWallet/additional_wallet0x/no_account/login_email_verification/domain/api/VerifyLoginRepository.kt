package com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.api

import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.model.VerifyLoginAccount

interface VerifyLoginRepository {
    fun verify(email: String, code: String, onResponse: (VerifyLoginAccount) -> Unit)
}