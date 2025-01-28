package com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.api

import com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.model.VerifyRegisterAccount

interface VerifyRegisterRepository {
    fun verifyRegister(email: String, code: String, onResponse: (VerifyRegisterAccount) -> Unit)
}