package com.monistoWallet.additional_wallet0x.no_account.login.domain.model

import com.monistoWallet.additional_wallet0x.no_account.login.domain.api.GetCodeToLoginSuccessModeInteractor
import com.monistoWallet.additional_wallet0x.no_account.login.domain.api.GetCodeToLoginSuccessModelRepository
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel

class GetCodeToLoginSuccessModeInteractorImpl(val repository: GetCodeToLoginSuccessModelRepository): GetCodeToLoginSuccessModeInteractor {
    override fun getLoginCode(email: String, password: String, onResponse: (GetCodeResponseModel) -> Unit) {
        repository.getLoginCode(email, password, onResponse)
    }
}