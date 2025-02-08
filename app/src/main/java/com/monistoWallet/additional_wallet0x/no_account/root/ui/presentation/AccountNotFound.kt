package com.monistoWallet.additional_wallet0x.no_account.root.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.ui.presentation.VerificationLoginScreen
import com.monistoWallet.additional_wallet0x.no_account.login.ui.presentation.screen.LoginScreen
import com.monistoWallet.additional_wallet0x.no_account.register.ui.presentation.screen.RegisterScreen
import com.monistoWallet.additional_wallet0x.no_account.register_verification.ui.presentation.VerificationRegisterScreen
import com.monistoWallet.additional_wallet0x.root.Constants.TERMS_URL
import com.monistoWallet.additional_wallet0x.root.openChromeWithUrl
import com.monistoWallet.additional_wallet0x.root.ui.GreyRedText
import com.monistoWallet.additional_wallet0x.root.usecase.obfuscateEmail
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer


@Composable
fun AccountNotFound(
    onAddAccount: (VerificationSuccessModel) -> Unit
) {
    var codeNotReceivedDialog by remember { mutableStateOf(false) }
    var registerScreen by remember { mutableStateOf(false) }
    var loginScreen by remember { mutableStateOf(false) }
    var mainScreen by remember { mutableStateOf(true) }
    var verificationLoginScreen by remember { mutableStateOf(false) }
    var verificationRegisterScreen by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (mainScreen) {
        MainScreen(onClickRegister = {
            mainScreen = false
            registerScreen = true
        }, {
            mainScreen = false
            loginScreen = true
        })
    }

    if (loginScreen) {
        LoginScreen({
            loginScreen = false
            mainScreen = true
        }, {
            loginScreen = false
            registerScreen = true
        }, { email1, password1 ->
            loginScreen = false
            verificationLoginScreen = true
            email = email1
            password = password1
        }, {
            onAddAccount.invoke(it)
        })
    }
    if (verificationLoginScreen) {
        VerificationLoginScreen(email, password, {
            loginScreen = true
            verificationLoginScreen = false
        }, {
            loginScreen = false
            verificationLoginScreen = false
            onAddAccount.invoke(it)
        }, {
            onAddAccount.invoke(it)
        })
    }



    val context = LocalContext.current
    if (registerScreen) {
        RegisterScreen({
            registerScreen = false
            mainScreen = true
        }, {
            openChromeWithUrl(context, TERMS_URL)
        }, { password1, email1 ->
            registerScreen = false
            verificationRegisterScreen = true
            password = password1
            email = email1
        })
    }
    if (verificationRegisterScreen) {
        VerificationRegisterScreen(email, password, {
            verificationRegisterScreen = false
            registerScreen = true
        }, {
            codeNotReceivedDialog = true
        }, {
            registerScreen = false
            verificationRegisterScreen = false
            onAddAccount.invoke(it)
        })
    }

    if (codeNotReceivedDialog) {
        CodeNotReceivedBottomSheet(email) {
            codeNotReceivedDialog = false
        }
    }
}

@Composable
fun MainScreen(onClickRegister: () -> Unit, onClickLogin: () -> Unit) {

    Box {
        Image(
            painter = painterResource(R.drawable.registration_img),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            VSpacer(height = 120.dp)
            ButtonPrimaryYellow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                title = stringResource(R.string.Register),
                onClick = {
                    onClickRegister.invoke()
                }
            )
            VSpacer(height = 20.dp)
            GreyRedText(
                Modifier
                    .padding(bottom = 20.dp)
                    .clickable {
                        onClickLogin.invoke()
                    },
                stringResource(R.string.Already_have_an_account),
                stringResource(R.string.Login)
            )

//            Image(painter = painterResource(id = R.drawable.ic_login_google),
//                contentDescription = "",
//                modifier = Modifier
//                    .size(32.dp)
//                    .clickable {
//
//                    }
//            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeNotReceivedBottomSheet(email: String, onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss.invoke()
        },
        containerColor = colorResource(id = R.color.grey_dialog_color),
        modifier = Modifier
            .fillMaxWidth(),
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.grey_dialog_color))
                        .padding(top = 8.dp)
                ) {
                    IconButton(
                        onClick = { onDismiss.invoke() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Close",
                            tint = colorResource(id = R.color.white)
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.Verification_code_not_received),
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 16.sp

                    )
                }
            }
        }
    ) {

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {

            Text(
                fontSize = 13.sp,
                color = Color.Gray,
                text = stringResource(R.string.We_send_a_code_to_your_email_text, obfuscateEmail(email))
            )
            VSpacer(height = 60.dp)

            ButtonPrimaryYellow(
                modifier = Modifier
                    .fillMaxWidth(),
                title = stringResource(R.string.Confirm),
                onClick = {
                    onDismiss.invoke()
                }
            )

            VSpacer(height = 80.dp)
        }
    }
}
