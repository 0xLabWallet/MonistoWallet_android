package com.monistoWallet.modules.transactionInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.modules.transactions.TransactionsModule
import com.monistoWallet.modules.transactions.TransactionsViewModel
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.CellUniversalLawrenceSection
import com.monistoWallet.ui.compose.components.MenuItem
import com.monistoWallet.ui.compose.components.SectionTitleCell
import com.monistoWallet.ui.compose.components.TitleAndValueCell
import com.monistoWallet.ui.compose.components.TransactionAmountCell
import com.monistoWallet.ui.compose.components.TransactionInfoAddressCell
import com.monistoWallet.ui.compose.components.TransactionInfoBtcLockCell
import com.monistoWallet.ui.compose.components.TransactionInfoCancelCell
import com.monistoWallet.ui.compose.components.TransactionInfoContactCell
import com.monistoWallet.ui.compose.components.TransactionInfoDoubleSpendCell
import com.monistoWallet.ui.compose.components.TransactionInfoExplorerCell
import com.monistoWallet.ui.compose.components.TransactionInfoRawTransaction
import com.monistoWallet.ui.compose.components.TransactionInfoSentToSelfCell
import com.monistoWallet.ui.compose.components.TransactionInfoSpeedUpCell
import com.monistoWallet.ui.compose.components.TransactionInfoStatusCell
import com.monistoWallet.ui.compose.components.TransactionInfoTransactionHashCell
import com.monistoWallet.ui.compose.components.TransactionNftAmountCell
import com.monistoWallet.ui.compose.components.WarningMessageCell

class TransactionInfoFragment : BaseComposeFragment() {

    private val viewModelTxs by navGraphViewModels<TransactionsViewModel>(R.id.mainFragment) { TransactionsModule.Factory() }

    @Composable
    override fun GetContent(navController: NavController) {
        val viewItem = viewModelTxs.tmpItemToShow
        if (viewItem == null) {
            navController.popBackStack()
            return
        }

        val viewModel by navGraphViewModels<TransactionInfoViewModel>(R.id.transactionInfoFragment) {
            TransactionInfoModule.Factory(viewItem)
        }

        TransactionInfoScreen(viewModel, navController)
    }

}

@Composable
fun TransactionInfoScreen(
    viewModel: TransactionInfoViewModel,
    navController: NavController
) {

    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = stringResource(R.string.TransactionInfo_Title),
            menuItems = listOf(
                MenuItem(
                    title = TranslatableString.ResString(R.string.Button_Close),
                    icon = R.drawable.ic_close,
                    onClick = {
                        navController.popBackStack()
                    }
                )
            )
        )
        TransactionInfo(viewModel, navController)
    }
}

@Composable
fun TransactionInfo(
    viewModel: TransactionInfoViewModel,
    navController: NavController
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)) {
        items(viewModel.viewItems) { section ->
            TransactionInfoSection(section, navController, viewModel::getRawTransaction)
        }
    }
}

@Composable
fun TransactionInfoSection(
    section: List<TransactionInfoViewItem>,
    navController: NavController,
    getRawTransaction: () -> String?
) {
    if (section.size == 1 && section[0] is TransactionInfoViewItem.WarningMessage) {
        (section[0] as? TransactionInfoViewItem.WarningMessage)?.let {
            WarningMessageCell(it.message)
        }
        return
    }

    CellUniversalLawrenceSection(
        buildList {
            for (viewItem in section) {
                when (viewItem) {
                    is TransactionInfoViewItem.Transaction -> {
                        add {
                            SectionTitleCell(title = viewItem.leftValue, value = viewItem.rightValue, iconResId = viewItem.icon)
                        }
                    }

                    is TransactionInfoViewItem.Amount -> {
                        add {
                            TransactionAmountCell(
                                fiatAmount = viewItem.fiatValue,
                                coinAmount = viewItem.coinValue,
                                coinIconUrl = viewItem.coinIconUrl,
                                coinIconPlaceholder = viewItem.coinIconPlaceholder,
                                coinUid = viewItem.coinUid,
                                navController = navController
                            )
                        }
                    }

                    is TransactionInfoViewItem.NftAmount -> {
                        add {
                            TransactionNftAmountCell(
                                viewItem.nftValue,
                                viewItem.iconUrl,
                                viewItem.iconPlaceholder,
                                viewItem.nftUid,
                                viewItem.providerCollectionUid,
                                navController
                            )
                        }
                    }

                    is TransactionInfoViewItem.Value -> {
                        add {
                            TitleAndValueCell(title = viewItem.title, value = viewItem.value)
                        }
                    }

                    is TransactionInfoViewItem.Address -> {
                        add {
                            TransactionInfoAddressCell(
                                title = viewItem.title,
                                value = viewItem.value,
                                showAdd = viewItem.showAdd,
                                blockchainType = viewItem.blockchainType,
                                navController = navController
                            )
                        }
                    }

                    is TransactionInfoViewItem.ContactItem -> {
                        add {
                            TransactionInfoContactCell(viewItem.contact.name)
                        }
                    }

                    is TransactionInfoViewItem.Status -> {
                        add {
                            TransactionInfoStatusCell(status = viewItem.status, navController = navController)
                        }
                    }

                    is TransactionInfoViewItem.SpeedUpCancel -> {
                        add {
                            TransactionInfoSpeedUpCell(transactionHash = viewItem.transactionHash, navController = navController)
                        }
                        add {
                            TransactionInfoCancelCell(transactionHash = viewItem.transactionHash, navController = navController)
                        }
                    }

                    is TransactionInfoViewItem.TransactionHash -> {
                        add {
                            TransactionInfoTransactionHashCell(transactionHash = viewItem.transactionHash)
                        }
                    }

                    is TransactionInfoViewItem.Explorer -> {
                        viewItem.url?.let {
                            add {
                                TransactionInfoExplorerCell(title = viewItem.title, url = viewItem.url)
                            }
                        }
                    }

                    is TransactionInfoViewItem.RawTransaction -> {
                        add {
                            TransactionInfoRawTransaction(rawTransaction = getRawTransaction)
                        }
                    }

                    is TransactionInfoViewItem.LockState -> {
                        add {
                            TransactionInfoBtcLockCell(lockState = viewItem, navController = navController)
                        }
                    }

                    is TransactionInfoViewItem.DoubleSpend -> {
                        add {
                            TransactionInfoDoubleSpendCell(
                                transactionHash = viewItem.transactionHash,
                                conflictingHash = viewItem.conflictingHash,
                                navController = navController
                            )
                        }
                    }

                    is TransactionInfoViewItem.SentToSelf -> {
                        add {
                            TransactionInfoSentToSelfCell()
                        }
                    }

                    is TransactionInfoViewItem.WarningMessage -> {
                        //already handled
                    }
                }
            }
        }
    )
}

