package com.monistoWallet.modules.walletconnect.list

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.modules.walletconnect.list.ui.WCSessionsScreen

class WCListFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val deepLinkUri = arguments?.getString(WC_CONNECTION_URI_KEY)
        WCSessionsScreen(
            navController,
            deepLinkUri
        )
    }

    companion object {
        private const val WC_CONNECTION_URI_KEY = "wc_connection_uri_key"

        fun prepareParams(deepLinkUri: String?) = bundleOf(WC_CONNECTION_URI_KEY to deepLinkUri)
    }

}
