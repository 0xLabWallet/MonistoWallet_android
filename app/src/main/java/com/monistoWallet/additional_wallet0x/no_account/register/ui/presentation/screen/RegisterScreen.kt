package com.monistoWallet.additional_wallet0x.no_account.register.ui.presentation.screen

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.card_variants.ui.presentation.HeadText
import com.monistoWallet.additional_wallet0x.no_account.register_verification.ui.model.EmailVerificationScreenState
import com.monistoWallet.additional_wallet0x.no_account.register.ui.view_model.RegistrationViewModel
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.additional_wallet0x.root.ui.GreyRedText
import com.monistoWallet.additional_wallet0x.root.ui.InputText
import com.monistoWallet.additional_wallet0x.root.ui.InputTextPassword
import com.monistoWallet.additional_wallet0x.root.usecase.isValidEmail
import com.monistoWallet.additional_wallet0x.root.usecase.isValidPassword
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer
import org.koin.androidx.compose.koinViewModel


@Composable
fun RegisterScreen(
    onClickBack: () -> Unit,
    onClickTermsOfService: () -> Unit,
    onClickNext: (String, String) -> Unit,
    vm: RegistrationViewModel = koinViewModel()
) {
    val view = LocalView.current
    val context = LocalContext.current
    var isLoadingVisible by remember { mutableStateOf(false) }
    var password1 by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
    var isValidEmail by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        LazyColumn {
            item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ButtonBack {
                            onClickBack.invoke()
                        }
                        Text(
                            text = stringResource(id = R.string.Register),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .padding(bottom = 20.dp)
                                .align(Alignment.Center)
                        )
                    }

                    InputText(stringResource(id = R.string.Email)) {
                        isValidEmail = isValidEmail(it)
                        email = it
                    }
                    VSpacer(height = 20.dp)
                    InputTextPassword(stringResource(id = R.string.Password)) {
                        password1 = it
                    }
                    VSpacer(height = 20.dp)
                    InputTextPassword(stringResource(id = R.string.Confirm_Password)) {
                        password2 = it
                    }
                    VSpacer(height = 20.dp)
                    InputText(stringResource(R.string.Referral_Id_Optional)) {
                        //TODO referral id
                    }
                    VSpacer(height = 100.dp)

            }
        }
        Spacer(modifier = Modifier.weight(1f))

        GreyRedText(
            Modifier
                .padding(bottom = 10.dp)
                .align(Alignment.CenterHorizontally)
                .clickable {
                    onClickTermsOfService.invoke()
                },
            stringResource(R.string.By_creating_an_account_you_agree_to_our),
            stringResource(R.string.Terms_of_Service)
        )
        ButtonPrimaryYellow(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = isValidEmail && password1.isNotEmpty() && password2.isNotEmpty(),
            title = stringResource(R.string.Button_Next),
            onClick = {
                if (password1 == password2) {
                    if (!isValidPassword(password1)) {
                        if (password1.length < 8) {
                            HudHelper.show0xErrorMessage(
                                view,
                                "",
                                context.getString(R.string.Alert_Min_Password_size)
                            )
                        } else {
                            HudHelper.show0xErrorMessage(
                                view,
                                "",
                                context.getString(R.string.Alert_Password_Symbols)
                            )
                        }
                    } else {
                        vm.getCodeToRegister(email, password1)
                    }
                } else {
                    HudHelper.show0xErrorMessage(
                        view,
                        "",
                        context.getString(R.string.Alert_Passwords_not_match)
                    )
                }
            }
        )
    }
    Crossfade(vm.screenStateLD, label = "") { viewState ->
        when (viewState) {
            is EmailVerificationScreenState.Error -> {
                isLoadingVisible = false
                LaunchedEffect(Unit) {
                    HudHelper.showErrorMessage(view, viewState.message)
                }
            }
            is EmailVerificationScreenState.Loading -> {
                isLoadingVisible = true
            }
            is EmailVerificationScreenState.Success -> {
                isLoadingVisible = false
                LaunchedEffect(Unit) {
                    Toast.makeText(context, viewState.data.message, Toast.LENGTH_SHORT).show()
                    onClickNext.invoke(password1, email)
                    vm.screenStateLD = EmailVerificationScreenState.Null
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