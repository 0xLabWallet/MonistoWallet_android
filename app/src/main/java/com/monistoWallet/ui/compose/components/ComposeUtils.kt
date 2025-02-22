package com.monistoWallet.ui.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.monistoWallet.R
import com.monistoWallet.ui.compose.ComposeAppTheme
import java.math.BigDecimal

@Composable
fun RateColor(diff: BigDecimal?) =
    if ((diff ?: BigDecimal.ZERO) >= BigDecimal.ZERO) ComposeAppTheme.colors.remus else ComposeAppTheme.colors.lucian

@Composable
fun diffColor(value: BigDecimal) =
    if (value.signum() >= 0) {
        ComposeAppTheme.colors.remus
    } else {
        ComposeAppTheme.colors.lucian
    }

@Composable
fun formatValueAsDiff(value: com.monistoWallet.modules.market.Value): String =
    com.monistoWallet.core.App.numberFormatter.formatValueAsDiff(value)

@Composable
fun RateText(diff: BigDecimal?): String {
    if (diff == null) return ""
    val sign = if (diff >= BigDecimal.ZERO) "+" else "-"
    return com.monistoWallet.core.App.numberFormatter.format(diff.abs(), 0, 2, sign, "%")
}

@Composable
fun CoinImage(
    iconUrl: String?,
    placeholder: Int? = null,
    modifier: Modifier,
    colorFilter: ColorFilter? = null
) {
    val fallback = placeholder ?: R.drawable.coin_placeholder
    when {
        iconUrl != null -> Image(
            painter = rememberAsyncImagePainter(
                model = iconUrl,
                error = painterResource(fallback)
            ),
            contentDescription = null,
            modifier = modifier,
            colorFilter = colorFilter,
            contentScale = ContentScale.FillBounds
        )
        else -> Image(
            painter = painterResource(fallback),
            contentDescription = null,
            modifier = modifier,
            colorFilter = colorFilter
        )
    }
}

@Composable
fun NftIcon(
    modifier: Modifier = Modifier,
    iconUrl: String?,
    placeholder: Int? = null,
    colorFilter: ColorFilter? = null
) {
    val fallback = placeholder ?: R.drawable.ic_platform_placeholder_24
    when {
        iconUrl != null -> Image(
            painter = rememberAsyncImagePainter(
                model = iconUrl,
                error = painterResource(fallback)
            ),
            contentDescription = null,
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .size(32.dp),
            colorFilter = colorFilter,
            contentScale = ContentScale.Crop
        )
        else -> Image(
            painter = painterResource(fallback),
            contentDescription = null,
            modifier = modifier.size(32.dp),
            colorFilter = colorFilter
        )
    }
}