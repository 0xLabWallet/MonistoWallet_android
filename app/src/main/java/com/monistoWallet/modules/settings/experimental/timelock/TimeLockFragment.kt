package com.monistoWallet.modules.settings.experimental.timelock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.modules.settings.experimental.ActivateCell
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.HsBackButton
import com.monistoWallet.ui.compose.components.InfoText

class TimeLockFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        ExperimentalScreen(
            navController
        )
    }

}

@Composable
private fun ExperimentalScreen(
    navController: NavController,
    viewModel: TimeLockViewModel = viewModel(factory = TimeLockModule.Factory())
) {
    Column(
        modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)
    ) {
        AppBar(
            title = stringResource(R.string.BitcoinHodling_Title),
            navigationIcon = {
                HsBackButton(onClick = { navController.popBackStack() })
            }
        )
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            ActivateCell(
                checked = viewModel.timeLockActivated,
                onChecked = { activated -> viewModel.setActivated(activated) }
            )
            InfoText(
                text = stringResource(R.string.BitcoinHodling_Description)
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
