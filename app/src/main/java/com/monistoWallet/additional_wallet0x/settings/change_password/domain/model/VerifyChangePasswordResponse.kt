package com.monistoWallet.additional_wallet0x.settings.change_password.domain.model

interface VerifyChangePasswordResponse {
    class Success(val message: String): VerifyChangePasswordResponse
    class Error(val message: String): VerifyChangePasswordResponse
}