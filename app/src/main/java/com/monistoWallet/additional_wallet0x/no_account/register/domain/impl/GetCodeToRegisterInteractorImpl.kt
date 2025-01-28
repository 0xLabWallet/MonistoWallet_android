package com.monistoWallet.additional_wallet0x.no_account.register.domain.impl

import com.monistoWallet.additional_wallet0x.no_account.register.domain.api.GetCodeToRegisterInteractor
import com.monistoWallet.additional_wallet0x.no_account.register.domain.api.GetCodeToRegisterRepository
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel

class GetCodeToRegisterInteractorImpl(private val getCodeToRegisterRepository: GetCodeToRegisterRepository) : GetCodeToRegisterInteractor {
    override fun getCodeToRegister(email: String, password: String, onResponse: (GetCodeResponseModel) -> Unit) {
        getCodeToRegisterRepository.getCodeToRegister(email, password, onResponse)
    }
}