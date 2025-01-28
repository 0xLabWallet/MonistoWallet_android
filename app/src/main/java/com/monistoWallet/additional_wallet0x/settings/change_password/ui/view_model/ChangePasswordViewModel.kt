package com.monistoWallet.additional_wallet0x.settings.change_password.ui.view_model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.additional_wallet0x.settings.change_password.domain.api.ChangePasswordInteractor
import com.monistoWallet.additional_wallet0x.settings.change_password.domain.model.VerifyChangePasswordResponse
import com.monistoWallet.additional_wallet0x.settings.change_password.ui.model.ChangePasswordScreenState
import com.monistoWallet.additional_wallet0x.settings.change_password.ui.model.VerifyChangePasswordScreenState
import com.monistoWallet.additional_wallet0x.root.main.domain.api.TokenDatabaseInteractor
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel.Companion.userEmail
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager.Companion.REFRESH_TOKEN_ERROR_CODE
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenResponse
import com.monistoWallet.additional_wallet0x.settings.change_email.ui.model.VerifyChangeEmailScreenState

class ChangePasswordViewModel(
    private val tokenDatabase: TokenDatabaseInteractor,
    private val changePasswordInteractor: ChangePasswordInteractor
) : ViewModel() {
    var changePasswordScreenState: ChangePasswordScreenState by mutableStateOf(
        ChangePasswordScreenState.Null)
        private set

    var verifyChangePasswordScreenState: VerifyChangePasswordScreenState by mutableStateOf(
        VerifyChangePasswordScreenState.Null)
        private set

    fun changePassword(oldPassword: String) {
        val token = (tokenDatabase.getToken() ?: return)
        if (userEmail == "") return
        changePasswordScreenState = ChangePasswordScreenState.Loading
        changePasswordInteractor.changePassword(token.access_token, userEmail, oldPassword) {
            when (it) {
                is GetCodeResponseModel.Error -> {
                    if (it.message.contains(REFRESH_TOKEN_ERROR_CODE)) {
                        RefreshTokenManager.updateAccessToken(token.refresh_token) {
                            when (it) {
                                is RefreshTokenResponse.Error -> {
                                    changePasswordScreenState = ChangePasswordScreenState.Error(it.message)
                                }
                                is RefreshTokenResponse.Success -> {
                                    tokenDatabase.saveToken(it.model)
                                    changePassword(oldPassword)
                                }
                            }
                        }
                        return@changePassword
                    }
                    changePasswordScreenState = ChangePasswordScreenState.Error(it.message)
                }
                is GetCodeResponseModel.Success -> {
                    Log.d("Wallet0xTag", "ChangePasswordViewModel.changePassword: ${it.model}")
                    changePasswordScreenState = ChangePasswordScreenState.Result(it.model)
                }
            }
        }
    }
    fun verifyChangePassword(password: String, code: String) {
        val token = (tokenDatabase.getToken() ?: return)
        if (userEmail == "") return
        verifyChangePasswordScreenState = VerifyChangePasswordScreenState.Loading
        changePasswordInteractor.verifyChangePassword(token.access_token, userEmail, code, password) {
            when (it) {
                is VerifyChangePasswordResponse.Error -> {
                    if (it.message.contains(REFRESH_TOKEN_ERROR_CODE)) {
                        RefreshTokenManager.updateAccessToken(token.refresh_token) {
                            when (it) {
                                is RefreshTokenResponse.Error -> {
                                    verifyChangePasswordScreenState = VerifyChangePasswordScreenState.Error(it.message)
                                }
                                is RefreshTokenResponse.Success -> {
                                    tokenDatabase.saveToken(it.model)
                                    verifyChangePassword(password, code)
                                }
                            }
                        }
                        return@verifyChangePassword
                    }
                    verifyChangePasswordScreenState = VerifyChangePasswordScreenState.Error(it.message)
                }
                is VerifyChangePasswordResponse.Success -> {
                    verifyChangePasswordScreenState = VerifyChangePasswordScreenState.Result(it.message)
                }
            }
        }
    }
}