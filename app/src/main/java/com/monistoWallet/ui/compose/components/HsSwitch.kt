package com.monistoWallet.ui.compose.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.monistoWallet.ui.compose.ComposeAppTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HsSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
){
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        Switch(
            modifier = modifier,
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ComposeAppTheme.colors.white,
                uncheckedThumbColor = ComposeAppTheme.colors.lightGrey,
                checkedTrackColor = Color(0xFF0257FF),
                uncheckedTrackColor = ComposeAppTheme.colors.elenaD,
                checkedTrackAlpha = 1f,
                uncheckedTrackAlpha = 0.2f,
            ),
            enabled = enabled
        )
    }
}
