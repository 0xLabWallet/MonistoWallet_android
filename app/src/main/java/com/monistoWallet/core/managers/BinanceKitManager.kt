package com.monistoWallet.core.managers

import com.monistoWallet.core.App
import com.monistoWallet.core.IBinanceKitManager
import com.monistoWallet.core.UnsupportedAccountException
import com.monistoWallet.entities.Wallet
import com.wallet0x.binancechainkit.BinanceChainKit

class BinanceKitManager : IBinanceKitManager {
    private var kit: BinanceChainKit? = null
    private var useCount = 0
    private var currentAccount: com.monistoWallet.entities.Account? = null

    override val binanceKit: BinanceChainKit?
        get() = kit

    override val statusInfo: Map<String, Any>?
        get() = kit?.statusInfo()

    override fun binanceKit(wallet: Wallet): BinanceChainKit {
        val account = wallet.account
        val accountType = account.type

        if (kit != null && currentAccount != account) {
            kit?.stop()
            kit = null
            currentAccount = null
        }

        if (kit == null) {
            if (accountType !is com.monistoWallet.entities.AccountType.Mnemonic)
                throw UnsupportedAccountException()

            useCount = 0

            kit = createKitInstance( accountType, account)
            currentAccount = account
        }

        useCount++
        return kit!!
    }

    private fun createKitInstance(accountType: com.monistoWallet.entities.AccountType.Mnemonic, account: com.monistoWallet.entities.Account): BinanceChainKit {
        val networkType = BinanceChainKit.NetworkType.MainNet

        val kit = BinanceChainKit.instance(com.monistoWallet.core.App.instance, accountType.words, accountType.passphrase, account.id, networkType)
        kit.refresh()

        return kit
    }

    override fun unlink(account: com.monistoWallet.entities.Account) {
        if (currentAccount != account) return

        useCount -= 1

        if (useCount < 1) {
            kit?.stop()
            kit = null
            currentAccount = null
        }
    }

}
