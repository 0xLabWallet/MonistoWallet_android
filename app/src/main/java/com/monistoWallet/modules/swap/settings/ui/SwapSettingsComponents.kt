package com.monistoWallet.modules.swap.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.entities.DataState
import com.monistoWallet.modules.address.HSAddressInput
import com.monistoWallet.modules.swap.settings.RecipientAddressViewModel
import com.monistoWallet.modules.swap.settings.SwapDeadlineViewModel
import com.monistoWallet.modules.swap.settings.SwapSlippageViewModel
import com.monistoWallet.ui.compose.ColoredTextStyle
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.*
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.TokenQuery
import com.wallet0x.marketkit.models.TokenType

@Composable
fun SlippageAmount(
    slippageViewModel: SwapSlippageViewModel
) {
    HeaderText(
        text = stringResource(R.string.SwapSettings_SlippageTitle)
    )
    InputWithButtons(
        modifier = Modifier.padding(horizontal = 16.dp),
        hint = slippageViewModel.inputFieldPlaceholder,
        initial = slippageViewModel.initialValue,
        buttons = slippageViewModel.inputButtons,
        state = slippageViewModel.errorState,
        onValueChange = {
            slippageViewModel.onChangeText(it)
        }
    )
    InfoText(
        text = stringResource(R.string.SwapSettings_SlippageDescription),
    )
}

@Composable
fun TransactionDeadlineInput(deadlineViewModel: SwapDeadlineViewModel) {
    HeaderText(
        text = stringResource(R.string.SwapSettings_DeadlineTitle)
    )
    InputWithButtons(
        modifier = Modifier.padding(horizontal = 16.dp),
        hint = deadlineViewModel.inputFieldPlaceholder,
        initial = deadlineViewModel.initialValue,
        buttons = deadlineViewModel.inputButtons,
        state = deadlineViewModel.errorState,
        onValueChange = {
            deadlineViewModel.onChangeText(it)
        }
    )
    InfoText(
        text = stringResource(R.string.SwapSettings_DeadlineDescription),
    )
}

@Composable
fun RecipientAddress(
    blockchainType: BlockchainType,
    recipientAddressViewModel: RecipientAddressViewModel,
    navController: NavController,
) {
    val tokenQuery = TokenQuery(blockchainType, TokenType.Native)
    com.monistoWallet.core.App.marketKit.token(tokenQuery)?.let { token ->
        HeaderText(
            text = stringResource(R.string.SwapSettings_RecipientAddressTitle)
        )
        HSAddressInput(
            modifier = Modifier.padding(horizontal = 16.dp),
            initial = recipientAddressViewModel.initialAddress,
            tokenQuery = token.tokenQuery,
            coinCode = token.coin.code,
            navController = navController,
            onError = {
                recipientAddressViewModel.updateError(it)
            },
            onValueChange = {
                recipientAddressViewModel.onChangeAddress(it)
            },
        )
        InfoText(
            text = stringResource(R.string.SwapSettings_RecipientAddressDescription),
        )
    }
}

@Composable
fun InputWithButtons(
    modifier: Modifier = Modifier,
    hint: String? = null,
    initial: String? = null,
    buttons: List<InputButton>,
    state: DataState<Any>? = null,
    onValueChange: (String) -> Unit,
) {
    val borderColor = when (state) {
        is DataState.Error -> {
            if (state.error is FormsInputStateWarning) {
                ComposeAppTheme.colors.yellow50
            } else {
                ComposeAppTheme.colors.red50
            }
        }
        else -> ComposeAppTheme.colors.steel20
    }

    val cautionColor = if (state?.errorOrNull is FormsInputStateWarning) {
        ComposeAppTheme.colors.jacob
    } else {
        ComposeAppTheme.colors.lucian
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .background(ComposeAppTheme.colors.lawrence)
                .height(44.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                mutableStateOf(TextFieldValue(initial ?: ""))
            }

            LaunchedEffect(initial) {
                if (textState.text != initial) {
                    textState = textState.copy(text = initial ?: "")
                }
            }

            BasicTextField(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .weight(1f),
                value = textState,
                onValueChange = { textValue ->
                    textState = textValue
                    onValueChange.invoke(textValue.text)
                },
                textStyle = ColoredTextStyle(
                    color = ComposeAppTheme.colors.leah,
                    textStyle = ComposeAppTheme.typography.body
                ),
                maxLines = 1,
                decorationBox = { innerTextField ->
                    if (textState.text.isEmpty()) {
                        body_grey50(
                            hint ?: "",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    }
                    innerTextField()
                },
            )

            if (textState.text.isNotEmpty()) {
                ButtonSecondaryCircle(
                    icon = R.drawable.ic_delete_20,
                    onClick = {
                        val text = ""
                        textState = textState.copy(text = text, selection = TextRange(0))
                        onValueChange.invoke(text)
                    }
                )
            } else {
                buttons.forEachIndexed { index, button ->
                    ButtonSecondaryDefault(
                        modifier = Modifier.padding(end = if (index == buttons.size - 1) 0.dp else 8.dp),
                        title = button.title,
                        onClick = {
                            textState = textState.copy(
                                text = button.rawValue,
                                selection = TextRange(button.rawValue.length)
                            )
                            onValueChange.invoke(button.rawValue)
                        },
                    )
                }
            }

        }

        state?.errorOrNull?.localizedMessage?.let {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
                text = it,
                color = cautionColor,
                style = ComposeAppTheme.typography.caption
            )
        }
    }
}

class InputButton(val title: String, val rawValue: String)
