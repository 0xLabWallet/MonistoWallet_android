package com.monistoWallet.additional_wallet0x.account.card_found.ui.presentation

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.card_variants.data.model.CardVariantsModel
import com.monistoWallet.additional_wallet0x.account.card_variants.ui.model.CardVariantsScreenState
import com.monistoWallet.additional_wallet0x.account.freeze_card.ui.model.FreezeCardScreenState
import com.monistoWallet.additional_wallet0x.account.freeze_card.ui.presentation.ShowFreezeCardDialog
import com.monistoWallet.additional_wallet0x.account.freeze_card.ui.view_model.CardMainViewModel
import com.monistoWallet.additional_wallet0x.account.transactions.ui.presentation.GroupedList
import com.monistoWallet.additional_wallet0x.account.transactions.ui.presentation.TransactionDetailsScreen
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.ui.compose.components.HSpacer
import com.monistoWallet.ui.compose.components.VSpacer
import com.monistoWallet.ui.helpers.TextHelper.copyText
import org.koin.androidx.compose.koinViewModel


@Composable
fun CardMainScreen(
    card: Card,
    onOpenCardListClick: () -> Unit,
    onOpenFullTransaction: () -> Unit,
    onTopUp: (CardVariantsModel, Card) -> Unit,
    onShowPinCode: () -> Unit,
    vm: CardMainViewModel = koinViewModel()
) {
    Log.d("Wallet0xTag", "CardMainScreen.card: $card")
    val context = LocalView.current.context
    val view = LocalView.current
    var isLoading: Boolean by remember { mutableStateOf(false) }
    var showFreezeDialog: Boolean by remember { mutableStateOf(false) }
    var showUnfreezeDialog: Boolean by remember { mutableStateOf(false) }
    var displayingItem: com.monistoWallet.additional_wallet0x.account.transactions.data.model.Transaction? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        Row(
            Modifier.padding(horizontal = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_card_navigation_0x_2), contentDescription = "",
                modifier = Modifier
                    .clickable {
                        onOpenCardListClick.invoke()
                    }
                    .size(28.dp),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Wallet USD", color = Color.White, fontSize = 18.sp)
            Spacer(modifier = Modifier.weight(1f))
            Spacer(
                modifier = Modifier.size(20.dp)
            )
        }
        val cardImage = painterResource(id = R.drawable.empty_0x_card)

        if (card.status == "ACTIVE") {
            var cardSide by remember {
                mutableStateOf(1)
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    VSpacer(height = 40.dp)
                    if (cardSide == 1) {
                        Box(
                            modifier = Modifier
                                .size(
                                    cardImage.intrinsicSize.width.dp,
                                    cardImage.intrinsicSize.height.dp / 2f
                                )
                                .clickable {
                                    cardSide = 2
                                }
                        ) {
                            Image(
                                painter = cardImage,
                                contentDescription = null,
                                modifier = Modifier.matchParentSize()
                            )
                            Column(
                                modifier = Modifier.padding(
                                    top = 10.dp,
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 10.dp
                                )
                            ) {
                                Row {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_reverse_0x_card),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = card.balance.toString() + " USD",
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Row {
                                    Text(
                                        text = card.getFormattedCardNumber(),
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_copy_invite_code),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable {
                                                HudHelper.show0xSuccessMessage(view, view.context.getString(R.string.Hud_Text_Copied))
                                                copyText(card.card_number)
                                            }
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "",
                                    color = Color.White,
                                    fontSize = 18.sp
                                ) //${card.type} - ${card.status}
                            }
                            Image(
                                painter = painterResource(id = if (card.pay_system == "Visa") R.drawable.ic_visa else if (card.pay_system == "MasterCard") R.drawable.ic_mastercard else R.drawable.union_pay),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(60.dp)
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 16.dp)
                            )

                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(
                                    cardImage.intrinsicSize.width.dp,
                                    cardImage.intrinsicSize.height.dp / 2f
                                )
                                .clickable { cardSide = 1 }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.card_wallet_0x_backside),
                                contentDescription = null,
                                modifier = Modifier
                                    .matchParentSize()
                                    .graphicsLayer(scaleX = -1f)
                            )
                            Column(
                                modifier = Modifier
                                    .padding(top = 28.dp, bottom = 8.dp)
                            ) {
                                Divider(
                                    color = Color.Black,
                                    thickness = 40.dp
                                )
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "Date  ${card.card_expires}",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Row {
                                        Text(
                                            text = "CVV  ",
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF2C3A54)),
                                        ) {
                                            Text(
                                                text = "${card.card_cvv}",
                                                color = Color.White,
                                                fontSize = 20.sp,
                                                modifier = Modifier
                                                    .align(Alignment.Center)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                        }
                    }
                    VSpacer(height = 26.dp)
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Column(
                            Modifier
                                .height(60.dp)
                                .clickable {
                                    vm.topUpCard()
                                }
                        ) {
                            Image(
                                painter = painterResource(R.drawable.icon_send),
                                contentDescription = null,
                                Modifier
                                    .align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                stringResource(R.string.Top_Up),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White
                            )
                        }
                        HSpacer(width = 60.dp)
                        Column(
                            Modifier
                                .height(60.dp)
                                .clickable {
                                    if (!isLoading) {
                                        showFreezeDialog = true
                                    }
                                }
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_freeze),
                                contentDescription = null,
                                Modifier
                                    .align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                stringResource(R.string.Freeze),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White
                            )
                        }

                        if (card.type == "Physics") {
                            HSpacer(width = 60.dp)
                            Column(
                                Modifier
                                    .height(60.dp)
                                    .clickable {
                                        onShowPinCode.invoke()
                                    }
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_lock),
                                    contentDescription = null,
                                    Modifier
                                        .align(Alignment.CenterHorizontally)
                                )

                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    stringResource(R.string.Pin_Code),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))
                    }
                    VSpacer(height = 20.dp)
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_manage_2),
                                contentDescription = "",
                                modifier = Modifier
                                    .clickable {
                                        onOpenFullTransaction.invoke()
                                    }
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(300.dp))
                                    .background(Color(0x336E7899))
                                    .padding(4.dp)
                            )
                        }
                        Column(modifier = Modifier.fillMaxSize()) {
                            GroupedList(card.transactions) {
                                displayingItem = it
                            }
                        }
                    }
                }
            }
        } else if (card.status == "TBA") {
            VSpacer(height = 40.dp)
            Box(
                modifier = Modifier
                    .size(cardImage.intrinsicSize.width.dp, cardImage.intrinsicSize.height.dp / 2f)
            ) {
                Image(
                    painter = cardImage,
                    contentDescription = null,
                    modifier = Modifier.matchParentSize()
                )
                Column(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        CircularProgressIndicator()
                    }
                    Text(
                        text = stringResource(R.string.The_card_is_in_the_process_of_being_issued),
                        color = Color.White,
                        fontSize = 24.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center
                    )
                }
            }
            VSpacer(height = 26.dp)
        } else if (card.status == "INACTIVE") {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    VSpacer(height = 40.dp)
                    Box(
                        modifier = Modifier
                            .size(cardImage.intrinsicSize.width.dp, cardImage.intrinsicSize.height.dp / 2f)
                    ) {
                        Image(
                            painter = cardImage,
                            contentDescription = null,
                            modifier = Modifier.matchParentSize()
                        )
                        Text(
                            text = stringResource(R.string.Card_frozen),
                            color = Color.White,
                            fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    VSpacer(height = 26.dp)
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Column(
                            Modifier
                                .height(60.dp)
                                .clickable {
                                    if (!isLoading) {
                                        showUnfreezeDialog = true
                                    }
                                }
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_freeze),
                                contentDescription = null,
                                Modifier
                                    .align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                stringResource(R.string.Unfreeze),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    VSpacer(height = 20.dp)
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_manage_2),
                                contentDescription = "",
                                modifier = Modifier
                                    .clickable {
                                        onOpenFullTransaction.invoke()
                                    }
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(300.dp))
                                    .background(Color(0x336E7899))
                                    .padding(4.dp)
                            )
                        }
                        Column(modifier = Modifier.fillMaxSize()) {
                            GroupedList(card.transactions) {
                                displayingItem = it
                            }
                        }
                    }
                }
            }

        }

    }
    Crossfade(targetState = vm.freezeCardScreenState, label = "") {
        when (it) {
            is FreezeCardScreenState.Error -> {
                isLoading = false
                HudHelper.showErrorMessage(view, it.message)
            }

            is FreezeCardScreenState.Loading -> {
                isLoading = true
            }
            is FreezeCardScreenState.Null -> {
                isLoading = false
            }

            is FreezeCardScreenState.Success -> {
                isLoading = false
                val str = if (card.status == "ACTIVE") stringResource(id = R.string.This_card_has_been_unfrozen) else stringResource(id = R.string.This_card_is_frozen)
                LaunchedEffect(Unit) {
                    HudHelper.show0xSuccessMessage(view, str)
                }
                vm.clearFreezeCardState()
            }
        }
    }
    Crossfade(targetState = vm.cardVariantsScreenState, label = "") {
        when (it) {
            is CardVariantsScreenState.Error -> {
                LaunchedEffect(Unit) {
                    isLoading = false
                    HudHelper.show0xErrorMessage(view, "Card List Error", it.message)
                }
            }

            is CardVariantsScreenState.Loading -> {
                LaunchedEffect(Unit) {
                    isLoading = true
                }
            }

            is CardVariantsScreenState.Result -> {
                LaunchedEffect(Unit) {
                    isLoading = false
                    onTopUp.invoke(it.model, card)
                }
            }
        }
    }

    if (showFreezeDialog) {
        ShowFreezeCardDialog(true, {
            vm.freezeCard(card)
            showFreezeDialog = false
        }, {
            showFreezeDialog = false
        })
    }
    if (showUnfreezeDialog) {
        ShowFreezeCardDialog(false, {
            vm.unfreezeCard(card)
            showUnfreezeDialog = false
        }, {
            showUnfreezeDialog = false
        })
    }
    if (displayingItem != null) {
        TransactionDetailsScreen(displayingItem!!) {
            displayingItem = null
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

