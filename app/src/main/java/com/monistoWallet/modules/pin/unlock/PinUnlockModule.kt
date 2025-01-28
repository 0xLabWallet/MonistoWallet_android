package com.monistoWallet.modules.pin.unlock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.pin.core.LockoutManager
import com.monistoWallet.modules.pin.core.LockoutUntilDateFactory
import com.monistoWallet.modules.pin.core.OneTimeTimer
import com.monistoWallet.modules.pin.core.UptimeProvider
import com.monistoWallet.core.CoreApp
import com.monistoWallet.core.CurrentDateProvider

object PinUnlockModule {

    class Factory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val lockoutManager = LockoutManager(
                CoreApp.lockoutStorage, UptimeProvider(), LockoutUntilDateFactory(
                    CurrentDateProvider()
                )
            )
            return PinUnlockViewModel(
                com.monistoWallet.core.App.pinComponent,
                lockoutManager,
                com.monistoWallet.core.App.systemInfoManager,
                OneTimeTimer(),
                com.monistoWallet.core.App.localStorage,
            ) as T
        }
    }

    data class PinUnlockViewState(
        val enteredCount: Int,
        val fingerScannerEnabled: Boolean,
        val unlocked: Boolean,
        val showShakeAnimation: Boolean,
        val inputState: InputState
    )

    sealed class InputState {
        class Enabled(val attemptsLeft: Int? = null) : InputState()
        class Locked(val until: String) : InputState()
    }

}