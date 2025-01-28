package com.monistoWallet.core.managers

import android.app.Activity
import com.monistoWallet.modules.keystore.KeyStoreActivity
import com.monistoWallet.modules.lockscreen.LockScreenActivity
import com.monistoWallet.core.BackgroundManager
import com.monistoWallet.core.IKeyStoreManager
import com.monistoWallet.core.IPinComponent
import com.monistoWallet.core.ISystemInfoManager
import com.monistoWallet.core.security.KeyStoreValidationResult

class BackgroundStateChangeListener(
    private val systemInfoManager: ISystemInfoManager,
    private val keyStoreManager: IKeyStoreManager,
    private val pinComponent: IPinComponent
) : BackgroundManager.Listener {

    override fun willEnterForeground(activity: Activity) {
        if (systemInfoManager.isSystemLockOff) {
            KeyStoreActivity.startForNoSystemLock(activity)
            return
        }

        when (keyStoreManager.validateKeyStore()) {
            KeyStoreValidationResult.UserNotAuthenticated -> {
                KeyStoreActivity.startForUserAuthentication(activity)
                return
            }
            KeyStoreValidationResult.KeyIsInvalid -> {
                KeyStoreActivity.startForInvalidKey(activity)
                return
            }
            KeyStoreValidationResult.KeyIsValid -> { /* Do nothing */}
        }

        pinComponent.willEnterForeground(activity)

        if (pinComponent.shouldShowPin(activity)) {
            LockScreenActivity.start(activity)
        }
    }

    override fun didEnterBackground() {
        pinComponent.didEnterBackground()
    }

    override fun onAllActivitiesDestroyed() {
        pinComponent.lock()
    }

}
