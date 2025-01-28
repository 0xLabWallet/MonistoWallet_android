package com.monistoWallet.additional_wallet0x.root.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.monistoWallet.R


@Composable
fun ButtonBack(
    onBack: () -> Unit
) {
    Image(
        painter = painterResource(R.drawable.ic_back),
        contentDescription = null,
        modifier = Modifier.size(24.dp).clickable {
            onBack.invoke()
        },
        colorFilter = ColorFilter.tint(Color.White)
    )
}
@Composable
fun ButtonClose(
    onClose: () -> Unit
) {
    Image(
        painter = painterResource(R.drawable.ic_close),
        contentDescription = null,
        modifier = Modifier.size(24.dp).clickable {
            onClose.invoke()
        },
        colorFilter = ColorFilter.tint(Color.White)
    )
}