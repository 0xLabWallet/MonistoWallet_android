package com.monistoWallet.core.managers

import com.monistoWallet.core.IWalletManager
import com.monistoWallet.entities.Wallet
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.Token
import com.wallet0x.marketkit.models.TokenQuery
import com.wallet0x.marketkit.models.TokenType

class WalletActivator(
    private val walletManager: IWalletManager,
    private val marketKit: MarketKitWrapper,
) {

    fun activateWallets(account: com.monistoWallet.entities.Account, tokenQueries: List<TokenQuery>) {

        val wallets = tokenQueries.mapNotNull { tokenQuery ->
            marketKit.token(tokenQuery)?.let { token ->
                Wallet(token, account)
            }
        }
        walletManager.save(wallets)
    }


    fun activateTokens(account: com.monistoWallet.entities.Account, tokens: List<Token>) {
        val wallets = mutableListOf<Wallet>()

        for (token in tokens) {
            wallets.add(Wallet(token, account))
        }

        walletManager.save(wallets)
    }

}
