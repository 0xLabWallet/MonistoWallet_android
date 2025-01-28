package com.monistoWallet.additional_wallet0x.account.recover_password.ui.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.api.RecoverInteractor
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.model.RecoverPassword
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.model.VerifyRecoverPassword
import com.monistoWallet.additional_wallet0x.account.recover_password.ui.model.ForgotPasswordScreenState
import com.monistoWallet.additional_wallet0x.account.recover_password.ui.model.VerifyRecoverPasswordScreenState

class RecoverViewModel(
    val recoverInteractor: RecoverInteractor
) : ViewModel() {
    var forgotPasswordScreenState: ForgotPasswordScreenState by mutableStateOf(
        ForgotPasswordScreenState.Null
    )
    var verifyRecoverPasswordScreenState: VerifyRecoverPasswordScreenState by mutableStateOf(
        VerifyRecoverPasswordScreenState.Null
    )

    fun forgotPassword(email: String) {
        forgotPasswordScreenState = ForgotPasswordScreenState.Loading
        recoverInteractor.forgotPassword(email) {
            when (it) {
                is RecoverPassword.Success -> {
                    forgotPasswordScreenState = ForgotPasswordScreenState.Result(it.message)
                }
                is RecoverPassword.Error -> {
                    forgotPasswordScreenState = ForgotPasswordScreenState.Error(it.message)
                }
            }
        }
    }

    fun verifyRecoverPassword(email: String, newPassword: String, code: String) {
        verifyRecoverPasswordScreenState = VerifyRecoverPasswordScreenState.Loading
        recoverInteractor.verifyRecoverPassword(email, newPassword, code) {
            when (it) {
                is VerifyRecoverPassword.Success -> {
                    verifyRecoverPasswordScreenState = VerifyRecoverPasswordScreenState.Result(it.data)
                }
                is VerifyRecoverPassword.Error -> {
                    verifyRecoverPasswordScreenState = VerifyRecoverPasswordScreenState.Error(it.message)
                }
            }
        }
    }
}