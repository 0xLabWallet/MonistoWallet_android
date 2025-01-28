package com.monistoWallet.modules.addtoken

import com.monistoWallet.core.App
import com.monistoWallet.core.customCoinUid
import com.monistoWallet.modules.addtoken.AddTokenModule.IAddTokenBlockchainService
import com.wallet0x.marketkit.models.Blockchain
import com.wallet0x.marketkit.models.Coin
import com.wallet0x.marketkit.models.Token
import com.wallet0x.marketkit.models.TokenQuery
import com.wallet0x.marketkit.models.TokenType
import com.wallet0x.tronkit.models.Address
import com.wallet0x.tronkit.network.Network
import com.wallet0x.tronkit.rpc.Trc20Provider

class AddTronTokenBlockchainService(
    private val blockchain: Blockchain,
    private val trc20Provider: Trc20Provider
) : IAddTokenBlockchainService {

    override fun isValid(reference: String): Boolean {
        return try {
            Address.fromBase58(reference)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun tokenQuery(reference: String): TokenQuery {
        return TokenQuery(blockchain.type, TokenType.Eip20(reference))
    }

    override suspend fun token(reference: String): Token {
        val tokenInfo = trc20Provider.getTokenInfo(Address.fromBase58(reference))
        val tokenQuery = tokenQuery(reference)
        return Token(
            coin = Coin(tokenQuery.customCoinUid, tokenInfo.tokenName, tokenInfo.tokenSymbol, tokenInfo.tokenDecimal),
            blockchain = blockchain,
            type = tokenQuery.tokenType,
            decimals = tokenInfo.tokenDecimal
        )
    }

    companion object {
        fun getInstance(blockchain: Blockchain): AddTronTokenBlockchainService {
            val trc20Provider = Trc20Provider.getInstance(Network.Mainnet, com.monistoWallet.core.App.appConfigProvider.trongridApiKeys)
            return AddTronTokenBlockchainService(blockchain, trc20Provider)
        }
    }
}
