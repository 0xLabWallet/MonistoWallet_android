package com.monistoWallet.additional_wallet0x.settings.change_password.domain.impl

import com.monistoWallet.additional_wallet0x.settings.change_password.domain.api.ChangePasswordInteractor
import com.monistoWallet.additional_wallet0x.settings.change_password.domain.api.ChangePasswordRepository
import com.monistoWallet.additional_wallet0x.settings.change_password.domain.model.VerifyChangePasswordResponse
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel

class ChangePasswordInteractorImpl(val repository: ChangePasswordRepository) :
    ChangePasswordInteractor {
    override fun changePassword(
        accessToken: String,
        email: String,
        oldPassword: String,
        onResponse: (GetCodeResponseModel) -> Unit
    ) {
        repository.changePassword(accessToken, email, oldPassword, onResponse)
    }

    override fun verifyChangePassword(
        accessToken: String,
        email: String,
        code: String,
        newPassword: String,
        onResponse: (VerifyChangePasswordResponse) -> Unit
    ) {
        repository.verifyChangePassword(accessToken, email, code, newPassword, onResponse)
    }
}