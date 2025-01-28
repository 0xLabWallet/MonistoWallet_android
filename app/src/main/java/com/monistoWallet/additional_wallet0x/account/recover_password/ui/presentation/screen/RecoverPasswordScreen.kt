package com.monistoWallet.additional_wallet0x.account.recover_password.ui.presentation.screen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.recover_password.ui.model.ForgotPasswordScreenState
import com.monistoWallet.additional_wallet0x.account.recover_password.ui.model.VerifyRecoverPasswordScreenState
import com.monistoWallet.additional_wallet0x.account.recover_password.ui.view_model.RecoverViewModel
import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.additional_wallet0x.root.ui.InputTextClear
import com.monistoWallet.additional_wallet0x.root.ui.InputTextGetCode
import com.monistoWallet.additional_wallet0x.root.ui.InputTextPassword
import com.monistoWallet.additional_wallet0x.root.usecase.isValidEmail
import com.monistoWallet.additional_wallet0x.root.usecase.isValidPassword
import com.monistoWallet.additional_wallet0x.root.usecase.obfuscateEmail
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer
import org.koin.androidx.compose.koinViewModel


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun RecoverPasswordScreen(
    emailI: String,
    onBack: () -> Unit,
    onAddAccount: (VerificationSuccessModel) -> Unit,
    vm: RecoverViewModel = koinViewModel()
) {
    val view = LocalView.current
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf(emailI) }
    var isValidEmail by remember { mutableStateOf(email != "") }
    var newPassword by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var isValidCode by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (email.isNotEmpty()) {
            vm.forgotPassword(email)
        }
    }

    Image(
        painter = painterResource(R.drawable.app_bg),
        contentDescription = null,
        modifier = Modifier.fillMaxSize().clickable(
            interactionSource = MutableInteractionSource(),
            indication = null
        ) {},
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        ButtonBack {
            onBack.invoke()
        }

        Text(
            text = stringResource(id = R.string.Recover_Password),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 71.dp, top = 28.dp)
                .align(Alignment.CenterHorizontally)
        )

        InputTextClear(stringResource(id = R.string.Email), email) {
            isValidEmail = isValidEmail(it)
            email = it
        }
        VSpacer(height = 20.dp)
        InputTextPassword(stringResource(id = R.string.New_Password)) {
            newPassword = it
        }
        VSpacer(height = 20.dp)
        InputTextGetCode(
            header = stringResource(R.string.Enter_the_6_digit_code_sent_to) + " " + obfuscateEmail(
                email
            ), onValueChanged = {
                code = it
                isValidCode = it.length == 6
            }, {
                if (isValidEmail) {
                    vm.forgotPassword(email)
                } else {
                    HudHelper.show0xErrorMessage(view, view.context.getString(R.string.Invalid_Email), "")
                }
            }, email.isNotEmpty()
        )
        Spacer(modifier = Modifier.weight(1f))

        ButtonPrimaryYellow(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = isValidEmail && isValidCode && newPassword.isNotEmpty(),
            title = stringResource(R.string.Confirm),
            onClick = {
                if (!isValidPassword(newPassword)) {
                    if (newPassword.length < 8) {
                        HudHelper.show0xErrorMessage(view, "", context.getString(R.string.Alert_Min_Password_size))
                    } else {
                        HudHelper.show0xErrorMessage(view, "", context.getString(R.string.Alert_Password_Symbols))
                    }
                } else {
                    vm.verifyRecoverPassword(email, newPassword, code)
                }
            }
        )
    }

    Crossfade(targetState = vm.forgotPasswordScreenState) {
        when (it) {
            is ForgotPasswordScreenState.Loading -> {
                isLoading = true
            }

            is ForgotPasswordScreenState.Result -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    Toast.makeText(view.context, it.data, Toast.LENGTH_SHORT).show()
                }
            }
            is ForgotPasswordScreenState.Error -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(view, "Recover Error", it.message)
                }
            }
        }
    }
    Crossfade(targetState = vm.verifyRecoverPasswordScreenState) {
        when (it) {
            is VerifyRecoverPasswordScreenState.Loading -> {
                isLoading = true
            }

            is VerifyRecoverPasswordScreenState.Result -> {
                isLoading = false
                onAddAccount.invoke(it.data)
            }
            is VerifyRecoverPasswordScreenState.Error -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(view, "Verification Recover Error", it.message)
                }
            }
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

}
