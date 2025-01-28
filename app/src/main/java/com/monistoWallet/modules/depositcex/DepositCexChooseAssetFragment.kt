package com.monistoWallet.modules.depositcex

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.core.slideFromRight

class DepositCexChooseAssetFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        DepositCexChooseAssetScreen(navController)
    }

}

@Composable
fun DepositCexChooseAssetScreen(navController: NavController) {
    SelectCoinScreen(
        onClose = { navController.popBackStack() },
        itemIsSuspended = { !it.depositEnabled },
        onSelectAsset = { cexAsset ->
            navController.slideFromRight(R.id.depositCexFragment, DepositCexFragment.args(cexAsset))
        },
        withBalance = false
    )
}
