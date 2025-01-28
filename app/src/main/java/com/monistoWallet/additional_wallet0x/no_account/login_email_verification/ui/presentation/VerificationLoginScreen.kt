package com.monistoWallet.additional_wallet0x.no_account.login_email_verification.ui.presentation

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.recover_password.ui.presentation.screen.RecoverPasswordScreen
import com.monistoWallet.additional_wallet0x.no_account.login.ui.presentation.model.LoginScreenState
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.ui.model.ConfirmLoginAccountScreenState
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.ui.view_model.LoginVerificationViewModel
import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.additional_wallet0x.root.ui.GreyRedText
import com.monistoWallet.additional_wallet0x.root.ui.InputTextGetCode
import com.monistoWallet.additional_wallet0x.root.usecase.obfuscateEmail
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer
import org.koin.androidx.compose.koinViewModel

@Composable
fun VerificationLoginScreen(
    email: String,
    password: String,
    onClickBack: () -> Unit,
    onClickNext: (VerificationSuccessModel) -> Unit,
    onAddAccount: (VerificationSuccessModel) -> Unit,
    vm: LoginVerificationViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var showRecover by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var currentCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        ButtonBack {
            onClickBack.invoke()
        }

        Text(
            text = stringResource(id = R.string.Email_Verification),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(top = 28.dp)
                .align(Alignment.CenterHorizontally)
        )

        VSpacer(height = 20.dp)
        InputTextGetCode(
            stringResource(R.string.Enter_the_6_digit_code_sent_to) + " " + obfuscateEmail(
                email
            ), {
                currentCode = it
            }, {
                vm.getLoginCode(email, password)
            })

        VSpacer(height = 32.dp)
        GreyRedText(rootModifier = Modifier.clickable {
            showRecover = true
        }, greyText = stringResource(id = R.string.Forgot_your_password), redText = stringResource(
            id = R.string.Recover_it_here
        ))
        Spacer(modifier = Modifier.weight(1f))
        ButtonPrimaryYellow(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = currentCode.length == 6 && !isLoading,
            title = stringResource(R.string.Button_Next),
            onClick = {
                if (!isLoading) {
                    isLoading = true
                    vm.verifyLoginBy(email, currentCode)
                }
            }
        )
    }

    val view = LocalView.current
    Crossfade(targetState = vm.screenCodeStateLD, label = "") {
        when (it) {
            is LoginScreenState.Error -> {
                LaunchedEffect(Unit) {
                    isLoading = false
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
                    Toast.makeText(context, it.data.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        vm.clearStates()
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            vm.clearStates()
        }
    }
    Crossfade(targetState = vm.verifyLoginScreenState, label = "") {
        when (it) {
            is ConfirmLoginAccountScreenState.Error -> {
                LaunchedEffect(Unit) {
                    isLoading = false
                    HudHelper.show0xErrorMessage(view, "Verify Login Error", it.message)
                }
            }

            is ConfirmLoginAccountScreenState.Loading -> {
                LaunchedEffect(Unit) {
                    isLoading = true
                }
            }

            is ConfirmLoginAccountScreenState.Success -> {
                LaunchedEffect(Unit) {
                    isLoading = false
                    onClickNext.invoke(it.data)
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