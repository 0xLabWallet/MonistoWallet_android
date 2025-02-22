package com.monistoWallet.modules.syncerror

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.slideFromBottom
import com.monistoWallet.entities.Wallet
import com.monistoWallet.modules.btcblockchainsettings.BtcBlockchainSettingsModule
import com.monistoWallet.modules.evmnetwork.EvmNetworkModule
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.ButtonPrimaryDefault
import com.monistoWallet.ui.compose.components.ButtonPrimaryTransparent
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.extensions.BaseComposableBottomSheetFragment
import com.monistoWallet.ui.extensions.BottomSheetHeader
import com.monistoWallet.core.findNavController
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.core.parcelable

class SyncErrorDialog : BaseComposableBottomSheetFragment() {
    private val error by lazy {
        requireArguments().getString(errorKey) ?: ""
    }

    private val wallet by lazy {
        requireArguments().parcelable<Wallet>(walletKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            setContent {
                wallet?.let {
                    SyncErrorScreen(findNavController(), it, error)
                }
            }
        }
    }

    companion object {
        private const val walletKey = "walletKey"
        private const val errorKey = "errorKey"

        fun prepareParams(wallet: Wallet, errorMessage: String?) = bundleOf(
            walletKey to wallet,
            errorKey to errorMessage,
        )
    }
}

@Composable
private fun SyncErrorScreen(navController: NavController, wallet: Wallet, error: String) {
    val viewModel = viewModel<SyncErrorViewModel>(factory = SyncErrorModule.Factory(wallet))

    val context = LocalContext.current
    val view = LocalView.current
    val clipboardManager = LocalClipboardManager.current

    ComposeAppTheme {
        BottomSheetHeader(
            iconPainter = painterResource(R.drawable.ic_attention_red_24),
            title = stringResource(R.string.BalanceSyncError_Title),
            onCloseClick = { navController.popBackStack() }
        ) {

            Spacer(Modifier.height(32.dp))
            ButtonPrimaryYellow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                title = stringResource(R.string.BalanceSyncError_ButtonRetry),
                onClick = {
                    viewModel.retry()
                    navController.popBackStack()
                }
            )
            if (viewModel.sourceChangeable) {
                Spacer(Modifier.height(12.dp))
                ButtonPrimaryDefault(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    title = stringResource(R.string.BalanceSyncError_ButtonChangeSource),
                    onClick = {
                        navController.popBackStack()

                        val blockchainWrapper = viewModel.blockchainWrapper
                        when (blockchainWrapper?.type) {
                            SyncErrorModule.BlockchainWrapper.Type.Bitcoin -> {
                                val params =
                                    BtcBlockchainSettingsModule.args(blockchainWrapper.blockchain)
                                navController.slideFromBottom(
                                    R.id.btcBlockchainSettingsFragment,
                                    params
                                )
                            }
                            SyncErrorModule.BlockchainWrapper.Type.Evm -> {
                                val params = EvmNetworkModule.args(blockchainWrapper.blockchain)
                                navController.slideFromBottom(R.id.evmNetworkFragment, params)
                            }
                            else -> {}
                        }
                    }
                )
            }
            Spacer(Modifier.height(12.dp))
            ButtonPrimaryTransparent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                title = stringResource(R.string.BalanceSyncError_ButtonReport),
                onClick = {
                    navController.popBackStack()

                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(viewModel.reportEmail))
                        putExtra(Intent.EXTRA_TEXT, error)
                    }

                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        clipboardManager.setText(AnnotatedString(viewModel.reportEmail))
                        HudHelper.showSuccessMessage(view, R.string.Hud_Text_EmailAddressCopied)
                    }
                }
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}

