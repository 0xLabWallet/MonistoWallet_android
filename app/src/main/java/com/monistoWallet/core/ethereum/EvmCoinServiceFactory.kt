package com.monistoWallet.core.ethereum

import com.monistoWallet.core.ICoinManager
import com.monistoWallet.core.managers.CurrencyManager
import com.monistoWallet.core.managers.MarketKitWrapper
import com.wallet0x.ethereumkit.models.Address
import com.wallet0x.marketkit.models.Token
import com.wallet0x.marketkit.models.TokenQuery
import com.wallet0x.marketkit.models.TokenType

class EvmCoinServiceFactory(
    private val baseToken: Token,
    private val marketKit: MarketKitWrapper,
    private val currencyManager: CurrencyManager,
    private val coinManager: ICoinManager
) {
    val baseCoinService = EvmCoinService(baseToken, currencyManager, marketKit)

    fun getCoinService(contractAddress: Address) = getCoinService(contractAddress.hex)

    fun getCoinService(contractAddress: String) = getToken(contractAddress)?.let { token ->
        EvmCoinService(token, currencyManager, marketKit)
    }

    fun getCoinService(token: Token) = EvmCoinService(token, currencyManager, marketKit)

    private fun getToken(contractAddress: String): Token? {
        val tokenQuery = TokenQuery(baseToken.blockchainType, TokenType.Eip20(contractAddress))
        return coinManager.getToken(tokenQuery)
    }

}
