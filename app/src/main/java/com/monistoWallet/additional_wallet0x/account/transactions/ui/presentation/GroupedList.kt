package com.monistoWallet.additional_wallet0x.account.transactions.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.tokens.model.Transaction
import com.monistoWallet.ui.compose.components.CellLawrenceSection
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun GroupedFullList(items: List<com.monistoWallet.additional_wallet0x.account.transactions.data.model.Transaction>,
                    onTransactionClick: (com.monistoWallet.additional_wallet0x.account.transactions.data.model.Transaction) -> Unit
) {
    fun extractDate(created: String): String {
        // Определяем формат даты
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val date = inputFormat.parse(created)
            outputFormat.format(date)
        } catch (e: Exception) {
            // В случае ошибки возвращаем пустую строку или другую обработку
            ""
        }
    }
    // Группируем элементы по дате
    val groupedItems = items.groupBy { extractDate(it.created) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        groupedItems.forEach { (date, itemsForDate) ->
            item {
                // Заголовок для группы
                Text(
                    text = date,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.White
                )
            }

            item {
                // Отображаем элементы для этой даты
                CellLawrenceSection(
                    getListComposableFullTransactions(itemsForDate, {
                        onTransactionClick.invoke(it)
                    })
                )
            }
        }
    }
}
@Composable
fun GroupedList(items: List<Transaction>, onClick: (com.monistoWallet.additional_wallet0x.account.transactions.data.model.Transaction) -> Unit) {
    fun extractDate(created: String): String {
        // Определяем формат даты
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val date = inputFormat.parse(created)
            outputFormat.format(date)
        } catch (e: Exception) {
            // В случае ошибки возвращаем пустую строку или другую обработку
            ""
        }
    }
    // Группируем элементы по дате
    val groupedItems = items.groupBy { extractDate(it.created) }

    groupedItems.forEach { (date, itemsForDate) ->
        // Заголовок для группы
        Text(
            text = date,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color.White
        )

        // Отображаем элементы для этой даты
        CellLawrenceSection(
            getListComposableTransactions(itemsForDate) {
                onClick.invoke(it)
            }
        )
    }
}

fun getListComposableFullTransactions(list: List<com.monistoWallet.additional_wallet0x.account.transactions.data.model.Transaction>, onClick: (com.monistoWallet.additional_wallet0x.account.transactions.data.model.Transaction) -> Unit): List<@Composable () -> Unit> {
    val listItems = mutableListOf<@Composable () -> Unit>()
    list.forEach {
        listItems.add {
            ListFullTransactionItem(content = it) {
                onClick.invoke(it)
            }
        }
    }
    return listItems
}
fun getListComposableTransactions(list: List<Transaction>, onClick: (com.monistoWallet.additional_wallet0x.account.transactions.data.model.Transaction) -> Unit): List<@Composable () -> Unit> {
    val listItems = mutableListOf<@Composable () -> Unit>()
    list.forEach {
        listItems.add {
            ListTransactionItem(content = it, onClick)
        }
    }
    return listItems
}
@Composable
fun ListFullTransactionItem(content: com.monistoWallet.additional_wallet0x.account.transactions.data.model.Transaction, onClick: (com.monistoWallet.additional_wallet0x.account.transactions.data.model.Transaction) -> Unit) {
    Row(
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp).clickable {
            onClick.invoke(content)
        }
    ) {
        Image(
            painter = painterResource(if (content.change_amount < 0) R.drawable.ic_send else R.drawable.ic_recieve),
            contentDescription = null,
            Modifier
                .clip(RoundedCornerShape(300.dp))
                .background(Color(0xFF17181A))
                .size(30.dp)
                .align(Alignment.CenterVertically),
        )
        Text(
            text = content.title,
            modifier = Modifier
                .padding(start = 20.dp).align(Alignment.CenterVertically).weight(1f),
            color = Color.White,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = content.change_amount.toString(),
            modifier = Modifier
                .padding(12.dp).align(Alignment.CenterVertically),
            color = Color.White,
            fontSize = 15.sp
        )


    }
}
@Composable
fun ListTransactionItem(
    content: Transaction,
    onClick: (com.monistoWallet.additional_wallet0x.account.transactions.data.model.Transaction) -> Unit
) {
    Row(
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp).clickable {  }
    ) {
        Image(
            painter = painterResource(if (content.change_amount < 0) R.drawable.ic_send else R.drawable.ic_recieve),
            contentDescription = null,
            Modifier
                .clip(RoundedCornerShape(300.dp))
                .background(Color(0xFF17181A))
                .size(30.dp)
                .align(Alignment.CenterVertically),
        )
        Text(
            text = content.title,
            modifier = Modifier
                .padding(start = 20.dp).align(Alignment.CenterVertically).weight(1f),
            color = Color.White,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = content.change_amount.toString(),
            modifier = Modifier
                .padding(12.dp).align(Alignment.CenterVertically),
            color = Color.White,
            fontSize = 15.sp
        )


    }
}