package com.monistoWallet.additional_wallet0x.root.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.ui.compose.Grey9A
import com.monistoWallet.ui.compose.RedW

@Composable
fun GreyRedText(
    rootModifier: Modifier,
    greyText: String,
    redText: String
) {
    Box(
        modifier = rootModifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Grey9A, fontSize = 12.sp)) {
                append(greyText)
            }
            withStyle(style = SpanStyle(color = RedW, fontSize = 12.sp)) {
                append(" $redText")
            }
        }

        Text(
            text = annotatedString,
            modifier = Modifier.padding(bottom = 8.dp),
            textAlign = TextAlign.Center,
        )
    }
}