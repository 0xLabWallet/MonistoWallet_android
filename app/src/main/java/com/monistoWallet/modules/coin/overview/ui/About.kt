package com.monistoWallet.modules.coin.overview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.monistoWallet.R
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.CellSingleLineClear
import com.monistoWallet.ui.compose.components.DescriptionMarkdown
import com.monistoWallet.ui.compose.components.VSpacer
import com.monistoWallet.ui.compose.components.body_leah
import com.monistoWallet.ui.compose.components.subhead2_jacob

@Preview
@Composable
fun AboutPreview() {
    ComposeAppTheme {
        About(text = "Bitcoin is a cryptocurrency and worldwide payment system. It is the first decentralized digital currency, as the system works without a central bank or single administrator. Bitcoin is a cryptocurrency and worldwide payment system. It is the first decentralized digital currency, as the system works without a central bank or single administrator. Bitcoin is a cryptocurrency and worldwide payment system. It is the first decentralized digital currency, as the system works without a central bank or single administrator. Bitcoin is a cryptocurrency and worldwide payment system. It is the first decentralized digital currency, as the system works without a central bank or single administrator. Bitcoin is a cryptocurrency")
    }
}

@Composable
fun About(text: String) {
    Column {
        CellSingleLineClear(borderTop = true) {
            body_leah(text = stringResource(id = R.string.CoinPage_Overview))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ComposeAppTheme.colors.lawrence)
        ) {
            DescriptionMarkdown(
                text = text,
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                subhead2_jacob(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.CoinPage_AiGeneratedText),
                )
            }
            VSpacer(12.dp)
        }
    }
}
