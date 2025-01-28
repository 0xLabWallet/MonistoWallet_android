package com.monistoWallet.additional_wallet0x.root.ui

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monistoWallet.R
import com.monistoWallet.ui.compose.components.VSpacer
import org.web3j.abi.datatypes.Bool

@Composable
fun InputText(
    header: String,
    defaultText: String = "",
    isEnabled: Boolean = true,
    onValueChanged: (String) -> Unit,
) {
    var text by remember { mutableStateOf(defaultText) }
    Column {
        Text(
            text = header,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            TextField(
                enabled = isEnabled,
                value = text,
                onValueChange = { value ->
                    text = value
                    onValueChanged.invoke(value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(0.5.dp, Color(0xFFCBCBCB), RoundedCornerShape(8.dp))
                    .background(Color.Transparent, RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    placeholderColor = Color.Gray,
                    textColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

    }
}

@Composable
fun InputTextClear(
    header: String,
    baseText: String = "",
    onValueChanged: (String) -> Unit,
) {
    var text by remember { mutableStateOf(baseText) }
    Column {
        Text(
            text = header,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            TextField(
                value = text,
                onValueChange = { value ->
                    text = value
                    onValueChanged.invoke(value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(0.5.dp, Color(0xFFCBCBCB), RoundedCornerShape(8.dp))
                    .background(Color.Transparent, RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    placeholderColor = Color.Gray,
                    textColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(end = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))

                if (text != "") {
                    Image(
                        painter = painterResource(id = R.drawable.ic_clear_text),
                        contentDescription = "",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                onValueChanged.invoke("")
                                text = ""
                            }
                    )
                }
            }
        }

    }
}
@Composable
fun InputTextPassword(
    header: String,
    onValueChanged: (String) -> Unit,
) {
    var isHidden by remember { mutableStateOf(true) }
    var displayedText by remember { mutableStateOf("") }

    Column {
        Text(
            text = header,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            TextField(
                value = displayedText,
                onValueChange = { value ->
                    displayedText = value
                    onValueChanged(value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(0.5.dp, Color(0xFFCBCBCB), RoundedCornerShape(8.dp))
                    .background(Color.Transparent, RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    placeholderColor = Color.Gray,
                    textColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                visualTransformation = if (isHidden) PasswordVisualTransformation() else VisualTransformation.None
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(end = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = if (isHidden) painterResource(id = R.drawable.ic_password_hidden) else painterResource(id = R.drawable.ic_password_showed),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            isHidden = !isHidden
                        }
                )
            }
        }
    }
}
@Composable
fun InputTextGetCode(
    header: String,
    onValueChanged: (String) -> Unit,
    onGetCode: () -> Unit,
    canGetCodeFirst: Boolean = true
) {
    val handler = Handler(Looper.getMainLooper())
    var displayedText by remember { mutableStateOf("") }
    var time by remember { mutableIntStateOf(if (canGetCodeFirst) 60 else 0) }
    var timerRunning by remember { mutableStateOf(true) }

    fun startTimer() {
        timerRunning = true
        handler.postDelayed({
            if (time > 0) {
                time -= 1
                startTimer() // Рекурсивный вызов для обновления таймера
            } else {
                timerRunning = false
            }
        }, 1000L)
    }
    Column {
        Text(
            text = header,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            TextField(
                value = displayedText,
                onValueChange = { value ->
                    displayedText = value
                    onValueChanged(value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(0.5.dp, Color(0xFFCBCBCB), RoundedCornerShape(8.dp))
                    .background(Color.Transparent, RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    placeholderColor = Color.Gray,
                    textColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(end = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (time <= 0) stringResource(R.string.get_code) else time.toString(),
                    fontSize = 14.sp,
                    color = Color(0xFF9A9A9A),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        if (time <= 0) {
                            displayedText = ""
                            onValueChanged("")
                            time = 60
                            onGetCode.invoke()
                            startTimer()
                        }
                    }
                )
            }
        }
    }

    // Запуск таймера при инициализации
    LaunchedEffect(Unit) {
        startTimer()
    }

}