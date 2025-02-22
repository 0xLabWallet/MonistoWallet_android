package com.monistoWallet.ui.compose.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.TextButton
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.monistoWallet.ui.compose.AppRippleTheme

@Composable
fun HsTextButton(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    CompositionLocalProvider(LocalRippleTheme provides AppRippleTheme) {
        TextButton(
            onClick = onClick
        ) {
            content()
        }
    }
}