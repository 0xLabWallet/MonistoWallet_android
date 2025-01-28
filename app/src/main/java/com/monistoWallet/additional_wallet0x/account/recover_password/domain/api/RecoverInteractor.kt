package com.monistoWallet.additional_wallet0x.account.recover_password.domain.api

import com.monistoWallet.additional_wallet0x.account.recover_password.domain.model.RecoverPassword
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.model.VerifyRecoverPassword

interface RecoverInteractor {
    fun forgotPassword(email: String, onResult: (RecoverPassword) -> Unit)
    fun verifyRecoverPassword(email: String, newPassword: String, code: String, onResult: (VerifyRecoverPassword) -> Unit)
}