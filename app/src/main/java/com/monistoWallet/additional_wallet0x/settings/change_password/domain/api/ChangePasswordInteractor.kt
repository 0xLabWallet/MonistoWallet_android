package com.monistoWallet.additional_wallet0x.settings.change_password.domain.api

import com.monistoWallet.additional_wallet0x.settings.change_password.domain.model.VerifyChangePasswordResponse
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel

interface ChangePasswordInteractor {
    fun changePassword(
        accessToken: String,
        email: String,
        oldPassword: String,
        onResponse: (GetCodeResponseModel) -> Unit
    )
    fun verifyChangePassword(
        accessToken: String,
        email: String,
        code: String,
        newPassword: String,
        onResponse: (VerifyChangePasswordResponse) -> Unit
    )
}