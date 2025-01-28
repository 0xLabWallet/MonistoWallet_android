package com.monistoWallet.additional_wallet0x.account.card_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card
import com.monistoWallet.ui.compose.components.HSpacer


@Composable
fun CardItemView(cardItem: Card, isCardSelected: Boolean, onCardClick: (Card) -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onCardClick.invoke(cardItem)
            }
            .border(
                width = 1.dp, // Обводка 1dp
                color = if (isCardSelected) colorResource(id = R.color.main_app_blue).copy(alpha = 0.5f) else colorResource(
                    id = R.color.transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            Modifier
                .background(Color(0x0DD9D9D9))
                .height(64.dp)
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(64.dp)
                    .width(94.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.card_0x_example),
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()
                )
            }
            HSpacer(width = 4.dp)
            Column(
                Modifier.padding(top = 4.dp)
            ) {
                Text(text = "${cardItem.type} - ${cardItem.status}", color = Color.White, fontSize = 16.sp)
                Text(text = cardItem.getHiddenCardNumber(), color = Color.White, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$" + cardItem.balance,
                color = Color.Gray,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}