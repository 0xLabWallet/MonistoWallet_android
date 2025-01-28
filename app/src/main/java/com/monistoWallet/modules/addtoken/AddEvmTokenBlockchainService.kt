package com.monistoWallet.modules.addtoken

import com.monistoWallet.core.App
import com.monistoWallet.core.customCoinUid
import com.monistoWallet.modules.addtoken.AddTokenModule.IAddTokenBlockchainService
import com.wallet0x.erc20kit.core.Eip20Provider
import com.wallet0x.ethereumkit.core.AddressValidator
import com.wallet0x.ethereumkit.models.Address
import com.wallet0x.ethereumkit.models.RpcSource
import com.wallet0x.marketkit.models.*
import kotlinx.coroutines.rx2.await

class AddEvmTokenBlockchainService(
    private val blockchain: Blockchain,
    private val eip20Provider: Eip20Provider
) : IAddTokenBlockchainService {

    override fun isValid(reference: String): Boolean {
        return try {
            AddressValidator.validate(reference)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun tokenQuery(reference: String): TokenQuery {
        return TokenQuery(blockchain.type, TokenType.Eip20(reference.lowercase()))
    }

    override suspend fun token(reference: String): Token {
        val tokenInfo = eip20Provider.getTokenInfo(Address(reference)).await()
        val tokenQuery = tokenQuery(reference)
        return Token(
            coin = Coin(tokenQuery.customCoinUid, tokenInfo.tokenName, tokenInfo.tokenSymbol, tokenInfo.tokenDecimal),
            blockchain = blockchain,
            type = tokenQuery.tokenType,
            decimals = tokenInfo.tokenDecimal
        )
    }

    companion object {
        fun getInstance(blockchain: Blockchain): AddEvmTokenBlockchainService {
            val httpSyncSource = com.monistoWallet.core.App.evmSyncSourceManager.getHttpSyncSource(blockchain.type)
            val httpRpcSource = httpSyncSource?.rpcSource as? RpcSource.Http
                ?: throw IllegalStateException("No HTTP RPC Source for blockchain ${blockchain.type}")

            val eip20Provider = Eip20Provider.instance(httpRpcSource)
            return AddEvmTokenBlockchainService(blockchain, eip20Provider)
        }
    }
}
