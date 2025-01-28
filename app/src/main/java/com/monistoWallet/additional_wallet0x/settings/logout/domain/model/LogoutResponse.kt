package com.monistoWallet.additional_wallet0x.settings.logout.domain.model

interface LogoutResponse {
    class Success(val message: String): LogoutResponse
    class Error(val message: String): LogoutResponse
}