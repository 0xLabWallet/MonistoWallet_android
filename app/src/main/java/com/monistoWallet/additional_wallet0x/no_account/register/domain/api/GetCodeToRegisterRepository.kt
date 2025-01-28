package com.monistoWallet.additional_wallet0x.no_account.register.domain.api

import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel

interface GetCodeToRegisterRepository {
    fun getCodeToRegister(email: String, password: String, onResponse: (GetCodeResponseModel) -> Unit)
}