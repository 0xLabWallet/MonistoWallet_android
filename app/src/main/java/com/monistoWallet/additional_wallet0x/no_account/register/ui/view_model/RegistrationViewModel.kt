package com.monistoWallet.additional_wallet0x.no_account.register.ui.view_model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.additional_wallet0x.no_account.register.domain.api.GetCodeToRegisterInteractor
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel
import com.monistoWallet.additional_wallet0x.no_account.register_verification.ui.model.EmailVerificationScreenState
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val getCodeToRegisterInteractor: GetCodeToRegisterInteractor
) : ViewModel() {

    var screenStateLD: EmailVerificationScreenState by mutableStateOf(EmailVerificationScreenState.Null)

    fun getCodeToRegister(email: String, password: String) {
        viewModelScope.launch {
            getCodeToRegisterInteractor.getCodeToRegister(email, password) {
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

}