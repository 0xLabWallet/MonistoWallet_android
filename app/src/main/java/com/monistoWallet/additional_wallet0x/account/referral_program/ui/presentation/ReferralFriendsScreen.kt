package com.monistoWallet.additional_wallet0x.account.referral_program.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.ui.compose.components.HSpacer


@Composable
fun ReferralFriendsScreen(onBackPressed: () -> Unit) {
    Column(
        modifier = Modifier.padding(20.dp)
    ) {

        Row {
            ButtonBack {
                onBackPressed.invoke()
            }

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.My_friends),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(bottom = 22.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.weight(1f))
            HSpacer(width = 20.dp)
        }

    }
}
