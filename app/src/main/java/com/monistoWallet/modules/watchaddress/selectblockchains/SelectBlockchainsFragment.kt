package com.monistoWallet.modules.watchaddress.selectblockchains

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.modules.manageaccounts.ManageAccountsModule
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.CellMultilineClear
import com.monistoWallet.ui.compose.components.HsBackButton
import com.monistoWallet.ui.compose.components.HsSwitch
import com.monistoWallet.ui.compose.components.MenuItem
import com.monistoWallet.ui.compose.components.body_leah
import com.monistoWallet.ui.compose.components.subhead2_grey
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.core.parcelable
import com.monistoWallet.entities.AccountType
import kotlinx.coroutines.delay

class SelectBlockchainsFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val popUpToInclusiveId =
            arguments?.getInt(ManageAccountsModule.popOffOnSuccessKey, R.id.selectBlockchainsFragment) ?: R.id.selectBlockchainsFragment
        val inclusive =
            arguments?.getBoolean(ManageAccountsModule.popOffInclusiveKey) ?: false
        val accountType = arguments?.parcelable<AccountType>(SelectBlockchainsModule.accountTypeKey)
        val accountName = arguments?.getString(SelectBlockchainsModule.accountNameKey)
        if (accountType != null) {
            SelectBlockchainsScreen(
                accountType,
                accountName,
                navController,
                popUpToInclusiveId,
                inclusive
            )
        } else {
            navController.popBackStack()
        }
    }

}

@Composable
private fun SelectBlockchainsScreen(
    accountType: AccountType,
    accountName: String?,
    navController: NavController,
    popUpToInclusiveId: Int,
    inclusive: Boolean
) {
    val viewModel = viewModel<SelectBlockchainsViewModel>(factory = SelectBlockchainsModule.Factory(accountType, accountName))

    val view = LocalView.current
    val uiState = viewModel.uiState
    val title = uiState.title
    val accountCreated = uiState.accountCreated
    val submitEnabled = uiState.submitButtonEnabled
    val blockchainViewItems = uiState.coinViewItems

    LaunchedEffect(accountCreated) {
        if (accountCreated) {
            HudHelper.showSuccessMessage(
                contenView = view,
                resId = R.string.Hud_Text_AddressAdded,
                icon = R.drawable.icon_binocule_24,
                iconTint = R.color.white
            )
            delay(300)
            navController.popBackStack(popUpToInclusiveId, inclusive)
        }
    }

    Column(
        modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)
    ) {
        AppBar(
            title = stringResource(title),
            navigationIcon = {
                HsBackButton(onClick = { navController.popBackStack() })
            },
            menuItems = listOf(
                MenuItem(
                    title = TranslatableString.ResString(R.string.Button_Done),
                    onClick = viewModel::onClickWatch,
                    enabled = submitEnabled
                )
            ),
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(
                    thickness = 1.dp,
                    color = ComposeAppTheme.colors.steel10,
                )
            }
            items(blockchainViewItems) { viewItem ->
                CellMultilineClear(
                    borderBottom = true,
                    onClick = { viewModel.onToggle(viewItem.item) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Image(
                            painter = viewItem.imageSource.painter(),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(32.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                body_leah(
                                    text = viewItem.title,
                                    maxLines = 1,
                                )
                                viewItem.label?.let { labelText ->
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 6.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ComposeAppTheme.colors.jeremy)
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(
                                                start = 4.dp,
                                                end = 4.dp,
                                                bottom = 1.dp
                                            ),
                                            text = labelText,
                                            color = ComposeAppTheme.colors.bran,
                                            style = ComposeAppTheme.typography.microSB,
                                            maxLines = 1,
                                        )
                                    }
                                }
                            }
                            subhead2_grey(
                                text = viewItem.subtitle,
                                maxLines = 1,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        HsSwitch(
                            checked = viewItem.enabled,
                            onCheckedChange = { viewModel.onToggle(viewItem.item) },
                        )
                    }
                }
            }
        }
    }
}
