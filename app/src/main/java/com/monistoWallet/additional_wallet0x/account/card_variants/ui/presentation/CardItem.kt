package com.monistoWallet.additional_wallet0x.account.card_variants.ui.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.card_variants.data.model.CardLayout
import com.monistoWallet.ui.compose.components.HSpacer
import com.monistoWallet.ui.compose.components.VSpacer


@Composable
fun CardItem(card: CardLayout, isSelected: Boolean, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .background(color = Color(0x0DD9D9D9), shape = RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .height(80.dp)
            .clickable {
                onClick.invoke()
            }
            .border(
                border = if (isSelected) BorderStroke(
                    2.dp,
                    colorResource(id = R.color.main_app_blue)
                ) else BorderStroke(0.dp, Color.Transparent),
                shape = RoundedCornerShape(10.dp),
            )
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.wallet0x_card_img),
            contentDescription = null,
            modifier = Modifier
                .height(51.dp)
                .width(92.dp)
                .align(Alignment.CenterVertically),
            contentScale = ContentScale.Crop
        )
        HSpacer(width = 16.dp)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                text = card.card_type + " card",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = card.description,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Thin
            )
            Row {
                val tags = mutableListOf<CardLayout.Companion.CardTag>()
                val isApple = card.parameters.contains("apple")
                val isGoogle = card.parameters.contains("google")
                val isNoKyc = card.parameters.contains("no_kyc")
                val isATM = card.parameters.contains("ATM")
                if (isApple) {
                    tags.add(CardLayout.Companion.CardTag.APAY)
                }
                if (isGoogle) {
                    tags.add(CardLayout.Companion.CardTag.GPAY)
                }
                if (isNoKyc) {
                    tags.add(CardLayout.Companion.CardTag.NOKYC)
                } else {
                    tags.add(CardLayout.Companion.CardTag.KYC)
                }
                if (isATM) {
                    tags.add(CardLayout.Companion.CardTag.ATM)
                }
                tags.forEach {
                    Image(
                        painter = painterResource(id = it.tagId),
                        contentDescription = "",
                        modifier = Modifier.size(40.dp)
                    )
                    HSpacer(width = 4.dp)
                }
            }
            VSpacer(4.dp)
        }
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {

            Text(
                text = "$" + card.price.toString(),
                color = Color.White,
                fontSize = 16.sp,
            )
            if (false) {
                Text(
                    text = "per month",
                    color = Color.Gray,
                    fontSize = 10.sp,
                )
            }
        }
    }
}