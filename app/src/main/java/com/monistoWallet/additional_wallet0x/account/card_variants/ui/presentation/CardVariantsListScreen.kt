package com.monistoWallet.additional_wallet0x.account.card_variants.ui.presentation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.card_variants.data.model.CardLayout
import com.monistoWallet.additional_wallet0x.account.card_variants.data.model.CardVariantsModel
import com.monistoWallet.additional_wallet0x.account.card_variants.ui.model.CardVariantsScreenState
import com.monistoWallet.additional_wallet0x.account.card_variants.ui.view_model.BuyCardViewModel
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.ui.presentation.SetupPayForCard
import com.monistoWallet.additional_wallet0x.account.order_card.ui.presentation.OrderCardScreen
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.ui.model.RequestPayApplyScreenState
import com.monistoWallet.additional_wallet0x.account.pay_for_card.ui.presentation.PayScreen
import com.monistoWallet.additional_wallet0x.account.pay_for_card.ui.view_model.PayViewModel
import com.monistoWallet.additional_wallet0x.root.Constants.MAX_PAYMENT_TO_ORDER_CARD
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel
import com.monistoWallet.additional_wallet0x.root.model.RechargeSettings
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer
import org.koin.androidx.compose.koinViewModel


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun CardVariantsListScreen(
    onClickBack: (() -> Unit)? = null,
    vm0: BuyCardViewModel = koinViewModel(),
    vm: RootAccountViewModel = koinViewModel(),
    payViewModel: PayViewModel = koinViewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingScreen by remember { mutableStateOf(false) }
    var showOrderCard by remember { mutableStateOf(false) }
    var showSetupPayForCard by remember { mutableStateOf(false) }
    var selectedCard by remember { mutableStateOf<CardLayout?>(null) }

    var listCardVariants: List<CardLayout> by remember { mutableStateOf(listOf()) }
    var fullInfo: CardVariantsModel? = null
    LaunchedEffect(Unit) {
        vm0.getCurrentCardsList()
    }

    val view = LocalView.current
    Crossfade(targetState = vm0.cardVariantsScreenState, label = "") {
        when (it) {
            is CardVariantsScreenState.Loading -> {
                isLoading = true
            }

            is CardVariantsScreenState.Error -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(view, "Card variants", it.message)
                }
            }

            is CardVariantsScreenState.Result -> {
                isLoading = false
                if (it.model.card_limit_reached) {
                    LaunchedEffect(Unit) {
                        Toast.makeText(view.context, "Max cards count reached!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    fullInfo = it.model
                    listCardVariants = it.model.card_layouts
                }
            }
        }
    }
    Crossfade(targetState = vm.requestPayApplyScreenState, label = "") {
        when (it) {
            is RequestPayApplyScreenState.Loading -> {
                isLoadingScreen = true
            }

            is RequestPayApplyScreenState.Error -> {
                isLoadingScreen = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(view, "Request Pay for Card Error", it.message)
                }
            }

            is RequestPayApplyScreenState.Result -> {
                payViewModel.resetTimer()
                vm.requestPayApplyScreenState = RequestPayApplyScreenState.Null
                vm.buyCardFullData = RechargeSettings(it.model, it.baseRechargeSettings.selectedNetwork, it.baseRechargeSettings.selectedCurrency, it.baseRechargeSettings.minPay, it.baseRechargeSettings.maxPay)
                isLoadingScreen = false
            }
        }
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
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (onClickBack != null) {
                ButtonBack {
                    onClickBack.invoke()
                }
            }
            HeadText(text = stringResource(R.string.choose_a_card_type), Modifier.align(Alignment.Center))
        }
        VSpacer(height = 20.dp)
        Text(
            text = stringResource(R.string.Select_your_card),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        listCardVariants.forEach {
            VSpacer(height = 12.dp)
            CardItem(
                it, selectedCard == it
            ) {
                selectedCard = it
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        ButtonPrimaryYellow(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = selectedCard != null,
            title = stringResource(R.string.Button_Next),
            onClick = {
                showOrderCard = true
            }
        )

    }

    if (vm.buyCardFullData != null) {
        PayScreen(
            vm.buyCardFullData!!.payModel,
            vm.buyCardFullData!!.selectedNetwork,
            vm.buyCardFullData!!.selectedCurrency,
            vm.buyCardFullData!!.minPay,
            vm.buyCardFullData!!.maxPay,
            {
                vm.buyCardFullData = null
            }
        )
    }
    if (showOrderCard) {
        OrderCardScreen(selectedCard ?: return,
            fullInfo!!.monthly_fee.toFloat(),
            {
                showOrderCard = false
            }, {
                showOrderCard = false
                showSetupPayForCard = true
            }
        )
    }
    if (showSetupPayForCard) {
        if (fullInfo == null && selectedCard == null) return
        SetupPayForCard(
            selectedCard!!,
            fullInfo!!,
            {
                showOrderCard = true
                showSetupPayForCard = false
            },
            { baseSettings ->
                vm.requestPayApply(selectedCard!!.provider_id, baseSettings)
                showOrderCard = false
                showSetupPayForCard = false
                isLoading = true
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
    if (isLoadingScreen) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(R.drawable.app_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {},
                contentScale = ContentScale.Crop
            )
            CircularProgressIndicator()
        }
    }
}


@Composable
fun HeadText(text: String, modifier: Modifier) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 16.sp,
        modifier = modifier
    )

}