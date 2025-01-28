package com.monistoWallet.modules.pin.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monistoWallet.R
import com.monistoWallet.modules.pin.set.PinSetModule
import com.monistoWallet.modules.pin.set.PinSetViewModel
import com.monistoWallet.ui.animations.CrossSlide
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.HsBackButton
import java.util.Timer
import kotlin.concurrent.schedule

@Composable
fun PinSet(
    title: String,
    description: String,
    dismissWithSuccess: () -> Unit,
    onBackPress: () -> Unit,
    forDuress: Boolean = false,
    viewModel: PinSetViewModel = viewModel(factory = PinSetModule.Factory(forDuress))
) {
    if (viewModel.uiState.finished) {
        dismissWithSuccess.invoke()
        viewModel.finished()
    }

    var angle by remember {
        mutableStateOf(0f)
    }
    val rotatedAngle by animateFloatAsState(
        targetValue = angle,
        animationSpec = tween(durationMillis = 500)
    )

    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            AppBar(
                title = title,
                navigationIcon = {
                    HsBackButton(onClick = onBackPress)
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = ComposeAppTheme.colors.tyler),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(134.dp),
                    contentAlignment = Alignment.Center
                ){
                }
                CrossSlide(
                    targetState = viewModel.uiState.stage,
                    modifier = Modifier.weight(1f),
                    reverseAnimation = viewModel.uiState.reverseSlideAnimation
                ) { stage ->
                    when (stage) {
                        PinSetModule.SetStage.Enter -> {
                            PinTopBlock(
                                title = description,
                                error = viewModel.uiState.error,
                                enteredCount = viewModel.uiState.enteredCount,
                            )
                        }
                        PinSetModule.SetStage.Confirm -> {
                            PinTopBlock(
                                title = stringResource(R.string.PinSet_ConfirmInfo),
                                enteredCount = viewModel.uiState.enteredCount,
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(2f)
                ) {
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
                    )
                }

            }
        }
    }
}
