package com.monistoWallet.additional_wallet0x.account.recover_password.domain.impl

import com.monistoWallet.additional_wallet0x.account.recover_password.domain.api.RecoverInteractor
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.api.RecoverRepository
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.model.RecoverPassword
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.model.VerifyRecoverPassword

class RecoverInteractorImpl(val repository: RecoverRepository): RecoverInteractor {
    override fun forgotPassword(email: String, onResult: (RecoverPassword) -> Unit) {
        repository.forgotPassword(email, onResult)
    }

    override fun verifyRecoverPassword(
        email: String,
        newPassword: String,
        code: String,
        onResult: (VerifyRecoverPassword) -> Unit
    ) {
        repository.verifyRecoverPassword(email, newPassword, code, onResult)
    }
}