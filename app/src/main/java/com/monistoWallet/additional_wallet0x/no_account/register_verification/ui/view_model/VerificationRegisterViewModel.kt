package com.monistoWallet.additional_wallet0x.no_account.register_verification.ui.view_model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.additional_wallet0x.no_account.register_verification.ui.model.ConfirmCreateAccountScreenState
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel
import com.monistoWallet.additional_wallet0x.no_account.register_verification.ui.model.EmailVerificationScreenState
import com.monistoWallet.additional_wallet0x.no_account.register.domain.api.GetCodeToRegisterInteractor
import com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.api.VerifyRegisterInteractor
import com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.model.VerifyRegisterAccount
import kotlinx.coroutines.launch

class VerificationRegisterViewModel(
    val getCodeInteractor: GetCodeToRegisterInteractor,
    val verifyRegisterInteractor: VerifyRegisterInteractor
): ViewModel() {
    var screenStateLD: EmailVerificationScreenState by mutableStateOf(EmailVerificationScreenState.Null)
        private set

    var verifyRegistrationLD: ConfirmCreateAccountScreenState by mutableStateOf(ConfirmCreateAccountScreenState.Null)
        private set

    fun getCodeToRegister(email: String, password: String) {
        screenStateLD = EmailVerificationScreenState.Loading
        viewModelScope.launch {
            getCodeInteractor.getCodeToRegister(email, password) {
                when (it) {
                    is GetCodeResponseModel.Error -> {
                        screenStateLD = EmailVerificationScreenState.Error(it.message)
                    }

                    is GetCodeResponseModel.Success -> {
                        Log.d("Wallet0xTag", "code: " + it.model.toString())
                        screenStateLD = EmailVerificationScreenState.Success(it.model)
                    }
                }
            }
        }
    }

    fun verifyRegister(email: String, code: String) {
        verifyRegistrationLD = ConfirmCreateAccountScreenState.Loading
        viewModelScope.launch {
            verifyRegisterInteractor.verifyRegister(email, code) {
                when (it) {
                    is VerifyRegisterAccount.Error -> {
                        Log.d("Wallet0xTag", "Register Err: ${it.message}")
                        verifyRegistrationLD = ConfirmCreateAccountScreenState.Error(it.message)
                    }

                    is VerifyRegisterAccount.Success -> {
                        Log.d("Wallet0xTag", "Register Success: ${it.model}")
                        verifyRegistrationLD = ConfirmCreateAccountScreenState.Success(it.model)
                    }
                }
            }
        }
    }

}