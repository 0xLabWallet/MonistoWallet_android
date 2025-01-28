package com.monistoWallet.additional_wallet0x.settings.logout.ui.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.additional_wallet0x.root.main.domain.api.TokenDatabaseInteractor
import com.monistoWallet.additional_wallet0x.root.main.ui.model.RootCardScreenState
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager.Companion.REFRESH_TOKEN_ERROR_CODE
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenResponse
import com.monistoWallet.additional_wallet0x.settings.change_password.ui.model.VerifyChangePasswordScreenState
import com.monistoWallet.additional_wallet0x.settings.logout.domain.api.LogoutInteractor
import com.monistoWallet.additional_wallet0x.settings.logout.domain.model.LogoutResponse
import com.monistoWallet.additional_wallet0x.settings.logout.ui.model.LogoutScreenState

class LogoutViewModel(
    val tokenDatabaseInteractor: TokenDatabaseInteractor,
    val logoutInteractor: LogoutInteractor,
) : ViewModel() {

    var logoutScreenStateLD: LogoutScreenState by mutableStateOf(LogoutScreenState.Null)
        private set

    fun logout() {
        val token = (tokenDatabaseInteractor.getToken() ?: return)
        logoutScreenStateLD = LogoutScreenState.Loading
        logoutInteractor.logout(token.access_token) {
            when (it) {
                is LogoutResponse.Error -> {
                    if (it.message.contains(REFRESH_TOKEN_ERROR_CODE)) {
                        RefreshTokenManager.updateAccessToken(token.refresh_token) {
                            when (it) {
                                is RefreshTokenResponse.Error -> {
                                    logoutScreenStateLD = LogoutScreenState.Error(it.message)
                                }
                                is RefreshTokenResponse.Success -> {
                                    logout()
                                }
                            }
                        }
                        return@logout
                    }
                    logoutScreenStateLD = LogoutScreenState.Error(it.message)
                }

                is LogoutResponse.Success -> {
                    logoutScreenStateLD = LogoutScreenState.Result(it.message)
                }
            }
        }
    }

    fun clearStates() {
        logoutScreenStateLD = LogoutScreenState.Null
    }
}