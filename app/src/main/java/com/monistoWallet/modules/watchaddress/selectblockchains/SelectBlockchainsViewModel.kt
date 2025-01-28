package com.monistoWallet.modules.watchaddress.selectblockchains

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.R
import com.monistoWallet.core.badge
import com.monistoWallet.core.description
import com.monistoWallet.core.imageUrl
import com.monistoWallet.entities.AccountType
import com.monistoWallet.modules.restoreaccount.restoreblockchains.CoinViewItem
import com.monistoWallet.modules.watchaddress.WatchAddressService
import com.wallet0x.marketkit.models.Token

class SelectBlockchainsViewModel(
    private val accountType: AccountType,
    private val accountName: String?,
    private val service: WatchAddressService
) : ViewModel() {

    private var title: Int = R.string.Watch_Select_Blockchains
    private var coinViewItems = listOf<CoinViewItem<Token>>()
    private var selectedCoins = setOf<Token>()
    private var accountCreated = false

    var uiState by mutableStateOf(
        SelectBlockchainsUiState(
            title = title,
            coinViewItems = coinViewItems,
            submitButtonEnabled = true,
            accountCreated = false
        )
    )
        private set

    init {
        val tokens = service.tokens(accountType)
        selectedCoins = tokens.toSet()

        when (accountType) {
            is AccountType.SolanaAddress,
            is AccountType.TronAddress,
            is AccountType.BitcoinAddress,
            is AccountType.TonAddress,
            is AccountType.Cex,
            is AccountType.Mnemonic,
            is AccountType.EvmPrivateKey -> Unit // N/A
            is AccountType.EvmAddress -> {
                title = R.string.Watch_Select_Blockchains
                coinViewItems = tokens.map {
                    coinViewItemForBlockchain(it)
                }
            }

            is AccountType.HdExtendedKey -> {
                title = R.string.Watch_Select_Coins
                coinViewItems = tokens.map {
                    coinViewItemForToken(it, label = it.badge)
                }
            }
        }

        emitState()
    }

    private fun coinViewItemForBlockchain(token: Token): CoinViewItem<Token> {
        val blockchain = token.blockchain
        return CoinViewItem(
            item = token,
            imageSource = com.monistoWallet.modules.market.ImageSource.Remote(blockchain.type.imageUrl, R.drawable.ic_platform_placeholder_32),
            title = blockchain.name,
            subtitle = blockchain.description,
            enabled = true
        )
    }

    private fun coinViewItemForToken(token: Token, label: String?): CoinViewItem<Token> {
        return CoinViewItem(
            item = token,
            imageSource = com.monistoWallet.modules.market.ImageSource.Remote(coinIconUrl(token), R.drawable.coin_placeholder),
            title = token.fullCoin.coin.code,
            subtitle = token.fullCoin.coin.name,
            enabled = true,
            label = label
        )
    }

    fun coinIconUrl(token: Token): String {
        if (token.coin.code != "DEXNET") {
            return token.fullCoin.coin.imageUrl
        } else {
            return "https://s2.coinmarketcap.com/static/img/coins/64x64/28538.png"
        }
    }

    fun onToggle(token: Token) {
        selectedCoins = if (selectedCoins.contains(token))
            selectedCoins.toMutableSet().also { it.remove(token) }
        else
            selectedCoins.toMutableSet().also { it.add(token) }

        coinViewItems = coinViewItems.map { viewItem ->
            viewItem.copy(enabled = selectedCoins.contains(viewItem.item))
        }

        emitState()
    }

    fun onClickWatch() {
        service.watchTokens(accountType, selectedCoins.toList(), accountName)
        accountCreated = true
        emitState()
    }

    private fun emitState() {
        uiState = SelectBlockchainsUiState(
            title = title,
            coinViewItems = coinViewItems,
            submitButtonEnabled = selectedCoins.isNotEmpty(),
            accountCreated = accountCreated
        )
    }
}

data class SelectBlockchainsUiState(
    val title: Int,
    val coinViewItems: List<CoinViewItem<Token>>,
    val submitButtonEnabled: Boolean,
    val accountCreated: Boolean
)
