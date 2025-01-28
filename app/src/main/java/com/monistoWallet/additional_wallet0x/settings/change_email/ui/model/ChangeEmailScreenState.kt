package com.monistoWallet.additional_wallet0x.settings.change_email.ui.model

import com.monistoWallet.additional_wallet0x.root.model.GetCodeSuccessModel

interface ChangeEmailScreenState {
    class Error(val message: String) : ChangeEmailScreenState
    class Result(val model: GetCodeSuccessModel) : ChangeEmailScreenState
    object Loading : ChangeEmailScreenState
    object Null : ChangeEmailScreenState
}