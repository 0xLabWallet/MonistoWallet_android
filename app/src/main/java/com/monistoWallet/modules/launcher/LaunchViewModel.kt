package com.monistoWallet.modules.launcher

import androidx.lifecycle.ViewModel
import com.monistoWallet.core.IAccountManager
import com.monistoWallet.core.ILocalStorage
import com.monistoWallet.core.IKeyStoreManager
import com.monistoWallet.core.IPinComponent
import com.monistoWallet.core.ISystemInfoManager
import com.monistoWallet.core.security.KeyStoreValidationResult

class LaunchViewModel(
    private val accountManager: IAccountManager,
    private val pinComponent: IPinComponent,
    private val systemInfoManager: ISystemInfoManager,
    private val keyStoreManager: IKeyStoreManager,
    localStorage: ILocalStorage
) : ViewModel() {

    private val mainShowedOnce = localStorage.mainShowedOnce

    fun getPage() = when {
        systemInfoManager.isSystemLockOff -> Page.NoSystemLock
        else -> when (keyStoreManager.validateKeyStore()) {
            KeyStoreValidationResult.UserNotAuthenticated -> Page.UserAuthentication
            KeyStoreValidationResult.KeyIsInvalid -> Page.KeyInvalidated
            KeyStoreValidationResult.KeyIsValid -> when {
                accountManager.isAccountsEmpty && !mainShowedOnce -> Page.Welcome
                pinComponent.isLocked -> Page.Unlock
                else -> Page.Main
            }
        }
    }

    enum class Page {
        Welcome,
        Main,
        Unlock,
        NoSystemLock,
        KeyInvalidated,
        UserAuthentication
    }
}
