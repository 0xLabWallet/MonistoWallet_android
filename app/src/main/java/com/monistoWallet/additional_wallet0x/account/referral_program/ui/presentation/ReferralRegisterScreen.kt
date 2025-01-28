package com.monistoWallet.additional_wallet0x.account.referral_program.ui.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.additional_wallet0x.root.ui.InputText
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.VSpacer

@Composable
fun ReferralRegisterScreen(onBackPressed: () -> Unit) {
    var field1 by remember { mutableStateOf("") }
    var field2 by remember { mutableStateOf("") }
    var field3 by remember { mutableStateOf("") }
    var field4 by remember { mutableStateOf("") }
    var field5 by remember { mutableStateOf("") }
    var field6 by remember { mutableStateOf("") }
    var field7 by remember { mutableStateOf("") }
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                ButtonBack {
                    onBackPressed.invoke()
                }
                Text(
                    text = stringResource(id = R.string.Partner_Promotion_Recruting),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(bottom = 30.dp, top = 46.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            }
            InputText(header = stringResource(id = R.string.What_is_your_name)) {
                field1 = it
            }
            VSpacer(height = 20.dp)
            InputText(header = stringResource(id = R.string.Email)) {
                field2 = it
            }
            VSpacer(height = 20.dp)
            InputText(header = stringResource(id = R.string.Your_Referral_ID)) {
                field3 = it
            }
            VSpacer(height = 20.dp)
            InputText(header = stringResource(id = R.string.What_country_are_you_from)) {
                field4 = it
            }
            VSpacer(height = 20.dp)
            InputText(header = stringResource(id = R.string.Your_Links)) {
                field5 = it
            }
            VSpacer(height = 20.dp)
            InputText(header = stringResource(id = R.string.Introduce_your_promotional)) {
                field6 = it
            }
            VSpacer(height = 20.dp)
            InputText(header = stringResource(id = R.string.Provide_Resource_support)) {
                field7 = it
            }

            VSpacer(height = 20.dp)
            ButtonPrimaryYellow(
                title = stringResource(id = R.string.Button_Submit),
                enabled = field1.isNotEmpty() && field2.isNotEmpty() && field3.isNotEmpty() && field4.isNotEmpty() && field5.isNotEmpty() && field6.isNotEmpty() && field7.isNotEmpty(),
                onClick = {
                    onBackPressed.invoke()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}