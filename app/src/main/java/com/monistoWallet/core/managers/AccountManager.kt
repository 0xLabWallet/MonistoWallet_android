package com.monistoWallet.core.managers

import com.monistoWallet.core.IAccountCleaner
import com.monistoWallet.core.IAccountManager
import com.monistoWallet.core.IAccountsStorage
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit

class AccountManager(
    private val storage: IAccountsStorage,
    private val accountCleaner: IAccountCleaner
) : IAccountManager {

    private var accountsCache = mutableMapOf<String, com.monistoWallet.entities.Account>()
    private val accountsSubject = PublishSubject.create<List<com.monistoWallet.entities.Account>>()
    private val accountsDeletedSubject = PublishSubject.create<Unit>()
    private val _activeAccountStateFlow = MutableStateFlow<ActiveAccountState>(ActiveAccountState.NotLoaded)
    private var currentLevel = Int.MAX_VALUE

    override val activeAccountStateFlow = _activeAccountStateFlow

    override val hasNonStandardAccount: Boolean
        get() = accountsCache.any { it.value.nonStandard }

    override var activeAccount: com.monistoWallet.entities.Account? = null

    override val isAccountsEmpty: Boolean
        get() = storage.isAccountsEmpty

    override val accounts: List<com.monistoWallet.entities.Account>
        get() = accountsCache.map { it.value }

    override val accountsFlowable: Flowable<List<com.monistoWallet.entities.Account>>
        get() = accountsSubject.toFlowable(BackpressureStrategy.BUFFER)

    override val accountsDeletedFlowable: Flowable<Unit>
        get() = accountsDeletedSubject.toFlowable(BackpressureStrategy.BUFFER)

    private val _newAccountBackupRequiredFlow = MutableStateFlow<com.monistoWallet.entities.Account?>(null)
    override val newAccountBackupRequiredFlow = _newAccountBackupRequiredFlow.asStateFlow()

    private fun updateCache(account: com.monistoWallet.entities.Account) {
        accountsCache[account.id] = account
    }

    override fun setActiveAccountId(activeAccountId: String?) {
        if (activeAccount?.id != activeAccountId) {
            storage.setActiveAccountId(currentLevel, activeAccountId)
            activeAccount = activeAccountId?.let { account(it) }
            _activeAccountStateFlow.update {
                ActiveAccountState.ActiveAccount(activeAccount)
            }
        }
    }

    override fun account(id: String): com.monistoWallet.entities.Account? {
        return accounts.find { account -> account.id == id }
    }

    override fun onHandledBackupRequiredNewAccount() {
        _newAccountBackupRequiredFlow.update { null }
    }

    override fun save(account: com.monistoWallet.entities.Account) {
        storage.save(account)

        updateCache(account)
        accountsSubject.onNext(accounts)

        setActiveAccountId(account.id)
        if (!account.isBackedUp && !account.isFileBackedUp) {
            _newAccountBackupRequiredFlow.update {
                account
            }
        }
    }

    override fun import(accounts: List<com.monistoWallet.entities.Account>) {
        for (account in accounts) {
            storage.save(account)
            updateCache(account)
        }

        accountsSubject.onNext(accounts)

        if (activeAccount == null) {
            accounts.minByOrNull { it.name.lowercase() }?.let { account ->
                setActiveAccountId(account.id)
                if (!account.isBackedUp && !account.isFileBackedUp) {
                    _newAccountBackupRequiredFlow.update {
                        account
                    }
                }
            }
        }
    }

    override fun updateAccountLevels(accountIds: List<String>, level: Int) {
        storage.updateLevels(accountIds, level)
    }

    override fun updateMaxLevel(level: Int) {
        storage.updateMaxLevel(level)
    }

    override fun update(account: com.monistoWallet.entities.Account) {
        storage.update(account)

        updateCache(account)
        accountsSubject.onNext(accounts)

        activeAccount?.id?.let {
            if (account.id == it) {
                activeAccount = account
                _activeAccountStateFlow.update { ActiveAccountState.ActiveAccount(activeAccount) }
            }
        }
    }

    override fun delete(id: String) {
        accountsCache.remove(id)
        storage.delete(id)

        accountsSubject.onNext(accounts)
        accountsDeletedSubject.onNext(Unit)

        if (id == activeAccount?.id) {
            setActiveAccountId(accounts.firstOrNull()?.id)
        }
    }

    override fun clear() {
        storage.clear()
        accountsCache.clear()
        accountsSubject.onNext(listOf())
        accountsDeletedSubject.onNext(Unit)
        setActiveAccountId(null)
    }

    override fun setLevel(level: Int) {
        currentLevel = level

        accountsCache = storage.allAccounts(level).associateBy { it.id }.toMutableMap()

        val activeAccountIdForLevel = storage.getActiveAccountId(level)
        if (activeAccount == null || activeAccount?.id != activeAccountIdForLevel) {
            activeAccount = accountsCache[activeAccountIdForLevel] ?: accounts.firstOrNull()
            _activeAccountStateFlow.update {
                ActiveAccountState.ActiveAccount(activeAccount)
            }
        }

        accountsSubject.onNext(accounts)
    }

    override fun clearAccounts() {
        val clearAsync = Single.fromCallable {
            accountCleaner.clearAccounts(storage.getDeletedAccountIds())
            storage.clearDeleted()
        }

        Single.timer(3, TimeUnit.SECONDS)
            .flatMap { clearAsync }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

}

class NoActiveAccount : Exception()

sealed class ActiveAccountState() {
    class ActiveAccount(val account: com.monistoWallet.entities.Account?) : ActiveAccountState()
    object NotLoaded : ActiveAccountState()
}