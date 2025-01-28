package com.monistoWallet.additional_wallet0x.root.tokens

import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel

interface RefreshTokenResponse {
    class Error(val message: String) : RefreshTokenResponse
    class Success(val model: VerificationSuccessModel) : RefreshTokenResponse
}