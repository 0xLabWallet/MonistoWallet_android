package com.monistoWallet.additional_wallet0x.account.freeze_card.ui.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowFreezeCardDialog(isFreeze: Boolean, onConfirm: () -> Unit, onDismiss: () -> Unit) {
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
                        .padding(top = 8.dp, end = 8.dp)
                ) {
                    IconButton(
                        onClick = { onDismiss.invoke() },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Close",
                            tint = colorResource(id = R.color.white)
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = stringResource(if (isFreeze) R.string.Freeze_card else R.string.Unfreeze_card),
                fontSize = 24.sp,
                color = colorResource(id = R.color.white),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
            VSpacer(height = 12.dp)
            Divider(modifier = Modifier
                .background(colorResource(id = R.color.grey_3).copy(0.15f))
                .width(140.dp)
                .align(Alignment.CenterHorizontally)
            )
            VSpacer(height = 12.dp)
            Text(
                text = stringResource(if (isFreeze) R.string.Press_confirm_to_freeze_card else R.string.Press_confirm_to_unfreeze_card),
                fontSize = 17.sp,
                color = colorResource(id = R.color.white),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                textAlign = TextAlign.Center
            )
            VSpacer(height = 40.dp)
            ButtonPrimaryYellow(title = stringResource(id = R.string.Confirm), onClick = {
                onConfirm.invoke()
            }, modifier = Modifier.fillMaxWidth())
            VSpacer(height = 100.dp)
        }
    }
}
