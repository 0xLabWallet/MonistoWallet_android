package com.monistoWallet.additional_wallet0x.settings.change_password.ui.presentation

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
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel.Companion.userEmail
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.additional_wallet0x.root.ui.InputTextGetCode
import com.monistoWallet.additional_wallet0x.root.ui.InputTextPassword
import com.monistoWallet.additional_wallet0x.root.usecase.isValidPassword
import com.monistoWallet.additional_wallet0x.root.usecase.obfuscateEmail
import com.monistoWallet.additional_wallet0x.settings.change_email.ui.presentation.ShowSuccessDialog
import com.monistoWallet.additional_wallet0x.settings.change_password.ui.model.ChangePasswordScreenState
import com.monistoWallet.additional_wallet0x.settings.change_password.ui.model.VerifyChangePasswordScreenState
import com.monistoWallet.additional_wallet0x.settings.change_password.ui.view_model.ChangePasswordViewModel
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer
import org.koin.androidx.compose.koinViewModel

class ChangePasswordFragment : BaseComposeFragment() {
    @Composable
    override fun GetContent(navController: NavController) {
        var oldPassword by remember {
            mutableStateOf("")
        }
        EnterOldPasswordScreen(onNextClick = { oldPassword1 ->
            oldPassword = oldPassword1
        }, onBackClick = {
            navController.popBackStack()
        })
        if (oldPassword != "") {
            ChangePasswordScreen(oldPassword, { navController.popBackStack() })
        }

    }

}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun EnterOldPasswordScreen(onNextClick: (String) -> Unit, onBackClick: () -> Boolean) {
    val context = LocalContext.current
    val view = LocalView.current
    var password by remember { mutableStateOf("") }

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
            onBackClick.invoke()
        }

        Text(
            text = stringResource(id = R.string.Change_password),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 82.dp, top = 28.dp)
                .align(Alignment.CenterHorizontally)
        )

        InputTextPassword(stringResource(id = R.string.Current_Password)) {
            password = it
        }
        Spacer(modifier = Modifier.weight(1f))
        ButtonPrimaryYellow(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = password.isNotEmpty(),
            title = stringResource(R.string.Button_Next),
            onClick = {
                if (!isValidPassword(password)) {
                    if (password.length < 8) {
                        HudHelper.show0xErrorMessage(view, "", context.getString(R.string.Alert_Min_Password_size))
                    } else {
                        HudHelper.show0xErrorMessage(view, "", context.getString(R.string.Alert_Password_Symbols))
                    }
                } else {
                    onNextClick.invoke(password)
                }
            }
        )
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ChangePasswordScreen(
    oldPassword: String,
    onBackClick: () -> Unit,
    vm: ChangePasswordViewModel = koinViewModel()
) {
    var successDialogText by remember { mutableStateOf("") }
    var isValidCode by remember { mutableStateOf(false) }
    var code by remember { mutableStateOf("") }
    var password1 by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }

    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        vm.changePassword(oldPassword)
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
    Crossfade(targetState = vm.changePasswordScreenState, label = "") {
        when (it) {
            is ChangePasswordScreenState.Error -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(view, "Change Password Error", it.message)
                }
            }

            is ChangePasswordScreenState.Loading -> {
                LaunchedEffect(Unit) {
                    isLoading = true
                }
            }
            is ChangePasswordScreenState.Result -> {
                isLoading = false
            }

        }
    }
    Crossfade(targetState = vm.verifyChangePasswordScreenState, label = "") {
        when (it) {
            is VerifyChangePasswordScreenState.Error -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(
                        view,
                        "Change Password Verification Error",
                        it.message
                    )
                }
            }

            is VerifyChangePasswordScreenState.Loading -> {
                LaunchedEffect(Unit) {
                    isLoading = true
                }
            }

            is VerifyChangePasswordScreenState.Result -> {
                isLoading = false
                successDialogText = it.model
            }

        }
    }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        ButtonBack {
            onBackClick.invoke()
        }

        Text(
            text = stringResource(id = R.string.Change_password),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 82.dp, top = 28.dp)
                .align(Alignment.CenterHorizontally)
        )

        InputTextPassword(stringResource(id = R.string.New_Password)) {
            password1 = it
        }
        VSpacer(height = 29.dp)
        InputTextPassword(stringResource(id = R.string.Confirm_New_Password)) {
            password2 = it
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
                vm.changePassword(oldPassword)
            })

        Spacer(modifier = Modifier.weight(1f))
        ButtonPrimaryYellow(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = password1.isNotEmpty() && password2.isNotEmpty() && isValidCode,
            title = stringResource(R.string.Button_Next),
            onClick = {
                if (password1 == password2) {
                    if (!isValidPassword(password1)) {
                        if (password1.length < 8) {
                            HudHelper.show0xErrorMessage(view, "", context.getString(R.string.Alert_Min_Password_size))
                        } else {
                            HudHelper.show0xErrorMessage(view, "", context.getString(R.string.Alert_Password_Symbols))
                        }
                    } else {
                        vm.verifyChangePassword(password1, code)
                    }
                } else {
                    HudHelper.show0xErrorMessage(view, "", context.getString(R.string.Alert_Passwords_not_match))
                }
            }
        )
    }

    if (successDialogText != "") {
        ShowSuccessDialog(successDialogText) {
            onBackClick.invoke()
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
