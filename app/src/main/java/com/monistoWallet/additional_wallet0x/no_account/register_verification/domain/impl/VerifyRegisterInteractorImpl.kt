package com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.impl

import com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.api.VerifyRegisterInteractor
import com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.api.VerifyRegisterRepository
import com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.model.VerifyRegisterAccount

class VerifyRegisterInteractorImpl(val repository: VerifyRegisterRepository): VerifyRegisterInteractor {
    override fun verifyRegister(
        email: String,
        code: String,
        onResponse: (VerifyRegisterAccount) -> Unit
    ) {
        repository.verifyRegister(email, code, onResponse)
    }
}