package com.monistoWallet.core.managers

import com.monistoWallet.core.IAccountManager
import com.monistoWallet.core.IBackupManager
import io.reactivex.Flowable

class BackupManager(private val accountManager: IAccountManager) : IBackupManager {

    override val allBackedUp: Boolean
        get() = accountManager.accounts.all { it.isBackedUp }

    override val allBackedUpFlowable: Flowable<Boolean>
        get() = accountManager.accountsFlowable.map { accounts ->
            accounts.all { it.isBackedUp }
        }
}
