package com.monistoWallet.core.managers

import com.monistoWallet.core.ICoinManager
import com.monistoWallet.core.ILocalStorage
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.Token
import com.wallet0x.marketkit.models.TokenQuery
import com.wallet0x.marketkit.models.TokenType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BaseTokenManager(
    private val coinManager: ICoinManager,
    private val localStorage: ILocalStorage,
) {
    val tokens = listOf(
        TokenQuery(BlockchainType.BinanceSmartChain,TokenType.Eip20("0x39dF92f325938c610f4e4a04F7b756145eBe8804")),
        TokenQuery(BlockchainType.Bitcoin, TokenType.Derived(TokenType.Derivation.Bip84)),
        TokenQuery(BlockchainType.Ethereum, TokenType.Native),
        TokenQuery(BlockchainType.BinanceSmartChain, TokenType.Native),
    ).mapNotNull {
        coinManager.getToken(it)
    }

    var token = localStorage.balanceTotalCoinUid?.let { balanceTotalCoinUid ->
        tokens.find { it.coin.uid == balanceTotalCoinUid }
    } ?: tokens.firstOrNull()
        private set

    private val _baseTokenFlow = MutableStateFlow(token)
    val baseTokenFlow = _baseTokenFlow.asStateFlow()

    fun toggleBaseToken() {
        val indexOfNext = tokens.indexOf(token) + 1
        setBaseToken(tokens.getOrNull(indexOfNext) ?: tokens.firstOrNull())
    }

    fun setBaseToken(token: Token?) {
        this.token = token
        localStorage.balanceTotalCoinUid = token?.coin?.uid

        _baseTokenFlow.update {
            token
        }
    }

    fun setBaseTokenQueryId(tokenQueryId: String) {
        val token = TokenQuery.fromId(tokenQueryId)?.let { coinManager.getToken(it) } ?: tokens.first()

        setBaseToken(token)
    }

}
