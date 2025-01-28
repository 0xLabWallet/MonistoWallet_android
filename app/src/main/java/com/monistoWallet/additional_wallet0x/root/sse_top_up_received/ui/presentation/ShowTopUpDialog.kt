package com.monistoWallet.additional_wallet0x.root.sse_top_up_received.ui.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.sse_top_up_received.data.model.SSETopUpReceivedModel
import com.monistoWallet.core.providers.Translator.getString
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ShowTopUpDialog(showTopUpModel: SSETopUpReceivedModel, onClickBack: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = {
            onClickBack.invoke()
        },
        containerColor = colorResource(id = R.color.grey_dialog_color),
        modifier = Modifier
            .fillMaxWidth(),
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = { onClickBack.invoke() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Close",
                        tint = colorResource(id = R.color.white)
                    )
                }
                Text(
                    text = stringResource(id = R.string.Top_up_card_alert_success_text),
                    color = colorResource(id = R.color.white),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 20.sp
                )
            }
        }
    ) {

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            val amount = showTopUpModel.balance.toFloat()
            val message1 = getString(R.string.deposit_received, amount)

            var message2 = ""
            val charge = showTopUpModel.dept
            val months = showTopUpModel.dept_period
            if (showTopUpModel.dept != 0) {
                message2 = " " + getString(R.string.deposit_charge, charge, months)
            }
            VSpacer(height = 6.dp)
            Text(
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "$message1$message2"
            )
            VSpacer(height = 46.dp)

            ButtonPrimaryYellow(title = stringResource(id = R.string.Confirm), onClick = {
                onClickBack.invoke()
            }, modifier = Modifier.fillMaxWidth())

            VSpacer(height = 80.dp)
        }
    }
}