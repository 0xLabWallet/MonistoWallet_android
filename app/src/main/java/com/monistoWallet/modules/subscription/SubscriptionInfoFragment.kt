package com.monistoWallet.modules.subscription

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.core.slideFromBottom
import com.monistoWallet.modules.evmfee.ButtonsGroupWithShade
import com.monistoWallet.modules.info.ui.InfoHeader
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.ButtonPrimaryTransparent
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.InfoH3
import com.monistoWallet.ui.compose.components.MenuItem
import com.monistoWallet.ui.compose.components.body_bran

class SubscriptionInfoFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val uriHandler = LocalUriHandler.current

        SubscriptionInfoScreen(
            onClickGetPremium = {
                uriHandler.openUri(App.appConfigProvider.analyticsLink)
            },
            onClickHavePremium = {
                navController.popBackStack()
                navController.slideFromBottom(R.id.activateSubscription)
            },
            onClose = {
                navController.popBackStack()
            }
        )
    }

}

@Composable
private fun SubscriptionInfoScreen(
    onClickGetPremium: () -> Unit,
    onClickHavePremium: () -> Unit,
    onClose: () -> Unit
) {
    Surface(color = ComposeAppTheme.colors.tyler) {
        Column {
            AppBar(
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = onClose
                    )
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                InfoHeader(R.string.SubscriptionInfo_Title)

                InfoH3(stringResource(R.string.SubscriptionInfo_Analytics_Title))
                body_bran(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
                    text = stringResource(R.string.SubscriptionInfo_Analytics_Info)
                )

                InfoH3(stringResource(R.string.SubscriptionInfo_Indicators_Title))
                body_bran(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
                    text = stringResource(R.string.SubscriptionInfo_Indicators_Info)
                )

                InfoH3(stringResource(R.string.SubscriptionInfo_PersonalSupport_Title))
                body_bran(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
                    text = stringResource(R.string.SubscriptionInfo_PersonalSupport_Info)
                )
            }

            ButtonsGroupWithShade {
                Column(Modifier.padding(horizontal = 24.dp)) {
                    ButtonPrimaryYellow(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.SubscriptionInfo_GetPremium),
                        onClick = onClickGetPremium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ButtonPrimaryTransparent(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.SubscriptionInfo_HavePremium),
                        onClick = onClickHavePremium
                    )
                }
            }
        }
    }
}
