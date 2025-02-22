package com.monistoWallet.modules.addtoken

import com.monistoWallet.core.INetworkManager
import com.monistoWallet.core.customCoinUid
import com.monistoWallet.modules.addtoken.AddTokenModule.IAddTokenBlockchainService
import com.wallet0x.marketkit.models.*

class AddBep2TokenBlockchainService(
    private val blockchain: Blockchain,
    private val networkManager: INetworkManager
) : IAddTokenBlockchainService {

    override fun isValid(reference: String): Boolean {
        //check reference for period in the middle
        val regex = "\\w+-\\w+".toRegex()
        return regex.matches(reference)
    }

    override fun tokenQuery(reference: String): TokenQuery {
        return TokenQuery(BlockchainType.BinanceChain, TokenType.Bep2(reference))
    }

    override suspend fun token(reference: String): Token {
        val bep2Tokens = networkManager.getBep2Tokens()
        val tokenInfo = bep2Tokens.firstOrNull { it.symbol == reference }
            ?: throw AddTokenService.TokenError.NotFound
        val tokenQuery = tokenQuery(reference)
        return Token(
            coin = Coin(tokenQuery.customCoinUid, tokenInfo.name, tokenInfo.original_symbol),
            blockchain = blockchain,
            type = tokenQuery.tokenType,
            decimals = 0
        )
    }
}
