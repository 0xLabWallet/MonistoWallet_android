package com.monistoWallet.additional_wallet0x.account.referral_program.ui.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack

@Composable
fun ReferralRulesScreen(onBackPressed: () -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        ButtonBack {
            onBackPressed.invoke()
        }
        Text(
            text = stringResource(id = R.string.Referral_Rules),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 30.dp, top = 46.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(id = R.string.Referral_Rules_H1),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(id = R.string.Referral_Rules_T1),
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(id = R.string.Referral_Rules_H2),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(id = R.string.Referral_Rules_T2),
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(id = R.string.Referral_Rules_H3),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(id = R.string.Referral_Rules_T3),
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(id = R.string.Referral_Rules_H4),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(id = R.string.Referral_Rules_T4),
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(bottom = 16.dp)
        )
    }
}