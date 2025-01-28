package com.monistoWallet.modules.settings.experimental

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.B2
import com.monistoWallet.ui.compose.components.CellUniversalLawrenceSection
import com.monistoWallet.ui.compose.components.HsBackButton
import com.monistoWallet.ui.compose.components.HsSwitch
import com.monistoWallet.ui.compose.components.RowUniversal
import com.monistoWallet.ui.compose.components.TextImportantWarning

class ExperimentalFeaturesFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        ExperimentalScreen(
            onCloseClick = { navController.popBackStack() },
            openTimeLock = { navController.slideFromRight(R.id.timeLockFragment) },
        )
    }

}

@Composable
private fun ExperimentalScreen(
    onCloseClick: () -> Unit,
    openTimeLock: () -> Unit,
) {
    Column(
        modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)
    ) {
        AppBar(
            title = stringResource(R.string.ExperimentalFeatures_Title),
            navigationIcon = {
                HsBackButton(onClick = onCloseClick)
            }
        )
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            TextImportantWarning(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                text = stringResource(R.string.ExperimentalFeatures_Description)
            )
            Spacer(Modifier.height(24.dp))
            CellUniversalLawrenceSection(
                listOf {
                    ItemCell(R.string.BitcoinHodling_Title, openTimeLock)
                }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ItemCell(title: Int, onClick: () -> Unit) {
    RowUniversal(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = onClick
    ) {
        B2(
            text = stringResource(title),
            maxLines = 1,
        )
        Spacer(Modifier.weight(1f))
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
        )
    }
}

@Composable
fun ActivateCell(
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    CellUniversalLawrenceSection(
        listOf {
            RowUniversal(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = { onChecked(!checked) }
            ) {
                B2(
                    text = stringResource(R.string.Hud_Text_Activate),
                    maxLines = 1,
                )
                Spacer(Modifier.weight(1f))
                HsSwitch(
                    checked = checked,
                    onCheckedChange = onChecked
                )
            }
        }
    )
}