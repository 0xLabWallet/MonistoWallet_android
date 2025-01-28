package com.monistoWallet.additional_wallet0x.no_account.login.ui.presentation.screen

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.recover_password.ui.presentation.screen.RecoverPasswordScreen
import com.monistoWallet.additional_wallet0x.no_account.login.ui.presentation.model.LoginScreenState
import com.monistoWallet.additional_wallet0x.no_account.login.ui.view_model.LoginViewModel
import com.monistoWallet.additional_wallet0x.root.model.GetCodeSuccessModel
import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.additional_wallet0x.root.ui.ButtonClose
import com.monistoWallet.additional_wallet0x.root.ui.GreyRedText
import com.monistoWallet.additional_wallet0x.root.ui.InputTextClear
import com.monistoWallet.additional_wallet0x.root.ui.InputTextPassword
import com.monistoWallet.additional_wallet0x.root.usecase.isValidEmail
import com.monistoWallet.additional_wallet0x.root.usecase.isValidPassword
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onClickRegister: () -> Unit,
    onClickNext: (String, String) -> Unit,
    onAddAccount: (VerificationSuccessModel) -> Unit,
    vm: LoginViewModel = koinViewModel()
) {
    var showRecover by remember { mutableStateOf(false) }
    val view = LocalView.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isValidEmail by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var model by remember { mutableStateOf<GetCodeSuccessModel?>(null) }

    val context = LocalView.current.context
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            ButtonBack {
                onBack.invoke()
            }
            Text(
                text = stringResource(id = R.string.Login),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(bottom = 71.dp, top = 28.dp)
                    .align(Alignment.Center)
            )
        }

        InputTextClear(stringResource(R.string.Email)) {
            isValidEmail = isValidEmail(it)
            email = it
        }
        Spacer(modifier = Modifier.height(18.dp))
        InputTextPassword(stringResource(R.string.Password)) {
            password = it
        }
        VSpacer(height = 32.dp)
        GreyRedText(rootModifier = Modifier
            .clickable {
                showRecover = true
            }
            .align(Alignment.Start), greyText = stringResource(id = R.string.Forgot_your_password), redText = stringResource(
            id = R.string.Recover_it_here
        ))
        Spacer(modifier = Modifier.weight(1f))

        GreyRedText(
            Modifier
                .padding(bottom = 10.dp)
                .align(Alignment.CenterHorizontally)
                .clickable {
                    onClickRegister.invoke()
                },
            stringResource(R.string.Dont_have_an_account),
            stringResource(R.string.Register)
        )
        ButtonPrimaryYellow(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = isValidEmail && password.isNotEmpty(),
            title = stringResource(R.string.Button_Next),
            onClick = {
                if (!isValidPassword(password)) {
                    if (password.length < 8) {
                        HudHelper.show0xErrorMessage(view, "", context.getString(R.string.Alert_Min_Password_size))
                    } else {
                        HudHelper.show0xErrorMessage(view, "", context.getString(R.string.Alert_Password_Symbols))
                    }
                } else {
                    vm.getLoginCode(email, password)
                }
            }
        )
    }
    LaunchedEffect(key1 = Unit) {
        vm.clearStates()
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            vm.clearStates()
        }
    }
    Crossfade(targetState = vm.screenStateLD, label = "") {
        when (it) {
            is LoginScreenState.Error -> {
                LaunchedEffect(Unit) {
                    isLoading = false
                    model = null
                    HudHelper.show0xErrorMessage(view, "Login Error", it.message)
                }
            }

            is LoginScreenState.Loading -> {
                LaunchedEffect(Unit) {
                    isLoading = true
                }
            }

            is LoginScreenState.Success -> {
                LaunchedEffect(Unit) {
                    isLoading = false
                    onClickNext.invoke(email, password)
                }
            }
        }
    }


    if (showRecover) {
        RecoverPasswordScreen(emailI = email, {
            showRecover = false
        }, {
            onAddAccount.invoke(it)
        })
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
