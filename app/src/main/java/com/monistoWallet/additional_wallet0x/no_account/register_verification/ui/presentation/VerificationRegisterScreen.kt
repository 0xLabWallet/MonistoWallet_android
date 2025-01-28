package com.monistoWallet.additional_wallet0x.no_account.register_verification.ui.presentation

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
import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel
import com.monistoWallet.additional_wallet0x.no_account.register_verification.ui.model.ConfirmCreateAccountScreenState
import com.monistoWallet.additional_wallet0x.no_account.register_verification.ui.model.EmailVerificationScreenState
import com.monistoWallet.additional_wallet0x.no_account.register_verification.ui.view_model.VerificationRegisterViewModel
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.additional_wallet0x.root.ui.GreyRedText
import com.monistoWallet.additional_wallet0x.root.ui.InputTextGetCode
import com.monistoWallet.additional_wallet0x.root.usecase.obfuscateEmail
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import org.koin.androidx.compose.koinViewModel


@Composable
fun VerificationRegisterScreen(
    email: String,
    password: String,
    onClickBack: () -> Unit,
    onClickCodeNotReceived: () -> Unit,
    onClickNext: (VerificationSuccessModel) -> Unit,
    vm: VerificationRegisterViewModel = koinViewModel()
) {
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
                .padding(top = 28.dp, bottom = 100.dp)
                .align(Alignment.CenterHorizontally)
        )

        InputTextGetCode(
            stringResource(R.string.Enter_the_6_digit_code_sent_to) + " " + obfuscateEmail(
                email
            ), {
                currentCode = it
            }, {
                vm.getCodeToRegister(email, password)
            })

        Spacer(modifier = Modifier.weight(1f))
        GreyRedText(
            Modifier
                .padding(bottom = 10.dp)
                .align(Alignment.CenterHorizontally)
                .clickable {
                    onClickCodeNotReceived.invoke()
                },
            " " + stringResource(R.string.Verification_code_not_received),
           ""
        )
        ButtonPrimaryYellow(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = currentCode.length == 6,
            title = stringResource(R.string.Button_Next),
            onClick = {
                vm.verifyRegister(email, currentCode)
            }
        )
    }
    var isLoadingVisible by remember { mutableStateOf(false) }
    val view = LocalView.current
    Crossfade(vm.screenStateLD, label = "") { viewState ->
        when (viewState) {
            is EmailVerificationScreenState.Error -> {
                isLoadingVisible = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(view, "Register Error", viewState.message)
                }
            }
            is EmailVerificationScreenState.Loading -> {
                isLoadingVisible = true
            }
            is EmailVerificationScreenState.Success -> {
                isLoadingVisible = false
                LaunchedEffect(Unit) {
                    Toast.makeText(view.context, viewState.data.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Crossfade(targetState = vm.verifyRegistrationLD) { viewState ->
        when(viewState) {
            is ConfirmCreateAccountScreenState.Error -> {
                isLoadingVisible = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(view, "Verify Register Error", viewState.message)
                }
            }
            is ConfirmCreateAccountScreenState.Loading -> {
                isLoadingVisible = true
            }
            is ConfirmCreateAccountScreenState.Success -> {
                isLoadingVisible = false
                LaunchedEffect(Unit) {
                    onClickNext.invoke(viewState.data)
                }
            }
        }
    }

    if (isLoadingVisible) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}