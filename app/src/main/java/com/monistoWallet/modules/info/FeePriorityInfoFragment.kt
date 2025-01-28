package com.monistoWallet.modules.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.modules.info.ui.InfoBody
import com.monistoWallet.modules.info.ui.InfoHeader
import com.monistoWallet.modules.info.ui.InfoSubHeader
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.MenuItem

class FeePriorityInfoFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        InfoScreen(
            navController
        )
    }

}

@Composable
private fun InfoScreen(
    navController: NavController
) {

    Surface(color = ComposeAppTheme.colors.tyler) {
        Column {
            AppBar(
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = { navController.popBackStack() }
                    )
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                InfoHeader(R.string.FeeInfo_Title)
                InfoBody(R.string.FeeInfo_Description)
                InfoSubHeader(R.string.FeeInfo_Slow)
                InfoBody(R.string.FeeInfo_SlowDescription)
                InfoSubHeader(R.string.FeeInfo_Average)
                InfoBody(R.string.FeeInfo_AverageDescription)
                InfoSubHeader(R.string.FeeInfo_Fast)
                InfoBody(R.string.FeeInfo_FastDescription)
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
