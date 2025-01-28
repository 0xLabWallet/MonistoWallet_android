package com.monistoWallet.additional_wallet0x.settings.change_password.ui.model

import com.monistoWallet.additional_wallet0x.root.model.GetCodeSuccessModel

interface ChangePasswordScreenState {
    class Error(val message: String) : ChangePasswordScreenState
    class Result(val model: GetCodeSuccessModel) : ChangePasswordScreenState
    object Loading : ChangePasswordScreenState
    object Null : ChangePasswordScreenState
}