package com.monistoWallet.core.factories

import com.monistoWallet.core.IAccountFactory
import com.monistoWallet.core.IAccountManager
import com.monistoWallet.core.managers.UserManager
import java.util.UUID

class AccountFactory(
    private val accountManager: IAccountManager,
    private val userManager: UserManager
) : IAccountFactory {

    override fun account(
        name: String,
        type: com.monistoWallet.entities.AccountType,
        origin: com.monistoWallet.entities.AccountOrigin,
        backedUp: Boolean,
        fileBackedUp: Boolean
    ): com.monistoWallet.entities.Account {
        val id = UUID.randomUUID().toString()

        return com.monistoWallet.entities.Account(
            id = id,
            name = name,
            type = type,
            origin = origin,
            level = userManager.getUserLevel(),
            isBackedUp = backedUp,
            isFileBackedUp = fileBackedUp
        )
    }

    override fun watchAccount(name: String, type: com.monistoWallet.entities.AccountType): com.monistoWallet.entities.Account {
        val id = UUID.randomUUID().toString()
        return com.monistoWallet.entities.Account(
            id = id,
            name = name,
            type = type,
            origin = com.monistoWallet.entities.AccountOrigin.Restored,
            level = userManager.getUserLevel(),
            isBackedUp = true
        )
    }

    override fun getNextWatchAccountName(): String {
        val watchAccountsCount = accountManager.accounts.count { it.isWatchAccount }

        return "Watch Wallet ${watchAccountsCount + 1}"
    }

    override fun getNextAccountName(): String {
        val nonWatchAccountsCount = accountManager.accounts.count { !it.isWatchAccount }

        return "Wallet ${nonWatchAccountsCount + 1}"
    }

    override fun getNextCexAccountName(cexType: com.monistoWallet.entities.CexType): String {
        val cexAccountsCount = accountManager.accounts.count {
            it.type is com.monistoWallet.entities.AccountType.Cex && cexType.sameType(it.type.cexType) }

        return "${cexType.name()} Wallet ${cexAccountsCount + 1}"
    }
}
