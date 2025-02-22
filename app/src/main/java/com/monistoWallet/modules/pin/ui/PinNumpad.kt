package com.monistoWallet.modules.pin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.monistoWallet.R
import com.monistoWallet.modules.pin.unlock.PinUnlockModule.InputState
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.ButtonSecondaryDefault
import com.monistoWallet.ui.compose.components.ButtonSecondaryYellow
import com.monistoWallet.ui.compose.components.VSpacer
import com.monistoWallet.core.helpers.HudHelper.vibrator


@Composable
fun PinNumpad(
    showFingerScanner: Boolean = false,
    pinRandomized: Boolean? = null,
    onNumberClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    showBiometricPrompt: (() -> Unit)? = null,
    inputState: InputState = InputState.Enabled(),
    updatePinRandomized: ((Boolean) -> Unit)? = null,
) {
    val numpadNumbers by remember(pinRandomized) { mutableStateOf(getNumpadNumbers(pinRandomized)) }
    val enabled = inputState is InputState.Enabled
    val vibrationWavePatterns = listOf(
        longArrayOf(50, 50),
        longArrayOf(50, 50),
        longArrayOf(50, 50, 50, 50),
        longArrayOf(50, 50, 50, 50, 50, 50),
        longArrayOf(50, 50, 50, 50, 50, 50, 50, 50),
        longArrayOf(50, 50, 50, 50, 50, 50, 50, 50, 50, 50),
        longArrayOf(50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50),
        longArrayOf(50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50),
        longArrayOf(50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50),
        longArrayOf(50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val windowed = numpadNumbers.windowed(size = 3, step = 3)
        windowed.forEach {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                it.forEach {
                    NumberKey(it,vibrationWavePatterns[it], enabled) { onNumberClick(it) }
                }
            }
            VSpacer(16.dp)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ImageKey(
                image = R.drawable.icon_touch_id_24,
                vibrationWavePatterns[0],
                contentDescription = stringResource(R.string.Unlock_BiometricScanner),
                visible = showFingerScanner,
                enabled = enabled
            ) {
                showBiometricPrompt?.invoke()
            }
            NumberKey(numpadNumbers.last(), vibrationWavePatterns[0], enabled) { onNumberClick(it) }
            ImageKey(
                image = R.drawable.ic_backspace,
                vibrationWavePatterns[0],
                contentDescription = stringResource(R.string.Button_Delete),
                visible = true,
                enabled = enabled
            ) {
                onDeleteClick.invoke()
            }
        }
        Column(
            modifier = Modifier.height(100.dp)
        ) {
            pinRandomized?.let { isRandomized ->
                VSpacer(24.dp)
                if (isRandomized) {
                    ButtonSecondaryYellow(
                        title = stringResource(R.string.Unlock_Random),
                        onClick = {
                            updatePinRandomized?.invoke(false)
                        },
                        enabled = enabled,
                    )
                } else {
                    ButtonSecondaryDefault(
                        title = stringResource(R.string.Unlock_Random),
                        onClick = {
                            updatePinRandomized?.invoke(true)
                        },
                        enabled = enabled,
                    )
                }
            }
        }
    }

}

private fun getNumpadNumbers(pinRandomized: Boolean?): List<Int> = when(pinRandomized) {
    true -> generateRandomNumpadNumbers()
    false, null -> generateOriginalNumpadNumbers()
}

private fun generateOriginalNumpadNumbers(): List<Int> {
    return listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)
}

private fun generateRandomNumpadNumbers(): List<Int> {
    return listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0).shuffled()
}

@Composable
private fun NumberKey(
    number: Int,
    vibrationWavePattern: LongArray,
    enabled: Boolean,
    onClick: (Int) -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .border(1.dp, ComposeAppTheme.colors.steel20, CircleShape)
            .clickable(
                enabled = enabled,
                onClick = {
//                    HudHelper.vibrate(context)
                    onClick.invoke(number)
                    context.vibrator(vibrationWavePattern)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            style = ComposeAppTheme.typography.title2R,
            color = if (enabled) ComposeAppTheme.colors.leah else ComposeAppTheme.colors.steel20,
        )
    }
}

@Composable
private fun ImageKey(
    image: Int,
    vibrationWavePattern: LongArray,
    contentDescription: String? = null,
    visible: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable(
                enabled = visible && enabled,
                onClick = {
//                    HudHelper.vibrate(context)
                    onClick.invoke()
                    context.vibrator(vibrationWavePattern)
                }
            )
    ) {
        if (visible) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                painter = painterResource(image),
                tint = if (enabled) ComposeAppTheme.colors.grey else ComposeAppTheme.colors.steel20,
                contentDescription = contentDescription,
            )
        }
    }
}

@Preview
@Composable
fun Preview_Pin() {
    ComposeAppTheme {
        Column(
            modifier = Modifier
                .background(color = ComposeAppTheme.colors.tyler)
        ) {
            PinNumpad(
                onNumberClick = { },
                onDeleteClick = { },
                showFingerScanner = true,
                pinRandomized = true,
                showBiometricPrompt = {

                }
            )
        }
    }
}

@Preview
@Composable
fun Preview_PinLocked() {
    ComposeAppTheme {
        Column(
            modifier = Modifier
                .background(color = ComposeAppTheme.colors.tyler)
        ) {
            PinNumpad(
                onNumberClick = { },
                onDeleteClick = { },
                showFingerScanner = true,
                pinRandomized = true,
                showBiometricPrompt = {},
                inputState = InputState.Locked("12:33")
            )
        }
    }
}
