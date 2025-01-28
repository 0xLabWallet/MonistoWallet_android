package com.monistoWallet.additional_wallet0x.account.top_up.ui.presentation

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.data.model.RequestPayForCardResponseModel
import com.monistoWallet.additional_wallet0x.account.pay_for_card.ui.view_model.PayViewModel
import com.monistoWallet.additional_wallet0x.root.main.ui.model.TopUpScreenState
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.ui.compose.components.HSpacer
import com.monistoWallet.ui.compose.components.TextImportantWarning
import com.monistoWallet.ui.compose.components.VSpacer
import com.monistoWallet.ui.helpers.TextHelper.copyText
import org.koin.androidx.compose.koinViewModel


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun TopUpScreen(
    payModel: RequestPayForCardResponseModel,
    selectedNetwork: String,
    selectedCurrency: String,
    minPay: Int,
    maxPay: Int,
    onBackClick: () -> Unit,
    vm: PayViewModel = koinViewModel(),
    mainVm: RootAccountViewModel = koinViewModel(),
) {
    val view = LocalView.current
    Log.d("Wallet0xTag", "TopUpScreen.payModel: $payModel")
    Log.d("Wallet0xTag", "TopUpScreen.payModel: $minPay $maxPay $selectedNetwork $selectedCurrency")
    val timeLeft by vm.timeLeft.collectAsState()
    val minutes = (timeLeft / 1000) / 60
    val seconds = (timeLeft / 1000) % 60

    LaunchedEffect(Unit) {
        vm.startCountdown()
    }

    if (vm.onTimeFinished) {
        onBackClick.invoke()
    }
    Crossfade(targetState = mainVm.rechargeSSEScreenState, label = "") {
        when (it) {
            is TopUpScreenState.Result -> {
                onBackClick.invoke()
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
            .fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            ButtonBack {
                onBackClick.invoke()
            }
            VSpacer(height = 10.dp)
            Text(
                text = stringResource(R.string.Deposit),
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold
            )
        }
        VSpacer(height = 14.dp)
        Text(
            text = selectedNetwork,
            color = Color.White,
            fontSize = 15.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        VSpacer(height = 14.dp)
        Text(
            text = selectedCurrency,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        VSpacer(height = 23.dp)

        val base64Image = payModel.qr.substringAfter("data:image/png;base64,")

        // Декодируем изображение из Base64
        val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        bitmap?.let { qrCode ->
            Image(
                modifier = Modifier
                    .padding(horizontal = 75.dp)
                    .fillMaxWidth(),
                bitmap = qrCode.asImageBitmap(),
                contentScale = ContentScale.FillWidth,
                contentDescription = null
            )
        }
        VSpacer(height = 31.dp)
        Divider(
            modifier = Modifier.padding(horizontal = 27.dp),
            color = colorResource(id = R.color.grey_3).copy(0.15f),
            thickness = 1.dp
        )
        VSpacer(height = 9.dp)
        Row {
            Text(
                text = stringResource(id = R.string.Card_address),
                color = colorResource(id = R.color.white),
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = formatAddress(payModel.address),
                color = colorResource(id = R.color.white),
                fontSize = 15.sp
            )
            HSpacer(width = 2.dp)
            Image(
                painter = painterResource(id = R.drawable.ic_copy_invite_code),
                contentDescription = "",
                Modifier
                    .size(16.dp)
                    .clickable {
                        HudHelper.show0xSuccessMessage(view, view.context.getString(R.string.Hud_Text_Copied))
                        copyText(payModel.address)
                    }
            )
        }

        Spacer(modifier = Modifier.weight(2f))
        Text(
            text = stringResource(id = R.string.Timer),
            color = colorResource(id = R.color.white),
            fontSize = 17.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        VSpacer(height = 12.dp)
        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            color = colorResource(id = R.color.white),
            fontSize = 25.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.weight(1f))
        TextImportantWarning( text = stringResource(
                R.string.Text_pay_for_min_to_max_currency_chain,
                minPay.toFloat(),
                maxPay.toFloat(),
                selectedCurrency,
                selectedNetwork
            )
        )
        VSpacer(height = 22.dp)
    }
}

private fun formatAddress(input: String): String {
    if (input.length <= 12) {
        return input
    }

    val start = input.substring(0, 6)
    val end = input.takeLast(6)

    return "$start...$end"
}
