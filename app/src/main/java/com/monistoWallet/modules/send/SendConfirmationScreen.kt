package com.monistoWallet.modules.send

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.imageUrl
import com.monistoWallet.entities.Address
import com.monistoWallet.entities.CurrencyValue
import com.monistoWallet.modules.amount.AmountInputType
import com.monistoWallet.modules.contacts.model.Contact
import com.monistoWallet.modules.fee.HSFeeInputRaw
import com.monistoWallet.modules.hodler.HSHodler
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.DisposableLifecycleCallbacks
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.CellUniversalLawrenceSection
import com.monistoWallet.ui.compose.components.CoinImage
import com.monistoWallet.ui.compose.components.HsBackButton
import com.monistoWallet.ui.compose.components.RowUniversal
import com.monistoWallet.ui.compose.components.SectionTitleCell
import com.monistoWallet.ui.compose.components.TransactionInfoAddressCell
import com.monistoWallet.ui.compose.components.TransactionInfoContactCell
import com.monistoWallet.ui.compose.components.subhead1Italic_leah
import com.monistoWallet.ui.compose.components.subhead1_grey
import com.monistoWallet.ui.compose.components.subhead2_grey
import com.monistoWallet.ui.compose.components.subhead2_leah
import com.monistoWallet.core.SnackbarDuration
import com.monistoWallet.core.helpers.HudHelper
import com.wallet0x.hodler.LockTimeInterval
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.Coin
import kotlinx.coroutines.delay
import java.math.BigDecimal

@Composable
fun SendConfirmationScreen(
    navController: NavController,
    coinMaxAllowedDecimals: Int,
    feeCoinMaxAllowedDecimals: Int,
    fiatMaxAllowedDecimals: Int,
    amountInputType: AmountInputType,
    rate: CurrencyValue?,
    feeCoinRate: CurrencyValue?,
    sendResult: SendResult?,
    blockchainType: BlockchainType,
    coin: Coin,
    feeCoin: Coin,
    amount: BigDecimal,
    address: Address,
    contact: Contact?,
    fee: BigDecimal,
    lockTimeInterval: LockTimeInterval?,
    memo: String?,
    onClickSend: () -> Unit,
    sendEntryPointDestId: Int
) {
    val closeUntilDestId = if (sendEntryPointDestId == 0) {
        R.id.sendXFragment
    } else {
        sendEntryPointDestId
    }
    val view = LocalView.current
    when (sendResult) {
        SendResult.Sending -> {
            HudHelper.showInProcessMessage(
                view,
                R.string.Send_Sending,
                SnackbarDuration.INDEFINITE
            )
        }

        SendResult.Sent -> {
            HudHelper.showSuccessMessage(
                view,
                R.string.Send_Success,
                SnackbarDuration.LONG
            )
        }

        is SendResult.Failed -> {
            HudHelper.showErrorMessage(view, sendResult.caution.getString())
        }

        null -> Unit
    }

    LaunchedEffect(sendResult) {
        if (sendResult == SendResult.Sent) {
            delay(1200)
            navController.popBackStack(closeUntilDestId, true)
        }
    }

    DisposableLifecycleCallbacks(
        //additional close for cases when user closes app immediately after sending
        onResume = {
            if (sendResult == SendResult.Sent) {
                navController.popBackStack(closeUntilDestId, true)
            }
        }
    )

    Column(Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = stringResource(R.string.Send_Confirmation_Title),
            navigationIcon = {
                HsBackButton(onClick = { navController.popBackStack() })
            },
            menuItems = listOf()
        )
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 106.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                val topSectionItems = buildList<@Composable () -> Unit> {
                    add {
                        SectionTitleCell(
                            stringResource(R.string.Send_Confirmation_YouSend),
                            coin.name,
                            R.drawable.ic_arrow_up_right_12
                        )
                    }
                    add {
                        val coinAmount = com.monistoWallet.core.App.numberFormatter.formatCoinFull(
                            amount,
                            coin.code,
                            coinMaxAllowedDecimals
                        )

                        val currencyAmount = rate?.let { rate ->
                            rate.copy(value = amount.times(rate.value))
                                .getFormattedFull()
                        }

                        ConfirmAmountCell(currencyAmount, coinAmount, coin.imageUrl)
                    }
                    add {
                        TransactionInfoAddressCell(
                            title = stringResource(R.string.Send_Confirmation_To),
                            value = address.hex,
                            showAdd = contact == null,
                            blockchainType = blockchainType,
                            navController = navController
                        )
                    }
                    contact?.let {
                        add {
                            TransactionInfoContactCell(name = contact.name)
                        }
                    }
                    if (lockTimeInterval != null) {
                        add {
                            HSHodler(lockTimeInterval = lockTimeInterval)
                        }
                    }
                }

                CellUniversalLawrenceSection(topSectionItems)

                Spacer(modifier = Modifier.height(28.dp))

                val bottomSectionItems = buildList<@Composable () -> Unit> {
                    add {
                        HSFeeInputRaw(
                            coinCode = feeCoin.code,
                            coinDecimal = feeCoinMaxAllowedDecimals,
                            fee = fee,
                            amountInputType = amountInputType,
                            rate = feeCoinRate,
                            navController = navController
                        )
                    }
                    if (!memo.isNullOrBlank()) {
                        add {
                            MemoCell(memo)
                        }
                    }
                }

                CellUniversalLawrenceSection(bottomSectionItems)
            }

            SendButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                sendResult = sendResult,
                onClickSend = onClickSend
            )
        }
    }
}

@Composable
fun SendButton(modifier: Modifier, sendResult: SendResult?, onClickSend: () -> Unit) {
    when (sendResult) {
        SendResult.Sending -> {
            ButtonPrimaryYellow(
                modifier = modifier,
                title = stringResource(R.string.Send_Sending),
                onClick = { },
                enabled = false
            )
        }

        SendResult.Sent -> {
            ButtonPrimaryYellow(
                modifier = modifier,
                title = stringResource(R.string.Send_Success),
                onClick = { },
                enabled = false
            )
        }

        else -> {
            ButtonPrimaryYellow(
                modifier = modifier,
                title = stringResource(R.string.Send_Confirmation_Send_Button),
                onClick = onClickSend,
                enabled = true
            )
        }
    }
}

@Composable
fun ConfirmAmountCell(fiatAmount: String?, coinAmount: String, iconUrl: String?) {
    RowUniversal(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        CoinImage(
            iconUrl = iconUrl,
            placeholder = R.drawable.coin_placeholder,
            modifier = Modifier.size(32.dp)
        )
        subhead2_leah(
            modifier = Modifier.padding(start = 16.dp),
            text = coinAmount,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.weight(1f))
        subhead1_grey(text = fiatAmount ?: "")
    }
}

@Composable
fun MemoCell(value: String) {
    RowUniversal(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        subhead2_grey(
            modifier = Modifier.padding(end = 16.dp),
            text = stringResource(R.string.Send_Confirmation_HintMemo),
        )
        Spacer(Modifier.weight(1f))
        subhead1Italic_leah(
            text = value,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
