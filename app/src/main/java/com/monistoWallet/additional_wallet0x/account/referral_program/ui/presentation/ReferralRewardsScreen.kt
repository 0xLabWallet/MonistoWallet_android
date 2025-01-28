package com.monistoWallet.additional_wallet0x.account.referral_program.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.ui.ButtonBack
import com.monistoWallet.ui.compose.components.CellLawrenceSection
import com.monistoWallet.ui.compose.components.VSpacer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReferralRewardsScreen(onBackPressed: () -> Unit) {
    val referralRewardsList = listOf(
        ReferralsInfo("Title1", 12323112L, 930f, "Completed"),
        ReferralsInfo("Title2", 12323112L, 30.5f, "Completed"),
        ReferralsInfo("Title3", 12323112L, 90.5f, "Completed"),
        ReferralsInfo("Title4", 12323112L, 930.5f, "Completed"),
        ReferralsInfo("Title5", 12323112L, 930.5f, "Completed"),
        ReferralsInfo("Title6", 15623112L, -930.5f, "Processing"),
        ReferralsInfo("Title7", 15623112L, 530.5f, "Processing"),
        ReferralsInfo("Title8", 15623112L, 1930.5f, "Processing"),
    )
    Column(
        modifier = Modifier.padding(20.dp)
    ) {

        Row {
            ButtonBack {
                onBackPressed.invoke()
            }

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.My_reward),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(bottom = 22.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.ic_manage_2),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {

                    },
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
        ReferralGroupedList(referralRewardsList)
    }
}


data class ReferralsInfo(
    val title: String,
    val date: Long,
    val amount: Float,
    val state: String,
)
@Composable
fun ReferralGroupedList(items: List<ReferralsInfo>) {
    val groupedItems = items.groupBy { it.date }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        groupedItems.forEach { (date, itemsForDate) ->
            item {
                // Заголовок для группы
                Text(
                    text = extractDate(date),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }

            item {
                // Отображаем элементы для этой даты
                CellLawrenceSection(
                    getListComposableReferralTransactions(itemsForDate)
                )
            }
        }
    }
}
fun getListComposableReferralTransactions(list: List<ReferralsInfo>): List<@Composable () -> Unit> {
    val listItems = mutableListOf<@Composable () -> Unit>()
    list.forEach {
        listItems.add {
            ListReferralTransactionItem(content = it)
        }
    }
    return listItems
}
@Composable
fun ListReferralTransactionItem(content: ReferralsInfo) {
    Row(
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp)
    ) {
        Image(
            painter = painterResource(if (content.amount < 0) R.drawable.ic_send else R.drawable.ic_recieve),
            contentDescription = null,
            Modifier
                .clip(RoundedCornerShape(300.dp))
                .background(Color(0xFF17181A))
                .size(30.dp),
        )
        Column(
            modifier = Modifier
                .padding(start = 20.dp)
        ) {
            VSpacer(height = 4.dp)
            Text(
                text = content.title,
                fontSize = 12.sp,
                color = Color.White
            )
            VSpacer(height = 2.dp)
            Text(
                text = extractDate(content.date),
                fontSize = 8.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column {
            Text(
                text = content.amount.toString(),
                modifier = Modifier
                    .padding(end = 12.dp, top = 10.dp)
                    .align(Alignment.End),
                color = Color.White
            )
            Text(
                text = content.state,
                modifier = Modifier
                    .padding(end = 12.dp, top = 2.dp)
                    .align(Alignment.End),
                fontSize = 8.sp,
                color = Color.Gray
            )
        }


    }
}

fun extractDate(date: Long): String {
    // Определяем формат даты
    val format = SimpleDateFormat("yyyy-MM-dd | HH:mm", Locale.getDefault())
    return format.format(Date(date))
}