package com.monistoWallet.additional_wallet0x.account.card_found.ui.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import com.monistoWallet.additional_wallet0x.account.card_found.ui.view_model.CardFoundViewModel
import com.monistoWallet.additional_wallet0x.account.card_list.CardsListScreen
import com.monistoWallet.additional_wallet0x.account.card_variants.data.model.CardVariantsModel
import com.monistoWallet.additional_wallet0x.account.card_variants.ui.presentation.CardVariantsListScreen
import com.monistoWallet.additional_wallet0x.account.pay_for_card.ui.view_model.PayViewModel
import com.monistoWallet.additional_wallet0x.account.pin_code.ui.presentation.ShowPinCodeDialog
import com.monistoWallet.additional_wallet0x.account.top_up.ui.model.TopUpCardScreenState
import com.monistoWallet.additional_wallet0x.account.top_up.ui.presentation.SetupTopUpCard
import com.monistoWallet.additional_wallet0x.account.transactions.ui.presentation.CardTransactionsScreen
import com.monistoWallet.additional_wallet0x.root.main.ui.model.UserScreenState
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel
import com.monistoWallet.additional_wallet0x.root.model.BaseRechargeSettings
import com.monistoWallet.additional_wallet0x.root.model.RechargeSettings
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card
import com.monistoWallet.core.helpers.HudHelper
import org.koin.androidx.compose.koinViewModel


@Composable
fun CardFound(
    vm: RootAccountViewModel = koinViewModel(),
    cardFoundViewModel: CardFoundViewModel = koinViewModel(),
    topUpViewModel: PayViewModel = koinViewModel()
) {
    val context = LocalView.current.context
    var cardScreen: Card? by remember { mutableStateOf(null) }
    var showPinCode: Boolean by remember { mutableStateOf(false) }
    var cardListScreen: Boolean by remember { mutableStateOf(false) }
    var topUpCardSettings: Pair<CardVariantsModel, Card>? by remember { mutableStateOf(null) }
    var cardTransactionsScreen: Boolean by remember { mutableStateOf(false) }
    var cardNotFound: Boolean by remember { mutableStateOf(false) }
    var isLoading: Boolean by remember { mutableStateOf(false) }
    Crossfade(targetState = vm.userCardsScreenStateLD, label = "") {
        when (it) {
            is UserScreenState.CardsFound -> {
                if (cardFoundViewModel.selectedCard != null) {
                    it.sseModel.cards_list.forEach {
                        if (cardFoundViewModel.selectedCard!!.id == it.id) {
                            cardFoundViewModel.selectCard(it)
                            return@forEach
                        }
                    }
                } else {
                    cardFoundViewModel.selectCard(it.sseModel.cards_list[0])
                }
            }
        }
    }

    Crossfade(targetState = cardFoundViewModel.selectedCard, label = "") {
        if (it != null) {
            cardScreen = it
        }
    }

    if (cardScreen != null) {
        CardMainScreen(cardScreen ?: return, {
            cardListScreen = true
        }, {
            cardTransactionsScreen = true
        }, { variants, card ->
            topUpCardSettings = Pair(variants, card)
        }, {
            showPinCode = true
        })
    }

    if (cardListScreen) {
        CardsListScreen(vm.getAllCardsList(),
            onBack = {
                cardListScreen = false
            },
            onSelectCard = {
                cardListScreen = false
                cardFoundViewModel.selectCard(it)
            },
            onOrderNewCard = {
                cardNotFound = true
            }
        )
    }
    if (cardNotFound) {
        CardVariantsListScreen({
            cardNotFound = false
        })
    }
    if (cardTransactionsScreen) {
        CardTransactionsScreen(cardFoundViewModel.selectedCard ?: return, {
            cardTransactionsScreen = false
        })
    }

    LaunchedEffect(Unit) {
        topUpCardSettings = null
    }


    if (topUpCardSettings != null) {
        SetupTopUpCard(
            card = topUpCardSettings!!.second,
            info = topUpCardSettings!!.first,
            onClickBack = {
                topUpCardSettings = null
            },
            onClickNext = { network, token, _minPay, _maxPay ->
                vm.requestRecharge(topUpCardSettings!!.second, BaseRechargeSettings(network, token, _minPay, _maxPay))
            })
    }

    val view = LocalView.current
    Crossfade(targetState = vm.topUpCardScreenState, label = "") {
        when (it) {
            is TopUpCardScreenState.Error -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(view, "Top Up Card Error", it.message)
                }
            }

            is TopUpCardScreenState.Loading -> {
                LaunchedEffect(Unit) {
                    isLoading = true
                }
            }

            is TopUpCardScreenState.Success -> {
                LaunchedEffect(Unit) {
                    vm.topUpCardScreenState = TopUpCardScreenState.Null
                    isLoading = false
                    topUpViewModel.resetTimer()
                    vm.rechargeFullData = RechargeSettings(it.model.payModel, it.model.selectedNetwork, it.model.selectedCurrency, it.model.minPay, it.model.maxPay)
                }
            }
        }
    }

    if (showPinCode) {
        ShowPinCodeDialog("0000") {
            showPinCode = false
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