package com.monistoWallet.additional_wallet0x.account.card_list

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.account.card_found.ui.view_model.CardFoundViewModel
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.ui.compose.components.TextImportantWarning
import com.monistoWallet.ui.compose.components.VSpacer
import org.koin.androidx.compose.koinViewModel


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun CardsListScreen(
    listCards: List<Card>,
    onBack: () -> Unit,
    onSelectCard: (Card) -> Unit,
    onOrderNewCard: () -> Unit,
    vm: CardFoundViewModel = koinViewModel()
) {

    Image(
        painter = painterResource(R.drawable.app_bg),
        contentDescription = null,
        modifier = Modifier.fillMaxSize().clickable(
            interactionSource = MutableInteractionSource(),
            indication = null
        ) {},
        contentScale = ContentScale.Crop
    )
    Column(
        Modifier.padding(12.dp)
    ) {
        Row {
            ButtonBack {
                onBack.invoke()
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.All_Wallets), color = Color.White, fontSize = 18.sp)
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listCards.size) { cardItem ->
                val selectedCard = vm.selectedCard
                CardItemView(listCards[cardItem], selectedCard?.id == listCards[cardItem].id) {
                    onSelectCard.invoke(it)
                }
            }
            item {
                VSpacer(height = 22.dp)
                Divider(
                    modifier = Modifier
                        .padding(horizontal = 100.dp)
                        .background(colorResource(id = R.color.grey_3).copy(0.15f))
                )
                if (listCards.size >= 2) {
                    VSpacer(height = 22.dp)
                    TextImportantWarning(text = stringResource(id = R.string.Cards_count_limit_reached_text))
                } else {
                    VSpacer(height = 22.dp)
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .fillMaxWidth()
                            .height(64.dp)
                            .clickable {
                                onOrderNewCard.invoke()
                            }
                    ) {
                        Row(
                            Modifier
                                .background(Color(0x0DD9D9D9))
                                .padding(16.dp)
                                .height(64.dp)
                                .align(Alignment.Center)
                        ) {
                            Text(
                                text = stringResource(id = R.string.Order_A_Card),
                                color = Color.White,
                                fontSize = 18.sp,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Image(
                                painter = painterResource(id = R.drawable.ic_square_add),
                                contentDescription = "",
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }
    }
}