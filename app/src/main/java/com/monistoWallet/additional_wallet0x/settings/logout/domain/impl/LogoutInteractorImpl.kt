package com.monistoWallet.additional_wallet0x.settings.logout.domain.impl

import com.monistoWallet.additional_wallet0x.settings.logout.domain.api.LogoutInteractor
import com.monistoWallet.additional_wallet0x.settings.logout.domain.api.LogoutRepository
import com.monistoWallet.additional_wallet0x.settings.logout.domain.model.LogoutResponse

class LogoutInteractorImpl(val repository: LogoutRepository) : LogoutInteractor {
    override fun logout(accessToken: String, onResponse: (LogoutResponse) -> Unit) {
        repository.logout(accessToken, onResponse)
    }

}