package com.monistoWallet.additional_wallet0x.account.root.ui.presentation

import android.os.Handler
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import com.monistoWallet.additional_wallet0x.account.card_found.ui.presentation.CardFound
import com.monistoWallet.additional_wallet0x.account.card_variants.ui.presentation.CardVariantsListScreen
import com.monistoWallet.additional_wallet0x.account.pay_for_card.ui.presentation.PayScreen
import com.monistoWallet.additional_wallet0x.account.pay_for_card.ui.view_model.PayViewModel
import com.monistoWallet.additional_wallet0x.account.top_up.ui.presentation.TopUpScreen
import com.monistoWallet.additional_wallet0x.root.main.ui.model.ErrorScreenState
import com.monistoWallet.additional_wallet0x.root.main.ui.model.TopUpScreenState
import com.monistoWallet.additional_wallet0x.root.main.ui.model.UserScreenState
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel
import com.monistoWallet.additional_wallet0x.root.model.RechargeSettings
import com.monistoWallet.additional_wallet0x.root.sse_buy_card.ui.model.BuyCardScreenState
import com.monistoWallet.additional_wallet0x.root.sse_payment_error_received.ui.presentation.ShowErrorDialog
import com.monistoWallet.additional_wallet0x.root.sse_top_up_received.data.model.SSETopUpReceivedModel
import com.monistoWallet.additional_wallet0x.root.sse_top_up_received.ui.presentation.ShowTopUpDialog
import com.monistoWallet.additional_wallet0x.root.tokens.model.SseResponseModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun AccountFound(vm: RootAccountViewModel = koinViewModel()) {
    var showTopUpDialog by remember { mutableStateOf<SSETopUpReceivedModel?>(null) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }
    Crossfade(targetState = vm.userCardsScreenStateLD, label = "") { state ->
        val key = when (state) {
            is UserScreenState.CardsFound -> "CardsFound"
            is UserScreenState.CardsNotFound -> "CardsNotFound"
            else -> "Default"
        }

        key(key) {
            when (state) {
                is UserScreenState.CardsFound -> {
                    CardFound()
                }

                is UserScreenState.CardsNotFound -> {
                    CardVariantsListScreen()
                }
            }
        }
    }
    Crossfade(targetState = vm.buyCardSSEScreenState, label = "") {
        when (it) {
            is BuyCardScreenState.Success -> {
                vm.buyCardFullData = null
                vm.buyCardSSEScreenState = BuyCardScreenState.Null
            }
        }
    }
    Crossfade(targetState = vm.rechargeSSEScreenState, label = "") {
        when (it) {
            is TopUpScreenState.Result -> {
                vm.rechargeFullData = null
                showTopUpDialog = it.response
                vm.rechargeSSEScreenState = TopUpScreenState.Null
            }
        }
    }
    Crossfade(targetState = vm.errorSSEScreenState, label = "") {
        when (it) {
            is ErrorScreenState.Error -> {
                showErrorDialog = it.text
                vm.errorSSEScreenState = ErrorScreenState.Null
            }
        }
    }

    var showTopUpScreen by remember { mutableStateOf<RechargeSettings?>(null) }
    Crossfade(targetState = vm.rechargeFullData, label = "") {
        showTopUpScreen = it
    }
    if (showTopUpScreen != null) {
        TopUpScreen(
            payModel = showTopUpScreen!!.payModel,
            selectedNetwork = showTopUpScreen!!.selectedNetwork,
            selectedCurrency = showTopUpScreen!!.selectedCurrency,
            showTopUpScreen!!.minPay,
            showTopUpScreen!!.maxPay,
            onBackClick = {
                vm.rechargeFullData = null
            }
        )
    }
    var showBuyCardScreen by remember { mutableStateOf<RechargeSettings?>(null) }
    Crossfade(targetState = vm.buyCardFullData, label = "") {
        showBuyCardScreen = it
    }
    if (showBuyCardScreen != null) {
        PayScreen(vm.buyCardFullData!!.payModel,
            vm.buyCardFullData!!.selectedNetwork,
            vm.buyCardFullData!!.selectedCurrency,
            vm.buyCardFullData!!.minPay,
            vm.buyCardFullData!!.maxPay,
            {
                vm.buyCardFullData = null
            }
        )
    }

    if (showTopUpDialog != null) {
        ShowTopUpDialog(showTopUpDialog ?: return) {
            vm.rechargeSSEScreenState = TopUpScreenState.Null
            showTopUpDialog = null
        }
    }

    if (showErrorDialog != null) {
        ShowErrorDialog(showErrorDialog ?: return) {
            showErrorDialog = null
            vm.userCardsScreenStateLD = UserScreenState.CardsNotFound(SseResponseModel(emptyList(), ""))
        }
    }
}