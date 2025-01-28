package com.monistoWallet.additional_wallet0x.no_account.login.ui.presentation.model

import com.monistoWallet.additional_wallet0x.root.model.GetCodeSuccessModel

interface LoginScreenState {
    object Loading : LoginScreenState
    class Error(val message: String) : LoginScreenState
    object Null : LoginScreenState

    class Success(val data: GetCodeSuccessModel) : LoginScreenState
}