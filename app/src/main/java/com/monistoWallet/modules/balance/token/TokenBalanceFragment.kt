package com.monistoWallet.modules.balance.token

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.entities.Wallet
import com.monistoWallet.modules.transactions.TransactionsModule
import com.monistoWallet.modules.transactions.TransactionsViewModel
import com.monistoWallet.core.parcelable

class TokenBalanceFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val wallet = requireArguments().parcelable<Wallet>(WALLET_KEY)
        if (wallet == null) {
            Toast.makeText(com.monistoWallet.core.App.instance, "Wallet is Null", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            return
        }
        val viewModel by viewModels<TokenBalanceViewModel> { TokenBalanceModule.Factory(wallet) }
        val transactionsViewModel by navGraphViewModels<TransactionsViewModel>(R.id.mainFragment) { TransactionsModule.Factory() }

        TokenBalanceScreen(
            viewModel,
            transactionsViewModel,
            navController
        )
    }

    companion object {
        private const val WALLET_KEY = "wallet_key"

        fun prepareParams(wallet: Wallet) = bundleOf(
            WALLET_KEY to wallet
        )
    }
}
