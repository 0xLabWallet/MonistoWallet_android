package com.monistoWallet.modules.managewallets

import com.monistoWallet.core.Clearable
import com.monistoWallet.core.IWalletManager
import com.monistoWallet.core.eligibleTokens
import com.monistoWallet.core.isDefault
import com.monistoWallet.core.isNative
import com.monistoWallet.core.managers.RestoreSettings
import com.monistoWallet.core.order
import com.monistoWallet.core.restoreSettingTypes
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.Account
import com.monistoWallet.entities.AccountType
import com.monistoWallet.entities.Wallet
import com.monistoWallet.modules.enablecoin.restoresettings.RestoreSettingsService
import com.monistoWallet.modules.receivemain.FullCoinsProvider
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.FullCoin
import com.wallet0x.marketkit.models.Token
import com.wallet0x.marketkit.models.TokenType
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ManageWalletsService(
    private val walletManager: IWalletManager,
    private val restoreSettingsService: RestoreSettingsService,
    private val fullCoinsProvider: FullCoinsProvider?,
    private val account: Account?
) : Clearable {

    private val _itemsFlow = MutableStateFlow<List<Item>>(listOf())
    val itemsFlow
        get() = _itemsFlow.asStateFlow()

    val accountType: AccountType?
        get() = account?.type

    private var fullCoins = listOf<FullCoin>()
    private var items = listOf<Item>()

    private val disposables = CompositeDisposable()

    private var filter: String = ""

    init {
        walletManager.activeWalletsUpdatedObservable
            .subscribeIO {
                handleUpdated(it)
            }
            .let {
                disposables.add(it)
            }

        restoreSettingsService.approveSettingsObservable
            .subscribeIO {
                enable(it.token, it.settings)
            }.let {
                disposables.add(it)
            }

        sync(walletManager.activeWallets)
        syncFullCoins()
        sortItems()
        syncState()
    }

    private fun isEnabled(token: Token): Boolean {
        return walletManager.activeWallets.any { it.token == token }
    }

    private fun sync(walletList: List<Wallet>) {
        fullCoinsProvider?.setActiveWallets(walletList)
    }

    private fun fetchFullCoins(): List<FullCoin> {
        return fullCoinsProvider?.getItems() ?: listOf()
    }

    private fun syncFullCoins() {
        fullCoins = fetchFullCoins()
    }

    private fun sortItems() {
        var comparator = compareByDescending<Item> {
            it.enabled
        }

        if (filter.isBlank()) {
            comparator = comparator.thenBy {
                it.token.blockchain.type.order
            }
        }

        items = fullCoins
            .map { getItemsForFullCoin(it) }
            .flatten()
            .sortedWith(comparator)
    }

    private fun getItemsForFullCoin(fullCoin: FullCoin): List<Item> {
        val accountType = account?.type ?: return listOf()
        val eligibleTokens = fullCoin.eligibleTokens(accountType)

        val tokens = if (filter.isNotBlank()) {
            eligibleTokens
        } else if (
            accountType !is AccountType.HdExtendedKey &&
            (eligibleTokens.all { it.type is TokenType.Derived } || eligibleTokens.all { it.type is TokenType.AddressTyped })
        ) {
            eligibleTokens.filter { isEnabled(it) || it.type.isDefault }
        } else {
            eligibleTokens.filter { isEnabled(it) || it.type.isNative }
        }

        return tokens.map { getItemForToken(it) }
    }

    private fun getItemForToken(token: Token): Item {
        val enabled = isEnabled(token)

        return Item(
            token = token,
            enabled = enabled,
            hasInfo = hasInfo(token, enabled)
        )
    }

    private fun hasInfo(token: Token, enabled: Boolean) = when (token.type) {
        is TokenType.Native -> token.blockchainType is BlockchainType.Zcash && enabled
        is TokenType.Derived,
        is TokenType.AddressTyped,
        is TokenType.Eip20,
        is TokenType.Bep2,
        is TokenType.Spl -> true
        else -> false
    }

    private fun syncState() {
        _itemsFlow.update {
            buildList { addAll(items) }
        }
    }

    private fun handleUpdated(wallets: List<Wallet>) {
        sync(wallets)

        val newFullCons = fetchFullCoins()
        if (newFullCons.size > fullCoins.size) {
            fullCoins = newFullCons
            sortItems()
        }

        syncState()
    }

    private fun updateSortedItems(token: Token, enable: Boolean) {
        items = items.map { item ->
            if (item.token == token) {
                item.copy(enabled = enable)
            } else {
                item
            }
        }
    }

    private fun enable(token: Token, restoreSettings: RestoreSettings) {
        val account = this.account ?: return

        if (restoreSettings.isNotEmpty()) {
            restoreSettingsService.save(restoreSettings, account, token.blockchainType)
        }

        walletManager.save(listOf(Wallet(token, account)))

        updateSortedItems(token, true)
    }

    fun setFilter(filter: String) {
        this.filter = filter
        fullCoinsProvider?.setQuery(filter)

        syncFullCoins()
        sortItems()
        syncState()
    }

    fun enable(token: Token) {
        val account = this.account ?: return

        if (token.blockchainType.restoreSettingTypes.isNotEmpty()) {
            restoreSettingsService.approveSettings(token, account)
        } else {
            enable(token, RestoreSettings())
        }
    }

    fun disable(token: Token) {
        walletManager.activeWallets
            .firstOrNull { it.token == token }
            ?.let {
                walletManager.delete(listOf(it))
                updateSortedItems(token, false)
            }
    }

    override fun clear() {
        disposables.clear()
    }

    data class Item(
        val token: Token,
        val enabled: Boolean,
        val hasInfo: Boolean
    )
}
