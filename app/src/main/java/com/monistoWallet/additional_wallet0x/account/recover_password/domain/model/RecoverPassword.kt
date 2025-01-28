package com.monistoWallet.additional_wallet0x.account.recover_password.domain.model

interface RecoverPassword {
    class Error(val message: String) : RecoverPassword
    class Success(val message: String) : RecoverPassword
}
