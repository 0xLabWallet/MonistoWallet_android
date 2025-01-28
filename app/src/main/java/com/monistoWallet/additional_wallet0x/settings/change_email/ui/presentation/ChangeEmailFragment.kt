package com.monistoWallet.additional_wallet0x.settings.change_email.ui.presentation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel.Companion.userEmail
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.additional_wallet0x.root.ui.InputText
import com.monistoWallet.additional_wallet0x.root.ui.InputTextClear
import com.monistoWallet.additional_wallet0x.root.ui.InputTextGetCode
import com.monistoWallet.additional_wallet0x.root.ui.InputTextPassword
import com.monistoWallet.additional_wallet0x.root.usecase.isValidEmail
import com.monistoWallet.additional_wallet0x.root.usecase.obfuscateEmail
import com.monistoWallet.additional_wallet0x.settings.change_email.ui.model.ChangeEmailScreenState
import com.monistoWallet.additional_wallet0x.settings.change_email.ui.model.VerifyChangeEmailScreenState
import com.monistoWallet.additional_wallet0x.settings.change_email.ui.view_model.ChangeEmailViewModel
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer
import org.koin.androidx.compose.koinViewModel

class ChangeEmailFragment : BaseComposeFragment() {
    @Composable
    override fun GetContent(navController: NavController) {
        var oldEmail by remember {
            mutableStateOf("")
        }
        EnterOldEmailScreen(onNextClick = { oldEmail1 ->
            oldEmail = oldEmail1
        }, onBackClick = {
            navController.popBackStack()
        })
        if (oldEmail != "") {
            ChangeEmailScreen(navController, oldEmail, { navController.popBackStack() })
        }
    }
}


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun EnterOldEmailScreen(onNextClick: (String) -> Unit, onBackClick: () -> Boolean) {
    var email by remember { mutableStateOf(userEmail) }
    var isValidEmail by remember { mutableStateOf(false) }

    Image(
        painter = painterResource(R.drawable.app_bg),
        contentDescription = null,
        modifier = Modifier.fillMaxSize().clickable(
            interactionSource = MutableInteractionSource(),
            indication = null
        ) {},
        contentScale = ContentScale.Crop
    )
    val view = LocalView.current
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        ButtonBack {
            onBackClick.invoke()
        }

        Text(
            text = stringResource(id = R.string.Change_email),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 82.dp, top = 28.dp)
                .align(Alignment.CenterHorizontally)
        )

        InputText(stringResource(id = R.string.Current_Email), email, false) {
            isValidEmail = isValidEmail(it)
            email = it
        }
        Spacer(modifier = Modifier.weight(1f))
        ButtonPrimaryYellow(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = true,
            title = stringResource(R.string.Button_Next),
            onClick = {
                onNextClick.invoke(email)
            }
        )
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ChangeEmailScreen(
    navController: NavController,
    oldEmail: String,
    onBackClick: () -> Unit,
    vm: ChangeEmailViewModel = koinViewModel()
) {
    var successDialogText by remember { mutableStateOf("") }
    var isValidCode by remember { mutableStateOf(false) }
    var isValidEmail1 by remember { mutableStateOf(false) }
    var code by remember { mutableStateOf("") }
    var email1 by remember { mutableStateOf("") }
    var isValidEmail2 by remember { mutableStateOf(false) }
    var email2 by remember { mutableStateOf("") }

    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        vm.changeEmail(oldEmail)
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
    val view = LocalView.current
    Crossfade(targetState = vm.changeEmailScreenState, label = "") {
        when (it) {
            is ChangeEmailScreenState.Error -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(view, "Change Email Error", it.message)
                }
            }

            is ChangeEmailScreenState.Loading -> {
                LaunchedEffect(Unit) {
                    isLoading = true
                }
            }
            is ChangeEmailScreenState.Result -> {
                isLoading = false
            }

        }
    }
    Crossfade(targetState = vm.verifyChangeEmailScreenState, label = "") {
        when (it) {
            is VerifyChangeEmailScreenState.Error -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(
                        view,
                        "Change Email Verification Error",
                        it.message
                    )
                }
            }

            is VerifyChangeEmailScreenState.Loading -> {
                LaunchedEffect(Unit) {
                    isLoading = true
                }
            }

            is VerifyChangeEmailScreenState.Result -> {
                isLoading = false
                successDialogText = it.model
                RootAccountViewModel.userEmail = email1
            }

        }
    }

    if (successDialogText != "") {
        ShowSuccessDialog(successDialogText) {
            onBackClick.invoke()
        }
    }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        ButtonBack {
            onBackClick.invoke()
        }

        Text(
            text = stringResource(id = R.string.Change_email),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 82.dp, top = 28.dp)
                .align(Alignment.CenterHorizontally)
        )

        InputTextClear(stringResource(id = R.string.New_Email)) {
            isValidEmail1 = isValidEmail(it)
            email1 = it
        }
        VSpacer(height = 29.dp)
        InputTextClear(stringResource(id = R.string.Confirm_New_Email)) {
            isValidEmail2 = isValidEmail(it)
            email2 = it
        }
        VSpacer(height = 40.dp)
        InputTextGetCode(
            header = stringResource(R.string.Enter_the_6_digit_code_sent_to) + " " + obfuscateEmail(
                userEmail
            ),
            onValueChanged = {
                isValidCode = it.length == 6
                code = it
            },
            onGetCode = {
                vm.changeEmail(oldEmail)
            })

        Spacer(modifier = Modifier.weight(1f))
        ButtonPrimaryYellow(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = isValidEmail1 && isValidEmail2 && email1 == email2 && isValidCode,
            title = stringResource(R.string.Button_Next),
            onClick = {
                vm.verifyChangeEmail(oldEmail, email1, code)
            }
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowSuccessDialog(text: String, onDismiss: () -> Unit) {
    var canShowPinCode: Boolean by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss.invoke()
        },
        containerColor = colorResource(id = R.color.grey_dialog_color),
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.grey_dialog_color))
                .padding(top = 8.dp, start = 20.dp, end = 20.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_success),
                contentDescription = "",
                modifier = Modifier
                    .clickable {
                        canShowPinCode = !canShowPinCode
                    }
                    .align(Alignment.CenterHorizontally)
            )
            VSpacer(height = 12.dp)
            Text(
                text = text,
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            VSpacer(height = 24.dp)
            ButtonPrimaryYellow(
                modifier = Modifier
                    .fillMaxWidth(),
                title = stringResource(R.string.Button_Next),
                onClick = {
                    onDismiss.invoke()
                }
            )
            VSpacer(height = 60.dp)
        }
    }
}