package com.monistoWallet.additional_wallet0x.account.recover_password.domain.model

import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel

interface VerifyRecoverPassword {
    class Error(val message: String) : VerifyRecoverPassword
    class Success(val data: VerificationSuccessModel) : VerifyRecoverPassword
}
