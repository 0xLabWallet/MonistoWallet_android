package com.monistoWallet.modules.tokenselect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.monistoWallet.core.App
import com.monistoWallet.core.managers.BalanceHiddenManager
import com.monistoWallet.core.swappable
import com.monistoWallet.modules.balance.BalanceModule
import com.monistoWallet.modules.balance.BalanceService
import com.monistoWallet.modules.balance.BalanceSortType
import com.monistoWallet.modules.balance.BalanceSorter
import com.monistoWallet.modules.balance.BalanceViewItem2
import com.monistoWallet.modules.balance.BalanceViewItemFactory
import com.monistoWallet.modules.balance.BalanceViewTypeManager
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.TokenType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TokenSelectViewModel(
    private val service: BalanceService,
    private val balanceViewItemFactory: BalanceViewItemFactory,
    private val balanceViewTypeManager: BalanceViewTypeManager,
    private val itemsFilter: ((BalanceModule.BalanceItem) -> Boolean)?,
    private val balanceSorter: BalanceSorter,
    private val balanceHiddenManager: BalanceHiddenManager,
    private val blockchainTypes: List<BlockchainType>?,
    private val tokenTypes: List<TokenType>?
) : ViewModel() {

    private var noItems = false
    private var query: String? = null
    private var balanceViewItems = listOf<BalanceViewItem2>()
    var uiState by mutableStateOf(
        TokenSelectUiState(
            items = balanceViewItems,
            noItems = noItems
        )
    )
        private set

    init {
        service.start()

        viewModelScope.launch {
            service.balanceItemsFlow.collect { items ->
                refreshViewItems(items)
            }
        }
    }

    private suspend fun refreshViewItems(balanceItems: List<BalanceModule.BalanceItem>?) {
        withContext(Dispatchers.IO) {
            if (balanceItems != null) {
                var itemsFiltered: List<BalanceModule.BalanceItem> = balanceItems
                blockchainTypes?.let { types ->
                    itemsFiltered = itemsFiltered.filter { item ->
                        types.contains(item.wallet.token.blockchainType)
                    }
                }
                tokenTypes?.let { types ->
                    itemsFiltered = itemsFiltered.filter { item ->
                        types.contains(item.wallet.token.type)
                    }
                }
                itemsFilter?.let {
                    itemsFiltered = itemsFiltered.filter(it)
                }
                noItems = itemsFiltered.isEmpty()

                val tmpQuery = query
                if (!tmpQuery.isNullOrBlank()) {
                    itemsFiltered = itemsFiltered.filter {
                        val coin = it.wallet.coin
                        coin.code.contains(tmpQuery, true) || coin.name.contains(tmpQuery, true)
                    }
                }

                val itemsSorted = balanceSorter.sort(itemsFiltered, BalanceSortType.Value)
                balanceViewItems = itemsSorted.map { balanceItem ->
                    balanceViewItemFactory.viewItem2(
                        item = balanceItem,
                        currency = service.baseCurrency,
                        hideBalance = balanceHiddenManager.balanceHidden,
                        watchAccount = service.isWatchAccount,
                        balanceViewType = balanceViewTypeManager.balanceViewTypeFlow.value,
                        networkAvailable = service.networkAvailable
                    )
                }
            } else {
                balanceViewItems = listOf()
            }

            emitState()
        }
    }

    fun updateFilter(q: String) {
        viewModelScope.launch {
            query = q
            refreshViewItems(service.balanceItemsFlow.value)
        }
    }

    private fun emitState() {
        viewModelScope.launch {
            uiState = TokenSelectUiState(
                items = balanceViewItems,
                noItems = noItems,
            )
        }
    }

    override fun onCleared() {
        service.clear()
    }

    class FactoryForSend(
        private val blockchainTypes: List<BlockchainType>? = null,
        private val tokenTypes: List<TokenType>? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TokenSelectViewModel(
                service = BalanceService.getInstance("wallet"),
                balanceViewItemFactory = BalanceViewItemFactory(),
                balanceViewTypeManager = com.monistoWallet.core.App.balanceViewTypeManager,
                itemsFilter = null,
                balanceSorter = BalanceSorter(),
                balanceHiddenManager = com.monistoWallet.core.App.balanceHiddenManager,
                blockchainTypes = blockchainTypes,
                tokenTypes = tokenTypes,
            ) as T
        }
    }

    class FactoryForSwap : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TokenSelectViewModel(
                service = BalanceService.getInstance("wallet"),
                balanceViewItemFactory = BalanceViewItemFactory(),
                balanceViewTypeManager = com.monistoWallet.core.App.balanceViewTypeManager,
                itemsFilter = {
                    it.wallet.token.swappable
                },
                balanceSorter = BalanceSorter(),
                balanceHiddenManager = com.monistoWallet.core.App.balanceHiddenManager,
                blockchainTypes = null,
                tokenTypes = null,
            ) as T
        }
    }
}

data class TokenSelectUiState(
    val items: List<BalanceViewItem2>,
    val noItems: Boolean,
)
