package com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.model

import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel

interface VerifyLoginAccount {
    class Error(val message: String) : VerifyLoginAccount
    class Success(val model: VerificationSuccessModel) : VerifyLoginAccount
}