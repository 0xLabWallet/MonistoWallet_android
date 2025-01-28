package com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.ui.presentation

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.card_variants.data.model.CardLayout
import com.monistoWallet.additional_wallet0x.account.card_variants.data.model.CardVariantsModel
import com.monistoWallet.additional_wallet0x.account.pay_for_card.ui.view_model.PayViewModel
import com.monistoWallet.additional_wallet0x.account.top_up.ui.presentation.DropDown0x
import com.monistoWallet.additional_wallet0x.account.top_up.ui.presentation.Option
import com.monistoWallet.additional_wallet0x.root.model.BaseRechargeSettings
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.TextImportantWarning
import com.monistoWallet.ui.compose.components.VSpacer
import org.koin.androidx.compose.koinViewModel


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun SetupPayForCard(
    card: CardLayout,
    info: CardVariantsModel,
    onClickBack: () -> Unit,
    onClickNext: (BaseRechargeSettings) -> Unit,
) {
    val currencyVariants = info.available_tokens.map { Option(it) }
    val networkVariants = info.available_networks.map { Option(it) }
    var selectedCurrency: Option by remember { mutableStateOf(currencyVariants[0]) }
    var selectedNetwork: Option by remember { mutableStateOf(networkVariants[0]) }
    Log.d("Wallet0xTag", "SetupPayForCard.card: $card")
    Log.d("Wallet0xTag", "SetupPayForCard.info: $info")

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
            Text(
                text = stringResource(R.string.Pay_For_Card),
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold
            )
        }
        Column {
            VSpacer(height = 30.dp)
            Row {
                Text(
                    text = stringResource(id = R.string.Currency),
                    color = colorResource(id = R.color.grey_3),
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
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
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
                DropDown0x(networkVariants) {
                    selectedNetwork = it
                }
            }
            VSpacer(height = 20.dp)
            Row {
                Text(
                    text = stringResource(id = R.string.Price_for_card),
                    color = colorResource(id = R.color.grey_3),
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "$" + card.price.toString(),
                    color = colorResource(id = R.color.grey_3),
                    fontSize = 15.sp
                )
            }
            VSpacer(height = 20.dp)
            Row {
                Text(
                    text = stringResource(id = R.string.Minimum_deposit_to_activate),
                    color = colorResource(id = R.color.grey_3),
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "$" + card.activate_min_amount.toFloat().toString(),
                    color = colorResource(id = R.color.grey_3),
                    fontSize = 15.sp
                )
            }
            VSpacer(height = 20.dp)
            Row {
                Text(
                    text = stringResource(id = R.string.Total),
                    color = colorResource(id = R.color.white),
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "$" + (card.price + card.activate_min_amount),
                    color = colorResource(id = R.color.white),
                    fontSize = 20.sp
                )
            }
            VSpacer(height = 20.dp)
        }
        TextImportantWarning( text = stringResource(
                R.string.Text_pay_for_min_to_max,
                card.recharge_min_amount + card.price,
                card.recharge_max_amount
            )
        )

        Spacer(modifier = Modifier.weight(1f))
        ButtonPrimaryYellow(title = stringResource(id = R.string.Confirm),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val baseSettings = BaseRechargeSettings(selectedNetwork.text, selectedCurrency.text,
                    (card.recharge_min_amount + card.price).toInt(), card.recharge_max_amount)
                onClickNext.invoke(baseSettings)
            }
        )
    }
}
