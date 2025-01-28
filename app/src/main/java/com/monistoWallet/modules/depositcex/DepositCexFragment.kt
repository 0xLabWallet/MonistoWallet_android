package com.monistoWallet.modules.depositcex

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.core.providers.CexAsset
import com.monistoWallet.core.providers.CexDepositNetwork
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.modules.receive.address.ReceiveAddressScreen
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.core.parcelable

class DepositCexFragment : BaseComposeFragment() {

    companion object {
        fun args(cexAsset: CexAsset, network: CexDepositNetwork? = null): Bundle {
            return bundleOf(
                "cexAsset" to cexAsset,
                "cexDepositNetwork" to network,
            )
        }
    }

    @Composable
    override fun GetContent(navController: NavController) {
        val cexAsset = arguments?.parcelable<CexAsset>("cexAsset")
        val network = arguments?.parcelable<CexDepositNetwork>("cexDepositNetwork")

        if (cexAsset != null) {
            val networks = cexAsset.depositNetworks
            if (networks.isEmpty() || network != null || networks.size == 1) {
                val viewContent = LocalContext.current

                val viewModel =
                    viewModel<com.monistoWallet.modules.depositcex.DepositAddressViewModel>(factory = com.monistoWallet.modules.depositcex.DepositAddressViewModel.Factory(cexAsset, network))

                ReceiveAddressScreen(
                    title = stringResource(R.string.CexDeposit_Title, cexAsset.id),
                    uiState = viewModel.uiState,
                    onErrorClick = { viewModel.onErrorClick() },
                    setAmount = { amount -> viewModel.setAmount(amount) },
                    navController = navController,
                    onShareClick = { address ->
                        viewContent.startActivity(Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, address)
                            type = "text/plain"
                        })
                    },
                )
            } else {
                val navigatedFromMain = navController.previousBackStackEntry?.destination?.id == R.id.mainFragment
                val navigateBack: () -> Unit = { navController.popBackStack() }
                SelectNetworkScreen(
                    networks = networks,
                    onNavigateBack = if (navigatedFromMain) null else navigateBack,
                    onClose = { navController.popBackStack(R.id.mainFragment, false) },
                    onSelectNetwork = {
                        navController.slideFromRight(R.id.depositCexFragment, args(cexAsset, it))
                    }
                )
            }

        } else {
            val view = LocalView.current
            HudHelper.showErrorMessage(view, stringResource(id = R.string.Error_ParameterNotSet))
            navController.popBackStack()
        }
    }

}
