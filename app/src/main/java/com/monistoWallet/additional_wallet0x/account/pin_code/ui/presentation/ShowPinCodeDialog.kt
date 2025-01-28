package com.monistoWallet.additional_wallet0x.account.pin_code.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.ui.compose.components.VSpacer


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowPinCodeDialog(pinCode: String, onDismiss: () -> Unit) {
    var canShowPinCode: Boolean by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss.invoke()
        },
        containerColor = colorResource(id = R.color.grey_dialog_color),
        modifier = Modifier
            .fillMaxWidth(),
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.grey_dialog_color))
                        .padding(top = 8.dp)
                ) {
                    IconButton(
                        onClick = { onDismiss.invoke() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Close",
                            tint = colorResource(id = R.color.white)
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.To_view_on_my_PIN_code),
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 20.sp

                    )
                }
            }
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            PinCodeCell(text = if (!canShowPinCode) " " else "${pinCode[0]}")
            PinCodeCell(text = if (!canShowPinCode) " " else "${pinCode[1]}")
            PinCodeCell(text = if (!canShowPinCode) " " else "${pinCode[2]}")
            PinCodeCell(text = if (!canShowPinCode) " " else "${pinCode[3]}")
            Column {
                Image(
                    painter = painterResource(id = if (canShowPinCode) R.drawable.ic_password_showed else R.drawable.ic_password_hidden),
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        canShowPinCode = !canShowPinCode
                    }
                )
                Spacer(modifier = Modifier.height(60.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        VSpacer(height = 100.dp)
    }
}
@Composable
fun PinCodeCell(text: String) {
    Surface(
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(12.dp), // Радиус углов
        border = BorderStroke(2.dp, Color.White), // Белая обводка
        color = Color.Transparent // Прозрачный фон (или можешь задать свой)
    ) {
        Box(
            contentAlignment = Alignment.Center, // Выравнивание по центру
            modifier = Modifier
                .height(68.dp)
                .width(47.dp)
        ) {
            Text(
                text = text,
                fontSize = 30.sp,
                color = Color.White, // Цвет текста
            )
        }
    }
}

