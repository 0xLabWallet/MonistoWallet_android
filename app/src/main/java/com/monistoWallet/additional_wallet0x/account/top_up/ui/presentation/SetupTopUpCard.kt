package com.monistoWallet.additional_wallet0x.account.top_up.ui.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.card_variants.data.model.CardVariantsModel
import com.monistoWallet.additional_wallet0x.account.pay_for_card.ui.view_model.PayViewModel
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.TextImportantWarning
import com.monistoWallet.ui.compose.components.VSpacer
import org.koin.androidx.compose.koinViewModel


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun SetupTopUpCard(
    card: Card,
    info: CardVariantsModel,
    onClickBack: () -> Unit,
    onClickNext: (String, String, Int, Int) -> Unit
) {
    val currencyVariants = info.available_tokens.map { Option(it) }
    val networkVariants = info.available_networks.map { Option(it) }
    var selectedCurrency: Option by remember { mutableStateOf(currencyVariants[0]) }
    var selectedNetwork: Option by remember { mutableStateOf(networkVariants[0]) }
    Log.d("Wallet0xTag", "SetupTopUpCard.card: $card")
    Log.d("Wallet0xTag", "SetupTopUpCard.info: $info")

    var minPay = 0
    var maxPay = 0
    info.card_layouts.forEach {
        if (it.provider_id == card.card_layout_provider_id) {
            minPay = it.recharge_min_amount
            maxPay = it.recharge_max_amount
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
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            ButtonBack {
                onClickBack.invoke()
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
        Column(
            Modifier
                .padding(top = 38.dp, end = 12.dp)
        ) {
            Row {
                Text(
                    text = stringResource(id = R.string.Currency),
                    color = colorResource(id = R.color.grey_3),
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                DropDown0x(currencyVariants) {
                    selectedCurrency = it
                }
            }
            VSpacer(height = 20.dp)
            Row {
                Text(
                    text = stringResource(id = R.string.Deposit_Network),
                    color = colorResource(id = R.color.grey_3),
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                DropDown0x(networkVariants) {
                    selectedNetwork = it
                }
            }
            VSpacer(height = 20.dp)
            Row {
                Text(
                    text = stringResource(id = R.string.Minimum_deposit),
                    color = colorResource(id = R.color.grey_3),
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "$${minPay.toFloat()}",
                    color = colorResource(id = R.color.grey_3),
                    fontSize = 15.sp
                )
            }
            VSpacer(height = 20.dp)
            Row {
                Text(
                    text = stringResource(id = R.string.Subscriber_debt),
                    color = colorResource(id = R.color.grey_3),
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "$${info.subscription_debt.toFloat()}",
                    color = colorResource(id = R.color.grey_3),
                    fontSize = 15.sp
                )
            }
            VSpacer(height = 24.dp)
            Row {
                Text(
                    text = stringResource(id = R.string.Total),
                    color = colorResource(id = R.color.white),
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "$${(info.subscription_debt + minPay).toFloat()}",
                    color = colorResource(id = R.color.white),
                    fontSize = 18.sp
                )
            }
            VSpacer(height = 24.dp)
            TextImportantWarning(
                text = stringResource(R.string.You_can_top_up_in_diapasone, minPay + info.subscription_debt, maxPay)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        ButtonPrimaryYellow(title = stringResource(id = R.string.Button_Next),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onClickNext.invoke(selectedNetwork.text, selectedCurrency.text, minPay, maxPay)
            }
        )
    }
}

data class Option(
    val text: String,
)

@Composable
fun DropDown0x(
    listOptions: List<Option>,
    onOptionSelected: (Option) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(listOptions.first()) }

    Column(
        modifier = Modifier
            .width(100.dp)
    ) {
        Row(
            Modifier
                .background(color = Color(0x0DD9D9D9), shape = RoundedCornerShape(10.dp))
                .clickable { expanded = !expanded }
                .fillMaxWidth()
        ) {
            Column {
                VSpacer(height = 4.dp)
                Image(
                    painter = painterResource(id = if (!expanded) R.drawable.ic_down_24 else R.drawable.ic_arrow_big_up_20),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colorResource(id = R.color.main_app_blue))
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = selectedOption.text,
                modifier = Modifier
                    .padding(8.dp),
                color = colorResource(id = R.color.white),
                fontSize = 15.sp
            )
        }
        if (expanded) {
            VSpacer(height = 2.dp)
            Column(
                modifier = Modifier
                    .background(color = Color(0x0DD9D9D9), shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .width(100.dp)
            ) {
                LazyColumn {
                    items(listOptions.size) { item ->
                        Text(
                            listOptions[item].text,
                            fontSize = 15.sp,
                            color = Color.White,
                            modifier = Modifier
                                .clickable {
                                    selectedOption = listOptions[item]
                                    expanded = false
                                    onOptionSelected.invoke(listOptions[item])
                                }
                                .fillMaxWidth().padding(bottom = if (listOptions.size > 1) 6.dp else 0.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}
