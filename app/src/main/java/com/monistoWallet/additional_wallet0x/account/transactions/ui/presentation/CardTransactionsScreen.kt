package com.monistoWallet.additional_wallet0x.account.transactions.ui.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.transactions.data.model.Transaction
import com.monistoWallet.additional_wallet0x.account.transactions.ui.model.TransactionsScreenState
import com.monistoWallet.additional_wallet0x.account.transactions.ui.view_model.CardTransactionsViewModel
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.ui.compose.components.VSpacer
import org.koin.androidx.compose.koinViewModel


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun CardTransactionsScreen(
    card: Card,
    onBackClick: () -> Unit,
    vm: CardTransactionsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var displayingItem: Transaction? by remember {
        mutableStateOf(
            null
        )
    }
    var isLoading: Boolean by remember { mutableStateOf(false) }
    var transactionsList: List<Transaction> by remember { mutableStateOf(listOf()) }
    LaunchedEffect(Unit) {
        vm.getCardTransactions(card)
    }
    DisposableEffect(key1 = Unit) {
        onDispose {
            vm.clearHistory()
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
        Modifier.padding(12.dp)
    ) {
        Row {
            ButtonBack {
                onBackClick.invoke()
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.Transactions_All),
                color = Color.White,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        GroupedFullList(items = transactionsList) {
            displayingItem = it
        }
    }
    val view = LocalView.current
    Crossfade(targetState = vm.cardTransactionsScreenState, label = "") {
        when (it) {
            is TransactionsScreenState.NotFound -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "Transactions not found", Toast.LENGTH_SHORT).show()
                }
            }
            is TransactionsScreenState.Error -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    HudHelper.show0xErrorMessage(view, "Transactions Error", it.message)
                }
            }
            is TransactionsScreenState.Loading -> {
                isLoading = true
            }
            is TransactionsScreenState.Success -> {
                isLoading = false
                LaunchedEffect(Unit) {
                    transactionsList = it.list
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

    if (displayingItem != null) {
        TransactionDetailsScreen(displayingItem!!) {
            displayingItem = null
        }
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun TransactionDetailsScreen(displayingItem: Transaction, onBackClick: () -> Unit) {
    Log.d("Wallet0xTag", "TransactionDetailsScreen.displayingItem: $displayingItem")

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
        Modifier.padding(12.dp)
    ) {

        Row {
            ButtonBack {
                onBackClick.invoke()
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.Transaction_details),
                color = Color.White,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(24.dp))
        }
        VSpacer(height = 45.dp)


        Image(
            painter = painterResource(id = if (displayingItem.change_amount > 0) R.drawable.ic_recieve else R.drawable.ic_send),
            contentDescription = "",
            Modifier
                .size(48.dp)
                .padding(4.dp)
                .clip(RoundedCornerShape(360.dp))
                .align(Alignment.CenterHorizontally)
                .background(colorResource(id = R.color.main_app_blue))
        )

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "USD ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.white),
                modifier = Modifier.align(Alignment.Bottom).padding(bottom = 4.dp)
            )
            Text(
                text = "${displayingItem.change_amount}",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.white),
                modifier = Modifier.align(Alignment.Bottom)
            )
        }

        VSpacer(height = 64.dp)

        Row {
            Text(text = stringResource(id = R.string.Card), fontSize = 15.sp, color = colorResource(id = R.color.white))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Card", fontSize = 15.sp, color = colorResource(id = R.color.grey_3))
        }
        VSpacer(height = 19.dp)
        Row {
            Text(text = stringResource(id = R.string.Transaction_status), fontSize = 15.sp, color = colorResource(id = R.color.white))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = displayingItem.status.toString(), fontSize = 15.sp, color = colorResource(id = R.color.grey_3))
        }
        VSpacer(height = 19.dp)
        Row {
            Text(text = stringResource(id = R.string.Paid_on), fontSize = 15.sp, color = colorResource(id = R.color.white))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = displayingItem.created.replaceAfter(".", "").dropLast(1), fontSize = 15.sp, color = colorResource(id = R.color.grey_3))
        }
        VSpacer(height = 19.dp)
        Row {
            Text(text = stringResource(id = R.string.Transaction_ID), fontSize = 15.sp, color = colorResource(id = R.color.white))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = displayingItem.id.toString(), fontSize = 15.sp, color = colorResource(id = R.color.grey_3))
        }
        VSpacer(height = 19.dp)

    }
}
