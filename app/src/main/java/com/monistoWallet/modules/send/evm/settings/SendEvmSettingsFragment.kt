package com.monistoWallet.modules.send.evm.settings

import androidx.annotation.IdRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.modules.evmfee.Cautions
import com.monistoWallet.modules.evmfee.Eip1559FeeSettings
import com.monistoWallet.modules.evmfee.EvmFeeCellViewModel
import com.monistoWallet.modules.evmfee.EvmFeeModule
import com.monistoWallet.modules.evmfee.EvmSettingsInput
import com.monistoWallet.modules.evmfee.LegacyFeeSettings
import com.monistoWallet.modules.evmfee.eip1559.Eip1559FeeSettingsViewModel
import com.monistoWallet.modules.evmfee.legacy.LegacyFeeSettingsViewModel
import com.monistoWallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.HsIconButton
import com.monistoWallet.ui.compose.components.MenuItem
import java.math.BigDecimal

class SendEvmSettingsFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val feeViewModel by navGraphViewModels<EvmFeeCellViewModel>(requireArguments().getInt(NAV_GRAPH_ID))
        val nonceViewModel by navGraphViewModels<SendEvmNonceViewModel>(requireArguments().getInt(NAV_GRAPH_ID))
        val sendViewModel by navGraphViewModels<SendEvmTransactionViewModel>(requireArguments().getInt(NAV_GRAPH_ID))

        val feeSettingsViewModel = viewModel<ViewModel>(
            factory = EvmFeeModule.Factory(
                feeViewModel.feeService,
                feeViewModel.gasPriceService,
                feeViewModel.coinService
            )
        )
        val sendSettingsViewModel = viewModel<SendEvmSettingsViewModel>(
            factory = SendEvmSettingsModule.Factory(sendViewModel.service.settingsService, feeViewModel.coinService)
        )
        SendEvmFeeSettingsScreen(
            viewModel = sendSettingsViewModel,
            feeSettingsViewModel = feeSettingsViewModel,
            nonceViewModel = nonceViewModel,
            navController = navController
        )
    }

    companion object {
        private const val NAV_GRAPH_ID = "nav_graph_id"

        fun prepareParams(@IdRes navGraphId: Int) =
            bundleOf(NAV_GRAPH_ID to navGraphId)
    }
}


@Composable
fun SendEvmFeeSettingsScreen(
    viewModel: SendEvmSettingsViewModel,
    feeSettingsViewModel: ViewModel,
    nonceViewModel: SendEvmNonceViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(color = ComposeAppTheme.colors.tyler)
    ) {
        AppBar(
            title = stringResource(R.string.SendEvmSettings_Title),
            navigationIcon = {
                HsIconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "back button",
                        tint = ComposeAppTheme.colors.jacob
                    )
                }
            },
            menuItems = listOf(
                MenuItem(
                    title = TranslatableString.ResString(R.string.Button_Reset),
                    enabled = !viewModel.isRecommendedSettingsSelected,
                    onClick = { viewModel.onClickReset() }
                )
            )
        )

        when (feeSettingsViewModel) {
            is LegacyFeeSettingsViewModel -> {
                LegacyFeeSettings(feeSettingsViewModel, navController)
            }

            is Eip1559FeeSettingsViewModel -> {
                Eip1559FeeSettings(feeSettingsViewModel, navController)
            }
        }

        val nonceUiState = nonceViewModel.uiState
        if (nonceUiState.showInSettings) {
            Spacer(modifier = Modifier.height(24.dp))
            EvmSettingsInput(
                title = stringResource(id = R.string.SendEvmSettings_Nonce),
                info = stringResource(id = R.string.SendEvmSettings_Nonce_Info),
                value = nonceUiState.nonce?.toBigDecimal() ?: BigDecimal.ZERO,
                decimals = 0,
                navController = navController,
                warnings = nonceUiState.warnings,
                errors = nonceUiState.errors,
                onValueChange = {
                    nonceViewModel.onEnterNonce(it.toLong())
                },
                onClickIncrement = nonceViewModel::onIncrementNonce,
                onClickDecrement = nonceViewModel::onDecrementNonce
            )
        }

        Cautions(viewModel.cautions)

        Spacer(modifier = Modifier.height(32.dp))
    }
}
