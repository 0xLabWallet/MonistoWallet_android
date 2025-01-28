package com.monistoWallet.modules.addtoken

import com.monistoWallet.core.App
import com.monistoWallet.core.IAccountManager
import com.monistoWallet.core.ICoinManager
import com.monistoWallet.core.IWalletManager
import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.core.order
import com.monistoWallet.entities.Wallet
import com.wallet0x.marketkit.models.Blockchain
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.Token
import com.wallet0x.marketkit.models.TokenType

class AddTokenService(
    private val coinManager: ICoinManager,
    private val walletManager: IWalletManager,
    private val accountManager: IAccountManager,
    marketKit: MarketKitWrapper,
) {

    private val blockchainTypes = listOf(
        BlockchainType.Ethereum,
        BlockchainType.BinanceSmartChain,
        BlockchainType.Dexnet,
        BlockchainType.Tron,
        BlockchainType.Polygon,
        BlockchainType.Avalanche,
        BlockchainType.BinanceChain,
        BlockchainType.Gnosis,
        BlockchainType.Fantom,
        BlockchainType.ArbitrumOne,
        BlockchainType.Optimism,
    )

    val blockchains = marketKit
        .blockchains(blockchainTypes.map { it.uid })
        .sortedBy { it.type.order }

    val accountType = accountManager.activeAccount?.type

    suspend fun tokenInfo(blockchain: Blockchain, reference: String): TokenInfo? {
        if (reference.isEmpty()) return null

        val blockchainService = when (blockchain.type) {
            BlockchainType.BinanceChain -> AddBep2TokenBlockchainService(
                blockchain,
                com.monistoWallet.core.App.networkManager
            )
            BlockchainType.Tron -> {
                AddTronTokenBlockchainService.getInstance(blockchain)
            }
            else -> AddEvmTokenBlockchainService.getInstance(blockchain)
        }

        if (!blockchainService.isValid(reference)) throw TokenError.InvalidReference

        val token = coinManager.getToken(blockchainService.tokenQuery(reference))
        if (token != null && token.type !is TokenType.Unsupported) {
            return TokenInfo(token, true)
        }

        try {
            val customToken = blockchainService.token(reference)
            return TokenInfo(customToken, false)
        } catch (e: Throwable) {
            throw TokenError.NotFound
        }
    }

    fun addToken(token: TokenInfo) {
        val account = accountManager.activeAccount ?: return
        val wallet = Wallet(token.token, account)
        walletManager.save(listOf(wallet))
    }

    sealed class TokenError : Exception() {
        object InvalidReference : TokenError()
        object NotFound : TokenError()
    }

    data class TokenInfo(
        val token: Token,
        val inCoinList: Boolean,
    )
}
