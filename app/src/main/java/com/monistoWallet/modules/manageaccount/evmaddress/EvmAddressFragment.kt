package com.monistoWallet.modules.manageaccount.evmaddress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.modules.manageaccount.ui.ActionButton
import com.monistoWallet.modules.manageaccount.ui.HidableContent
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.HsBackButton
import com.monistoWallet.ui.helpers.TextHelper
import com.monistoWallet.core.helpers.HudHelper

class EvmAddressFragment : BaseComposeFragment(screenshotEnabled = false) {

    companion object {
        const val EVM_ADDRESS_KEY = "evm_address_key"
    }

    @Composable
    override fun GetContent(navController: NavController) {
        val evmAddress = arguments?.getString(EVM_ADDRESS_KEY) ?: ""
        EvmAddressScreen(evmAddress, navController)
    }

}

@Composable
private fun EvmAddressScreen(evmAddress: String, navController: NavController) {
    val view = LocalView.current
    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = stringResource(R.string.PublicKeys_EvmAddress),
            navigationIcon = {
                HsBackButton(onClick = navController::popBackStack)
            },
            menuItems = listOf(
//                MenuItem(
//                    title = TranslatableString.ResString(R.string.Info_Title),
//                    icon = R.drawable.ic_info_24,
//                    onClick = {
//                        FaqManager.showFaqPage(navController, FaqManager.faqPathPrivateKeys)
//                    }
//                )
            )
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(12.dp))
            HidableContent(evmAddress)
            Spacer(Modifier.height(24.dp))
        }
        ActionButton(R.string.Alert_Copy) {
            TextHelper.copyText(evmAddress)
            HudHelper.showSuccessMessage(view, R.string.Hud_Text_Copied)
        }
    }
}