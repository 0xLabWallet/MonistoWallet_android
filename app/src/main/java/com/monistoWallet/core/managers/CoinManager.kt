package com.monistoWallet.core.managers

import com.monistoWallet.core.ICoinManager
import com.monistoWallet.core.IWalletManager
import com.wallet0x.marketkit.models.Token
import com.wallet0x.marketkit.models.TokenQuery

class CoinManager(
    private val marketKit: MarketKitWrapper,
    private val walletManager: IWalletManager
) : ICoinManager {

    override fun getToken(query: TokenQuery): Token? {
        return marketKit.token(query) ?: customToken(query)
    }

    private fun customToken(tokenQuery: TokenQuery): Token? {
        return walletManager.activeWallets.find { it.token.tokenQuery == tokenQuery }?.token
    }
}
