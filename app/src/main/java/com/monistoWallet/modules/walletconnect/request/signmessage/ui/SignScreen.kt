package com.monistoWallet.modules.walletconnect.request.signmessage.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.modules.evmfee.ButtonsGroupWithShade
import com.monistoWallet.modules.walletconnect.request.WCRequestChain
import com.monistoWallet.modules.walletconnect.request.signmessage.SignMessage
import com.monistoWallet.modules.walletconnect.request.signmessage.WCSignMessageRequestViewModel
import com.monistoWallet.modules.walletconnect.request.ui.TitleTypedValueCell
import com.monistoWallet.modules.walletconnect.session.ui.BlockchainCell
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.ButtonPrimaryDefault
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.CellUniversalLawrenceSection
import com.monistoWallet.ui.compose.components.HsCheckbox
import com.monistoWallet.ui.compose.components.MenuItem
import com.monistoWallet.ui.compose.components.MessageToSign
import com.monistoWallet.ui.compose.components.RowUniversal
import com.monistoWallet.ui.compose.components.TextImportantWarning
import com.monistoWallet.ui.compose.components.subhead2_leah
import com.monistoWallet.core.helpers.HudHelper

@Composable
fun SignMessageRequestScreen(
    navController: NavController,
    viewModel: WCSignMessageRequestViewModel,
) {

    if (viewModel.showSignError) {
        HudHelper.showErrorMessage(LocalView.current, R.string.Error)
        viewModel.signErrorShown()
    }

    Column(
        modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)
    ) {
        AppBar(
            stringResource(R.string.WalletConnect_SignMessageRequest_Title),
            menuItems = listOf(
                MenuItem(
                    title = TranslatableString.ResString(R.string.Button_Close),
                    icon = R.drawable.ic_close,
                    onClick = { navController.popBackStack() }
                )
            )
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .fillMaxWidth()
        ) {
            Spacer(Modifier.height(12.dp))

            when (val message = viewModel.message) {
                is SignMessage.PersonalMessage -> {
                    MessageContent(message.data, viewModel.dAppName, viewModel.chain, viewModel)
                }

                is SignMessage.Message -> {
                    MessageContent(message.data, viewModel.dAppName, viewModel.chain, viewModel, message.showLegacySignWarning)
                }

                is SignMessage.TypedMessage -> {
                    TypedMessageContent(message, viewModel.dAppName, viewModel.chain)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
        ButtonsGroupWithShade {
            Column(Modifier.padding(horizontal = 24.dp)) {
                ButtonPrimaryYellow(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.WalletConnect_SignMessageRequest_ButtonSign),
                    enabled = viewModel.signEnabled,
                    onClick = { viewModel.sign() },
                )
                Spacer(Modifier.height(16.dp))
                ButtonPrimaryDefault(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.Button_Reject),
                    onClick = { viewModel.reject() }
                )
            }
        }
    }
}

@Composable
private fun TypedMessageContent(
    message: SignMessage.TypedMessage,
    dAppName: String?,
    chain: WCRequestChain
) {
    message.domain?.let { domain ->
        CellUniversalLawrenceSection(
            listOf {
                TitleTypedValueCell(
                    stringResource(R.string.WalletConnect_SignMessageRequest_Domain),
                    domain
                )
            }
        )
    }

    val composableItems: MutableList<@Composable () -> Unit> = mutableListOf()
    dAppName?.let { name ->
        composableItems.add {
            TitleTypedValueCell(
                stringResource(R.string.WalletConnect_SignMessageRequest_dApp),
                name
            )
        }
    }
    composableItems.add {
        BlockchainCell(chain.name, chain.address)
    }

    CellUniversalLawrenceSection(
        composableItems
    )

    MessageToSign(message.data)
}

@Composable
private fun MessageContent(
    message: String,
    dAppName: String?,
    chain: WCRequestChain,
    viewModel: WCSignMessageRequestViewModel,
    showLegacySignWarning: Boolean = false
) {
    CellUniversalLawrenceSection(buildList {
        dAppName?.let { dApp ->
            add { TitleTypedValueCell(stringResource(R.string.WalletConnect_SignMessageRequest_dApp), dApp) }
        }
        add {
            BlockchainCell(chain.name, chain.address)
        }
    })

    MessageToSign(message)

    if (showLegacySignWarning) {
        Spacer(Modifier.height(32.dp))
        TextImportantWarning(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.WalletConnect_LegacySignWarning),
            icon = R.drawable.ic_attention_20,
            title = stringResource(R.string.WalletConnect_Note),
        )
        Spacer(Modifier.height(12.dp))
        CellUniversalLawrenceSection(
            listOf {
                RowUniversal(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = {
                        viewModel.onTrustChecked(!viewModel.trustCheckmarkChecked)
                    }
                ) {
                    HsCheckbox(
                        checked = viewModel.trustCheckmarkChecked,
                        onCheckedChange = { checked ->
                            viewModel.onTrustChecked(checked)
                        }
                    )
                    Spacer(Modifier.width(16.dp))
                    subhead2_leah(text = stringResource(R.string.WalletConnect_I_Trust))
                }
            }
        )
    }
}
