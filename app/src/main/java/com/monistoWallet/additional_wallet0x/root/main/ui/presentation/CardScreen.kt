package com.monistoWallet.additional_wallet0x.root.main.ui.presentation

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.root.ui.presentation.AccountFound
import com.monistoWallet.additional_wallet0x.no_account.root.ui.presentation.AccountNotFound
import com.monistoWallet.additional_wallet0x.root.main.ui.model.RootCardScreenState
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel.Companion.userEmail
import com.monistoWallet.additional_wallet0x.root.sse_top_up_received.data.model.SSETopUpReceivedModel
import com.monistoWallet.additional_wallet0x.root.usecase.obfuscateEmail
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.core.providers.Translator.getString
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CardScreen(vm: RootAccountViewModel = koinViewModel()) {
    var isLoading by remember { mutableStateOf(false) }
    val view = LocalView.current

    Crossfade(targetState = vm.screenStateLD, label = "") {
        when (it) {
            is RootCardScreenState.NoAccount -> {
                isLoading = false
                AccountNotFound(onAddAccount = { accountData ->
                    vm.saveAccountTokenAndUpdate(accountData)
                })
            }

            is RootCardScreenState.Account -> {
                isLoading = false
                if (userEmail == "logout user") {
                    AccountNotFound(onAddAccount = { accountData ->
                        vm.saveAccountTokenAndUpdate(accountData)
                    })
                } else {
                    AccountFound()
                }
            }

            is RootCardScreenState.Loading -> {
                LaunchedEffect(Unit) {
                    isLoading = true
                }
            }

            is RootCardScreenState.Error -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(view, "Card Error", it.message)
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
