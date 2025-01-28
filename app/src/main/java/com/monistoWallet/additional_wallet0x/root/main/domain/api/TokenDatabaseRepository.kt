package com.monistoWallet.additional_wallet0x.root.main.domain.api

import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel

interface TokenDatabaseRepository {
    fun saveToken(model: VerificationSuccessModel?)
    fun getToken(): VerificationSuccessModel?
}