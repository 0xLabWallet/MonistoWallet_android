package com.monistoWallet.additional_wallet0x.account.order_card.ui.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.card_variants.data.model.CardLayout
import com.monistoWallet.additional_wallet0x.account.card_variants.ui.presentation.CardItem
import com.monistoWallet.additional_wallet0x.account.card_variants.ui.presentation.HeadText
import com.monistoWallet.additional_wallet0x.root.Constants.TERMS_URL
import com.monistoWallet.additional_wallet0x.root.openChromeWithUrl
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.additional_wallet0x.root.ui.GreyRedText
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer

@Composable
fun OrderCardScreen(card: CardLayout, monthlyFee: Float, onClickBack: () -> Unit, onClickNext: () -> Unit) {
    val context = LocalContext.current
    Image(
        painter = painterResource(R.drawable.app_bg),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            ButtonBack {
                onClickBack.invoke()
            }
            HeadText(text = stringResource(R.string.Order_A_Card), Modifier.align(Alignment.Center))
        }
        LazyColumn {
            item {
                VSpacer(height = 48.dp)
                CardItem(card, false)
                VSpacer(height = 20.dp)
                Text(text = stringResource(R.string.Payment_for_card_registration), color = Color.Gray, fontSize = 16.sp)
                VSpacer(height = 6.dp)
                Column(
                    modifier = Modifier
                        .background(color = Color(0x0DD9D9D9), shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.Card_currency), color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Text(text = "USD", color = Color.Gray, fontSize = 14.sp)
                    }
                    VSpacer(height = 10.dp)
                    Divider(
                        Modifier
                            .background(colorResource(id = R.color.grey_3).copy(0.1f))
                            .fillMaxWidth()
                            .height(1.dp))
                    VSpacer(height = 10.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.Card_opening_fee), color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Text(text = "$${card.price}", color = Color.Gray, fontSize = 14.sp)
                    }
                    VSpacer(height = 10.dp)
                    Divider(
                        Modifier
                            .background(colorResource(id = R.color.grey_3).copy(0.1f))
                            .fillMaxWidth()
                            .height(1.dp))
                    VSpacer(height = 10.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.Monthly_fee), color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Text(text = "$$monthlyFee", color = Color.Gray, fontSize = 14.sp)
                    }
                    VSpacer(height = 10.dp)
                    Divider(
                        Modifier
                            .background(colorResource(id = R.color.grey_3).copy(0.1f))
                            .fillMaxWidth()
                            .height(1.dp))
                    VSpacer(height = 10.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.Recharge_fee), color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Text(text = "${card.recharge_fee}%", color = Color.Gray, fontSize = 14.sp)
                    }
                    VSpacer(height = 10.dp)
                    Divider(
                        Modifier
                            .background(colorResource(id = R.color.grey_3).copy(0.1f))
                            .fillMaxWidth()
                            .height(1.dp))
                    VSpacer(height = 10.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.Spending_limit), color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Text(
                            text = stringResource(R.string.Spending_limits_text), color = Color.Gray, fontSize = 14.sp,
                            textAlign = TextAlign.End,
                        )
                    }
                    VSpacer(height = 10.dp)
                    Divider(
                        Modifier
                            .background(colorResource(id = R.color.grey_3).copy(0.1f))
                            .fillMaxWidth()
                            .height(1.dp))
                    VSpacer(height = 10.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.Total), color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
                        Text(text = "$${card.price + monthlyFee}", color = Color.White, fontSize = 16.sp)
                    }

                }
                GreyRedText(
                    Modifier
                        .padding(top = 10.dp).clickable {
                            openChromeWithUrl(context, TERMS_URL)
                        },
                    stringResource(R.string.The_subscription_fee_will_be_debited_automatically_Text),
                    stringResource(R.string.Terms_of_Service),
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        ButtonPrimaryYellow(title = stringResource(id = R.string.Button_Next),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onClickNext.invoke()
            })
    }
}