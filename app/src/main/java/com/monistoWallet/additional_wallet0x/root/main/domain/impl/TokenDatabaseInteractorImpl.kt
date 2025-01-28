package com.monistoWallet.additional_wallet0x.root.main.domain.impl

import com.monistoWallet.additional_wallet0x.root.main.domain.api.TokenDatabaseInteractor
import com.monistoWallet.additional_wallet0x.root.main.domain.api.TokenDatabaseRepository
import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel

class TokenDatabaseInteractorImpl(val repository: TokenDatabaseRepository): TokenDatabaseInteractor {
    override fun saveToken(model: VerificationSuccessModel?) {
        repository.saveToken(model)
    }

    override fun getToken(): VerificationSuccessModel? {
        return repository.getToken()
    }
}