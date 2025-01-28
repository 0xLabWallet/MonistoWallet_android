package com.monistoWallet.additional_wallet0x.no_account.login.domain.api

import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel

interface GetCodeToLoginSuccessModeInteractor {
    fun getLoginCode(email: String, password: String, onResponse: (GetCodeResponseModel) -> Unit)
}