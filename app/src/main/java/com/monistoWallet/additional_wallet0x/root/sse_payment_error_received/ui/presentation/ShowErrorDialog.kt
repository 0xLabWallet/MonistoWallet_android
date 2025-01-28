package com.monistoWallet.additional_wallet0x.root.sse_payment_error_received.ui.presentation

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ShowErrorDialog(text: String, onClickBack: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = {
            onClickBack.invoke()
        },
        containerColor = colorResource(id = R.color.grey_dialog_color),
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
                    text = stringResource(id = R.string.Error),
                    color = colorResource(id = R.color.white),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 20.sp
                )
            }
        }
    ) {

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            VSpacer(height = 6.dp)
            Text(
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "$text"
            )
            VSpacer(height = 46.dp)

            ButtonPrimaryYellow(title = stringResource(id = R.string.Confirm), onClick = {
                onClickBack.invoke()
            }, modifier = Modifier.fillMaxWidth())

            VSpacer(height = 80.dp)
        }
    }
}