package com.monistoWallet.additional_wallet0x.account.referral_program.ui.presentation

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralTierDialog(onBackPressed: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = {
            onBackPressed.invoke()
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
                        onClick = { onBackPressed.invoke() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Close",
                            tint = colorResource(id = R.color.white)
                        )
                    }

                    Text(
                        text = "Tier 2",
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Divider(
                Modifier
                    .background(colorResource(id = R.color.grey_3).copy(0.15f))
                    .width(140.dp)
                    .align(Alignment.CenterHorizontally))
            Text(
                text = stringResource(id = R.string.Tier2_text),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )

            ButtonPrimaryYellow(title = stringResource(id = R.string.Confirm), onClick = {
                    onBackPressed.invoke()
                }, modifier = Modifier.fillMaxWidth()
            )
            VSpacer(height = 60.dp)
        }
    }
    
}