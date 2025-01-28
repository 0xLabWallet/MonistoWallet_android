package com.monistoWallet.modules.balance.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.entities.AccountType
import com.monistoWallet.modules.balance.BalanceAccountsViewModel
import com.monistoWallet.modules.balance.BalanceModule
import com.monistoWallet.modules.balance.BalanceScreenState
import com.monistoWallet.modules.balance.cex.BalanceForAccountCex

@Composable
fun BalanceScreen(navController: NavController) {
    val viewModel = viewModel<BalanceAccountsViewModel>(factory = BalanceModule.AccountsFactory())

    Box {
        when (val tmpAccount = viewModel.balanceScreenState) {
            BalanceScreenState.NoAccount -> BalanceNoAccount(navController)
            is BalanceScreenState.HasAccount -> when (tmpAccount.accountViewItem.type) {
                is AccountType.Cex -> {
                    BalanceForAccountCex(navController, tmpAccount.accountViewItem)
                }

                else -> {
                    BalanceForAccount(navController, tmpAccount.accountViewItem)
                }
            }

            else -> {}
        }
    }

}



