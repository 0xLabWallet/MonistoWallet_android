package com.monistoWallet.additional_wallet0x.settings.change_email.ui.view_model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.api.ChangeEmailInteractor
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.model.ChangeEmailResponse
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.model.VerifyChangeEmailResponse
import com.monistoWallet.additional_wallet0x.settings.change_email.ui.model.ChangeEmailScreenState
import com.monistoWallet.additional_wallet0x.settings.change_email.ui.model.VerifyChangeEmailScreenState
import com.monistoWallet.additional_wallet0x.root.main.domain.api.TokenDatabaseInteractor
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager.Companion.REFRESH_TOKEN_ERROR_CODE
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenResponse

class ChangeEmailViewModel(
    private val tokenDatabase: TokenDatabaseInteractor,
    private val changeEmailInteractor: ChangeEmailInteractor,
) : ViewModel() {
    var changeEmailScreenState: ChangeEmailScreenState by mutableStateOf(
        ChangeEmailScreenState.Null)
        private set
    var verifyChangeEmailScreenState: VerifyChangeEmailScreenState by mutableStateOf(
        VerifyChangeEmailScreenState.Null)
        private set

    fun changeEmail(oldEmail: String) {
        val token = (tokenDatabase.getToken() ?: return)
        if (oldEmail == "") return
        changeEmailScreenState = ChangeEmailScreenState.Loading
        changeEmailInteractor.changeEmail(token.access_token, oldEmail) {
            when (it) {
                is ChangeEmailResponse.Error -> {
                    if (it.message.contains(REFRESH_TOKEN_ERROR_CODE)) {
                        RefreshTokenManager.updateAccessToken(token.refresh_token) {
                            when (it) {
                                is RefreshTokenResponse.Error -> {
                                    changeEmailScreenState = ChangeEmailScreenState.Error(it.message)
                                }
                                is RefreshTokenResponse.Success -> {
                                    tokenDatabase.saveToken(it.model)
                                    changeEmail(oldEmail)
                                }
                            }
                        }
                        return@changeEmail
                    }
                    changeEmailScreenState = ChangeEmailScreenState.Error(it.message)
                }
                is ChangeEmailResponse.Success -> {
                    Log.d("Wallet0xTag", "ChangeEmailViewModel.changeEmail: ${it.model}")
                    changeEmailScreenState = ChangeEmailScreenState.Result(it.model)
                }
            }
        }
    }
    fun verifyChangeEmail(oldEmail: String, newEmail: String, code: String) {
        val token = (tokenDatabase.getToken() ?: return)
        if (oldEmail == "") return
        verifyChangeEmailScreenState = VerifyChangeEmailScreenState.Loading
        changeEmailInteractor.verifyChangeEmail(token.access_token, newEmail, code) {
            when (it) {
                is VerifyChangeEmailResponse.Error -> {
                    if (it.message.contains(REFRESH_TOKEN_ERROR_CODE)) {
                        RefreshTokenManager.updateAccessToken(token.refresh_token) {
                            when (it) {
                                is RefreshTokenResponse.Error -> {
                                    verifyChangeEmailScreenState = VerifyChangeEmailScreenState.Error(it.message)
                                }
                                is RefreshTokenResponse.Success -> {
                                    tokenDatabase.saveToken(it.model)
                                    verifyChangeEmail(oldEmail, newEmail, code)
                                }
                            }
                        }
                        return@verifyChangeEmail
                    }
                    verifyChangeEmailScreenState = VerifyChangeEmailScreenState.Error(it.message)
                }
                is VerifyChangeEmailResponse.Success -> {
                    tokenDatabase.saveToken(it.model)
                    verifyChangeEmailScreenState = VerifyChangeEmailScreenState.Result("Email successfully changed")
                }
            }
        }
    }

}