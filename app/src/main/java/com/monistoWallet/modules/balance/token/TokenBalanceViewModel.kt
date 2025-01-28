package com.monistoWallet.modules.balance.token

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.core.badge
import com.monistoWallet.core.managers.BalanceHiddenManager
import com.monistoWallet.core.managers.ConnectivityManager
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.Wallet
import com.monistoWallet.modules.balance.BackupRequiredError
import com.monistoWallet.modules.balance.BalanceModule
import com.monistoWallet.modules.balance.BalanceViewItem
import com.monistoWallet.modules.balance.BalanceViewItemFactory
import com.monistoWallet.modules.balance.BalanceViewModel
import com.monistoWallet.modules.balance.BalanceViewType
import com.monistoWallet.modules.transactions.TransactionItem
import com.monistoWallet.modules.transactions.TransactionViewItem
import com.monistoWallet.modules.transactions.TransactionViewItemFactory
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TokenBalanceViewModel(
    private val wallet: Wallet,
    private val balanceService: TokenBalanceService,
    private val balanceViewItemFactory: BalanceViewItemFactory,
    private val transactionsService: TokenTransactionsService,
    private val transactionViewItem2Factory: TransactionViewItemFactory,
    private val balanceHiddenManager: BalanceHiddenManager,
    private val connectivityManager: ConnectivityManager,
) : ViewModel() {

    private val title = wallet.token.coin.code + wallet.token.badge?.let { " ($it)" }.orEmpty()
    private val disposables = CompositeDisposable()

    private var balanceViewItem: BalanceViewItem? = null
    private var transactions: Map<String, List<TransactionViewItem>>? = null

    var uiState by mutableStateOf(
        TokenBalanceModule.TokenBalanceUiState(
            title = title,
            balanceViewItem = balanceViewItem,
            transactions = transactions,
        )
    )
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            balanceService.balanceItemFlow.collect { balanceItem ->
                balanceItem?.let {
                    updateBalanceViewItem(it)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            balanceHiddenManager.balanceHiddenFlow.collect {
                balanceService.balanceItem?.let {
                    updateBalanceViewItem(it)
                    transactionViewItem2Factory.updateCache()
                    transactionsService.refreshList()
                }
            }
        }

        transactionsService.itemsObservable
            .subscribeIO {
                updateTransactions(it)
            }
            .let {
                disposables.add(it)
            }

        viewModelScope.launch(Dispatchers.IO) {
            balanceService.start()
            delay(300)
            transactionsService.start()
        }
    }

    private fun emitUiState() {
        viewModelScope.launch {
            uiState = TokenBalanceModule.TokenBalanceUiState(
                title = title,
                balanceViewItem = balanceViewItem,
                transactions = transactions,
            )
        }
    }

    private fun updateTransactions(items: List<TransactionItem>) {
        transactions = items
            .map { transactionViewItem2Factory.convertToViewItemCached(it) }
            .groupBy { it.formattedDate }

        emitUiState()
    }

    private fun updateBalanceViewItem(balanceItem: BalanceModule.BalanceItem) {
        val balanceViewItem = balanceViewItemFactory.viewItem(
            balanceItem,
            balanceService.baseCurrency,
            balanceHiddenManager.balanceHidden,
            wallet.account.isWatchAccount,
            BalanceViewType.CoinThenFiat
        )

        this.balanceViewItem = balanceViewItem.copy(
            primaryValue = balanceViewItem.primaryValue.copy(value = balanceViewItem.primaryValue.value + " " + balanceViewItem.coinCode)
        )

        emitUiState()
    }

    fun getWalletForReceive(viewItem: BalanceViewItem) = when {
        viewItem.wallet.account.isBackedUp || viewItem.wallet.account.isFileBackedUp -> viewItem.wallet
        else -> throw BackupRequiredError(viewItem.wallet.account, viewItem.coinTitle)
    }

    fun onBottomReached() {
        transactionsService.loadNext()
    }

    fun willShow(viewItem: TransactionViewItem) {
        transactionsService.fetchRateIfNeeded(viewItem.uid)
    }

    fun getTransactionItem(viewItem: TransactionViewItem) = transactionsService.getTransactionItem(viewItem.uid)

    fun toggleBalanceVisibility() {
        balanceHiddenManager.toggleBalanceHidden()
    }

    fun getSyncErrorDetails(viewItem: BalanceViewItem): BalanceViewModel.SyncError = when {
        connectivityManager.isConnected -> BalanceViewModel.SyncError.Dialog(viewItem.wallet, viewItem.errorMessage)
        else -> BalanceViewModel.SyncError.NetworkNotAvailable()
    }

    override fun onCleared() {
        super.onCleared()

        disposables.clear()
        balanceService.clear()
    }

}
