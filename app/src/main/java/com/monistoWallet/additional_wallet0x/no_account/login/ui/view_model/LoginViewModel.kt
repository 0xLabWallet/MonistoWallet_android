package com.monistoWallet.additional_wallet0x.no_account.login.ui.view_model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.additional_wallet0x.no_account.login.domain.api.GetCodeToLoginSuccessModeInteractor
import com.monistoWallet.additional_wallet0x.no_account.login.ui.presentation.model.LoginScreenState
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel
import kotlinx.coroutines.launch

class LoginViewModel(
    private val getCodeToLoginSuccessModeInteractor: GetCodeToLoginSuccessModeInteractor
) : ViewModel() {
    var screenStateLD: LoginScreenState by mutableStateOf(LoginScreenState.Null)

    fun getLoginCode(email: String, password: String) {
        screenStateLD = LoginScreenState.Loading
        viewModelScope.launch {
            getCodeToLoginSuccessModeInteractor.getLoginCode(email, password) {
                when(it) {
                    is GetCodeResponseModel.Error -> {
                        Log.d("Wallet0xTag","Login Err ${it.message}")
                        screenStateLD = LoginScreenState.Error(it.message)
                    }

                    is GetCodeResponseModel.Success -> {
                        Log.d("Wallet0xTag","Login success ${it.model}")
                        screenStateLD = LoginScreenState.Success(it.model)
                    }
                }
            }
        }
    }

    fun clearStates() {
        screenStateLD = LoginScreenState.Null
    }
}