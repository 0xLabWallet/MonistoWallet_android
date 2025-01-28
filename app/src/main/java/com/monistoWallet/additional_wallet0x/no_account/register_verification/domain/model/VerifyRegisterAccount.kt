package com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.model

import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel

interface VerifyRegisterAccount {
    class Error(val message: String) : VerifyRegisterAccount
    class Success(val model: VerificationSuccessModel) : VerifyRegisterAccount
}