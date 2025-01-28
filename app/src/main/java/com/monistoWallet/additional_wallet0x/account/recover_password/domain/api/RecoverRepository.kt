package com.monistoWallet.additional_wallet0x.account.recover_password.domain.api

import com.monistoWallet.additional_wallet0x.account.recover_password.domain.model.RecoverPassword
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.model.VerifyRecoverPassword

interface RecoverRepository {
    fun forgotPassword(email: String, onResponse: (RecoverPassword) -> Unit)
    fun verifyRecoverPassword(email: String, newPassword: String, code: String, onResponse: (VerifyRecoverPassword) -> Unit)
}