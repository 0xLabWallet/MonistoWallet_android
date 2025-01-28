package com.monistoWallet.modules.contacts.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monistoWallet.R
import com.monistoWallet.modules.addtoken.blockchainselector.BlockchainCell
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.HSSectionRounded
import com.monistoWallet.ui.compose.components.HsBackButton
import com.monistoWallet.ui.compose.components.MenuItem
import com.wallet0x.marketkit.models.Blockchain

@Composable
fun BlockchainSelectorScreen(
    blockchains: List<Blockchain>,
    selectedBlockchain: Blockchain,
    onSelectBlockchain: (Blockchain) -> Unit,
    onNavigateToBack: () -> Unit
) {
    val menuItems = emptyList<MenuItem>()
    var selectedItem by remember { mutableStateOf(selectedBlockchain) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = ComposeAppTheme.colors.tyler)
    ) {
        AppBar(
            title = stringResource(R.string.Market_Filter_Blockchains),
            navigationIcon = {
                HsBackButton(onNavigateToBack)
            },
            menuItems = menuItems
        )

        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            HSSectionRounded {
                blockchains.forEachIndexed { index, item ->
                    BlockchainCell(
                        item = item,
                        selected = selectedItem == item,
                        onCheck = {
                            selectedItem = item

                            onSelectBlockchain(it)
                        },
                        borderTop = index != 0
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
