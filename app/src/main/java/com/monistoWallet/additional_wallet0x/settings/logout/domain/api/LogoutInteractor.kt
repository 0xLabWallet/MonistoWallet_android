package com.monistoWallet.additional_wallet0x.settings.logout.domain.api

import com.monistoWallet.additional_wallet0x.settings.logout.domain.model.LogoutResponse

interface LogoutInteractor {
    fun logout(accessToken: String, onResponse: (LogoutResponse) -> Unit)
}