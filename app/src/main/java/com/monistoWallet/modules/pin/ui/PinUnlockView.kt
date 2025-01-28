package com.monistoWallet.modules.pin.ui

import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monistoWallet.R
import com.monistoWallet.modules.pin.unlock.PinUnlockModule
import com.monistoWallet.modules.pin.unlock.PinUnlockViewModel
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.title3_leah
import java.util.Timer
import kotlin.concurrent.schedule

@Composable
fun PinUnlock(
    onSuccess: () -> Unit,
) {
    val viewModel = viewModel<PinUnlockViewModel>(factory = PinUnlockModule.Factory())
    val uiState = viewModel.uiState
    var showBiometricPrompt by remember {
        mutableStateOf(
            uiState.fingerScannerEnabled && uiState.inputState is PinUnlockModule.InputState.Enabled
        )
    }
    var showBiometricDisabledAlert by remember { mutableStateOf(false) }

    var angle by remember {
        mutableStateOf(0f)
    }
    val rotatedAngle by animateFloatAsState(
        targetValue = angle,
        animationSpec = tween(durationMillis = 500)
    )

    if (uiState.unlocked) {
        onSuccess.invoke()
        viewModel.unlocked()
    }

    if (showBiometricPrompt) {
        BiometricPromptDialog(
            onSuccess = {
                viewModel.onBiometricsUnlock()
                showBiometricPrompt = false
            },
            onError = { errorCode ->
                if (errorCode == BiometricPrompt.ERROR_LOCKOUT) {
                    showBiometricDisabledAlert = true
                }
                showBiometricPrompt = false
            }
        )
    }

    if (showBiometricDisabledAlert) {
        BiometricDisabledDialog {
            showBiometricDisabledAlert = false
        }
    }

    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                title3_leah(
                    text = stringResource(R.string.Unlock_Title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(134.dp),
                contentAlignment = Alignment.Center
            ){

            }
            PinTopBlock(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.Unlock_EnterPasscode),
                enteredCount = uiState.enteredCount,
                showShakeAnimation = uiState.showShakeAnimation,
                inputState = uiState.inputState,
                onShakeAnimationFinish = { viewModel.onShakeAnimationFinish() },
            )

            PinNumpad(
                onNumberClick = {
                    number -> viewModel.onKeyClick(number)
                    angle = -(number * 36).toFloat()
                    Timer().schedule(1000) {
                        angle = 0f
                    }
                                },
                onDeleteClick = {
                    viewModel.onDelete()
                    angle = 0f
                                },
                showFingerScanner = uiState.fingerScannerEnabled,
                pinRandomized = viewModel.pinRandomized,
                showBiometricPrompt = {
                    showBiometricPrompt = true
                },
                inputState = uiState.inputState,
                updatePinRandomized = { randomized ->
                    viewModel.updatePinRandomized(randomized)
                }
            )
        }
    }
}
