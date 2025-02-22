package com.monistoWallet.modules.pin.unlock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.monistoWallet.core.App
import com.monistoWallet.core.ILocalStorage
import com.monistoWallet.modules.pin.PinModule
import com.monistoWallet.modules.pin.core.ILockoutManager
import com.monistoWallet.modules.pin.core.LockoutManager
import com.monistoWallet.modules.pin.core.LockoutState
import com.monistoWallet.modules.pin.core.LockoutUntilDateFactory
import com.monistoWallet.modules.pin.core.OneTimeTimer
import com.monistoWallet.modules.pin.core.OneTimerDelegate
import com.monistoWallet.modules.pin.core.UptimeProvider
import com.monistoWallet.core.CoreApp
import com.monistoWallet.core.CurrentDateProvider
import com.monistoWallet.core.IPinComponent
import com.monistoWallet.core.helpers.DateHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PinConfirmViewModel(
    private val pinComponent: IPinComponent,
    private val lockoutManager: ILockoutManager,
    private val timer: OneTimeTimer,
    private val localStorage: ILocalStorage,
) : ViewModel(), OneTimerDelegate {

    private var attemptsLeft: Int? = null

    var pinRandomized by mutableStateOf(localStorage.pinRandomized)
        private set

    var uiState by mutableStateOf(
        PinConfirmViewState(
            enteredCount = 0,
            unlocked = false,
            showShakeAnimation = false,
            inputState = PinUnlockModule.InputState.Enabled(attemptsLeft)
        )
    )
        private set

    private var enteredPin = ""

    init {
        timer.delegate = this
        updateLockoutState()
    }

    override fun onFire() {
        updateLockoutState()
    }

    fun updatePinRandomized(random: Boolean) {
        localStorage.pinRandomized = random
        pinRandomized = random
    }

    fun onKeyClick(number: Int) {
        if (enteredPin.length < PinModule.PIN_COUNT) {

            enteredPin += number.toString()
            uiState = uiState.copy(
                enteredCount = enteredPin.length
            )

            if (enteredPin.length == PinModule.PIN_COUNT) {
                if (confirm(enteredPin)) {
                    uiState = uiState.copy(unlocked = true)
                } else {
                    uiState = uiState.copy(
                        showShakeAnimation = true
                    )
                    viewModelScope.launch {
                        delay(500)
                        enteredPin = ""
                        uiState = uiState.copy(
                            enteredCount = enteredPin.length,
                            showShakeAnimation = false
                        )
                    }
                }
            }
        }
    }

    fun onDelete() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            uiState = uiState.copy(
                enteredCount = enteredPin.length,
                showShakeAnimation = false
            )
        }
    }

    fun unlocked() {
        uiState = uiState.copy(unlocked = false)
    }

    fun onShakeAnimationFinish() {
        uiState = uiState.copy(showShakeAnimation = false)
    }

    private fun updateLockoutState() {
        uiState = when (val state = lockoutManager.currentState) {
            is LockoutState.Unlocked -> {
                attemptsLeft = state.attemptsLeft
                uiState.copy(inputState = PinUnlockModule.InputState.Enabled(attemptsLeft))
            }

            is LockoutState.Locked -> {
                timer.schedule(state.until)
                uiState.copy(
                    inputState = PinUnlockModule.InputState.Locked(
                        until = DateHelper.getOnlyTime(state.until)
                    )
                )
            }
        }
    }

    private fun confirm(pin: String): Boolean {
        val valid = pinComponent.validateCurrentLevel(pin)
        if (valid) {
            lockoutManager.dropFailedAttempts()
        } else {
            lockoutManager.didFailUnlock()
            updateLockoutState()
        }

        return valid
    }

    class Factory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val lockoutManager = LockoutManager(
                CoreApp.lockoutStorage, UptimeProvider(), LockoutUntilDateFactory(
                    CurrentDateProvider()
                )
            )
            return PinConfirmViewModel(
                com.monistoWallet.core.App.pinComponent,
                lockoutManager,
                OneTimeTimer(),
                com.monistoWallet.core.App.localStorage
            ) as T
        }
    }
}

data class PinConfirmViewState(
    val enteredCount: Int,
    val unlocked: Boolean,
    val showShakeAnimation: Boolean,
    val inputState: PinUnlockModule.InputState
)
