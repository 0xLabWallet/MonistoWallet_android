package com.monistoWallet.additional_wallet0x.settings.logout.ui.presentation

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel.Companion.userEmail
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.additional_wallet0x.settings.logout.ui.model.LogoutScreenState
import com.monistoWallet.additional_wallet0x.settings.logout.ui.view_model.LogoutViewModel
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.ui.compose.components.ButtonPrimaryWhite
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.HSpacer
import com.monistoWallet.ui.compose.components.VSpacer
import org.koin.androidx.compose.koinViewModel

class LogoutFragment : BaseComposeFragment() {
    @Composable
    override fun GetContent(navController: NavController) {

        LogoutScreen(navController)
    }

}

@Composable
fun LogoutScreen(
    navController: NavController,
    vm: LogoutViewModel = koinViewModel(),
    cardMainViewModel: RootAccountViewModel = koinViewModel(),
) {
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF14151A))
            .padding(20.dp)
    ) {

        ButtonBack {
            navController.popBackStack()
        }

        Text(
            text = stringResource(id = R.string.Log_Out),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 42.dp, top = 28.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(id = R.string.Logout_Text),
            fontSize = 15.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(1f))
        ButtonPrimaryYellow(
            modifier = Modifier
                .fillMaxWidth(),
            title = stringResource(R.string.Logout_Confirm),
            onClick = {
                showDialog = true
            }
        )
    }
    val view = LocalView.current
    Crossfade(targetState = vm.logoutScreenStateLD, label = "") {
        when (it) {
            is LogoutScreenState.Error -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(view, "Logout Error", it.message)
                    navController.popBackStack()
                }
            }

            is LogoutScreenState.Loading -> {
                LaunchedEffect(Unit) {
                    isLoading = true
                }
            }

            is LogoutScreenState.Result -> {
                isLoading = false
                cardMainViewModel.logout()
                navController.popBackStack()
            }

        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
            },
            text = {
                Text(
                    text = stringResource(R.string.Are_you_sure_you_want_to_delete_your_account),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Row {
                    HSpacer(width = 40.dp)
                    Text(
                        text = stringResource(id = R.string.Button_Cancel),
                        color = Color(0xFF4692FD),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            showDialog = false
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(id = R.string.Confirm),
                        color = Color(0xFFF44238),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            vm.logout()
                            showDialog = false
                        }
                    )
                    HSpacer(width = 40.dp)
                }
            },
            dismissButton = {
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color(0xFF222324)
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
fun LogOutDialogDialog(onBackPressed: () -> Unit, onConfirm: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = {
            onBackPressed.invoke()
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
                    Text(
                        text = stringResource(id = R.string.Alert),
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(
                        onClick = { onBackPressed.invoke() },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Close",
                            tint = colorResource(id = R.color.white)
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Divider(
                Modifier
                    .background(colorResource(id = R.color.grey_3).copy(0.15f))
                    .width(140.dp)
                    .align(Alignment.CenterHorizontally))
            VSpacer(height = 12.dp)
            Text(
                text = stringResource(R.string.Logout_dialog_text),
                fontSize = 13.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
            VSpacer(height = 20.dp)
            Row {
                ButtonPrimaryWhite(title = stringResource(id = R.string.Button_Cancel), onClick = {
                        onBackPressed.invoke()
                    }, modifier = Modifier.weight(1f)
                )

                HSpacer(width = 12.dp)

                ButtonPrimaryYellow(title = stringResource(id = R.string.Confirm), onClick = {
                        onConfirm.invoke()
                    }, modifier = Modifier.weight(1f)
                )
            }
            VSpacer(height = 60.dp)
        }
    }

}