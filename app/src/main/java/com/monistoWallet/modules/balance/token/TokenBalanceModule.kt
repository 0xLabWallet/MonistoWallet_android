package com.monistoWallet.modules.balance.token

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.entities.Wallet
import com.monistoWallet.modules.balance.BalanceAdapterRepository
import com.monistoWallet.modules.balance.BalanceCache
import com.monistoWallet.modules.balance.BalanceViewItem
import com.monistoWallet.modules.balance.BalanceViewItemFactory
import com.monistoWallet.modules.balance.BalanceXRateRepository
import com.monistoWallet.modules.transactions.NftMetadataService
import com.monistoWallet.modules.transactions.TransactionRecordRepository
import com.monistoWallet.modules.transactions.TransactionSyncStateRepository
import com.monistoWallet.modules.transactions.TransactionViewItem
import com.monistoWallet.modules.transactions.TransactionViewItemFactory
import com.monistoWallet.modules.transactions.TransactionsRateRepository

class TokenBalanceModule {

    class Factory(private val wallet: Wallet) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val balanceService = TokenBalanceService(
                wallet,
                BalanceXRateRepository("wallet", com.monistoWallet.core.App.currencyManager, com.monistoWallet.core.App.marketKit),
                com.monistoWallet.modules.balance.BalanceAdapterRepository(
                    com.monistoWallet.core.App.adapterManager,
                    com.monistoWallet.modules.balance.BalanceCache(
                        com.monistoWallet.core.App.appDatabase.enabledWalletsCacheDao()
                    )
                ),
            )

            val tokenTransactionsService = TokenTransactionsService(
                wallet,
                TransactionRecordRepository(com.monistoWallet.core.App.transactionAdapterManager),
                TransactionsRateRepository(com.monistoWallet.core.App.currencyManager, com.monistoWallet.core.App.marketKit),
                TransactionSyncStateRepository(com.monistoWallet.core.App.transactionAdapterManager),
                com.monistoWallet.core.App.contactsRepository,
                NftMetadataService(com.monistoWallet.core.App.nftMetadataManager),
                com.monistoWallet.core.App.spamManager
            )

            return TokenBalanceViewModel(
                wallet,
                balanceService,
                BalanceViewItemFactory(),
                tokenTransactionsService,
                TransactionViewItemFactory(com.monistoWallet.core.App.evmLabelManager, com.monistoWallet.core.App.contactsRepository, com.monistoWallet.core.App.balanceHiddenManager),
                com.monistoWallet.core.App.balanceHiddenManager,
                com.monistoWallet.core.App.connectivityManager
            ) as T
        }
    }

    data class TokenBalanceUiState(
        val title: String,
        val balanceViewItem: BalanceViewItem?,
        val transactions: Map<String, List<TransactionViewItem>>?,
    )
}
