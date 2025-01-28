package com.monistoWallet.modules.pin.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monistoWallet.R
import com.monistoWallet.modules.pin.unlock.PinConfirmViewModel
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.HsBackButton
import org.web3j.abi.datatypes.primitive.Float
import java.util.Timer
import kotlin.concurrent.schedule

@Composable
fun PinConfirm(
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
) {
    val viewModel = viewModel<PinConfirmViewModel>(factory = PinConfirmViewModel.Factory())
    var angle by remember {
        mutableStateOf(0f)
    }
    val rotatedAngle by animateFloatAsState(
        targetValue = angle,
        animationSpec = tween(durationMillis = 500)
    )

    if (viewModel.uiState.unlocked) {
        onSuccess.invoke()
        viewModel.unlocked()
    }

    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            AppBar(
                title = stringResource(R.string.Unlock_Title),
                navigationIcon = {
                    HsBackButton(onClick = onCancel)
                },
            )
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
                enteredCount = viewModel.uiState.enteredCount,
                showShakeAnimation = viewModel.uiState.showShakeAnimation,
                inputState = viewModel.uiState.inputState,
                onShakeAnimationFinish = { viewModel.onShakeAnimationFinish() }
            )

            PinNumpad(
                pinRandomized = viewModel.pinRandomized,
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
                inputState = viewModel.uiState.inputState,
                updatePinRandomized = { random -> viewModel.updatePinRandomized(random) }
            )
        }
    }
}