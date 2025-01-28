package com.monistoWallet.additional_wallet0x.no_account.login_email_verification.ui.view_model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.additional_wallet0x.no_account.login.domain.api.GetCodeToLoginSuccessModeInteractor
import com.monistoWallet.additional_wallet0x.no_account.login.ui.presentation.model.LoginScreenState
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.ui.model.ConfirmLoginAccountScreenState
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.api.VerifyLoginInteractor
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.model.VerifyLoginAccount
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel
import kotlinx.coroutines.launch

class LoginVerificationViewModel(
    private val getCodeToLoginSuccessModeInteractor: GetCodeToLoginSuccessModeInteractor,
    private val verifyLoginInteractor: VerifyLoginInteractor
) : ViewModel() {
    var screenCodeStateLD: LoginScreenState by mutableStateOf(LoginScreenState.Null)
    var verifyLoginScreenState: ConfirmLoginAccountScreenState by mutableStateOf(ConfirmLoginAccountScreenState.Null)

    fun getLoginCode(email: String, password: String) {
        screenCodeStateLD = LoginScreenState.Loading
        viewModelScope.launch {
            getCodeToLoginSuccessModeInteractor.getLoginCode(email, password) {
                when(it) {
                    is GetCodeResponseModel.Error -> {
                        Log.d("Wallet0xTag","Login Err ${it.message}")
                        screenCodeStateLD = LoginScreenState.Error(it.message)
                    }

                    is GetCodeResponseModel.Success -> {
                        Log.d("Wallet0xTag","Login success ${it.model}")
                        screenCodeStateLD = LoginScreenState.Success(it.model)
                    }
                }
            }
        }
    }


    fun verifyLoginBy(email: String, currentCode: String) {
        verifyLoginScreenState = ConfirmLoginAccountScreenState.Loading
        viewModelScope.launch {
            verifyLoginInteractor.verify(email, currentCode) {
                when (it) {
                    is VerifyLoginAccount.Error -> {
                        Log.d("Wallet0xTag","VerifyLogin Err ${it.message}")
                        verifyLoginScreenState = ConfirmLoginAccountScreenState.Error(it.message)
                    }
                    is VerifyLoginAccount.Success -> {
                        Log.d("Wallet0xTag","VerifyLogin Success ${it.model}")
                        verifyLoginScreenState = ConfirmLoginAccountScreenState.Success(it.model)
                    }
                }
            }
        }
    }

    fun clearStates() {
        screenCodeStateLD = LoginScreenState.Null
        verifyLoginScreenState = ConfirmLoginAccountScreenState.Null
    }
}