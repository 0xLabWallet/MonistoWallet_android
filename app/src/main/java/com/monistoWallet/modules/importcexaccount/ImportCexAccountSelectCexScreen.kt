package com.monistoWallet.modules.importcexaccount

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monistoWallet.R
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.CellUniversalLawrenceSection
import com.monistoWallet.ui.compose.components.HSpacer
import com.monistoWallet.ui.compose.components.HsBackButton
import com.monistoWallet.ui.compose.components.InfoText
import com.monistoWallet.ui.compose.components.MenuItem
import com.monistoWallet.ui.compose.components.RowUniversal
import com.monistoWallet.ui.compose.components.body_leah
import com.monistoWallet.ui.compose.components.subhead2_grey

@Composable
fun ImportCexAccountSelectCexScreen(
    onSelectCex: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onClose: () -> Unit,
) {
    val viewModel =
        viewModel<ImportCexAccountViewModel>(factory = ImportCexAccountViewModel.Factory())
    val cexItems = viewModel.cexItems

    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            AppBar(
                title = stringResource(R.string.ImportCexAccount_SelectCex),
                navigationIcon = {
                    HsBackButton(onClick = onNavigateBack)
                },
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = onClose
                    )
                )
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            InfoText(text = stringResource(R.string.ImportCexAccount_SelectCexDescription))

            CellUniversalLawrenceSection(cexItems) {
                RowUniversal(
                    onClick = {
                        onSelectCex.invoke(it.id)
                    }
                ) {
                    Image(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(32.dp),
                        painter = painterResource(it.icon),
                        contentDescription = null
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        body_leah(
                            text = it.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(1.dp))
                        subhead2_grey(
                            text = it.url,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.ic_arrow_right),
                        contentDescription = null,
                    )
                    HSpacer(width = 16.dp)
                }
            }
        }
    }
}