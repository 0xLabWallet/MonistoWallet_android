package com.monistoWallet.modules.pin.unlock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.core.ILocalStorage
import com.monistoWallet.modules.pin.PinModule
import com.monistoWallet.modules.pin.core.ILockoutManager
import com.monistoWallet.modules.pin.core.LockoutState
import com.monistoWallet.modules.pin.core.OneTimeTimer
import com.monistoWallet.modules.pin.core.OneTimerDelegate
import com.monistoWallet.modules.pin.unlock.PinUnlockModule.PinUnlockViewState
import com.monistoWallet.core.IPinComponent
import com.monistoWallet.core.ISystemInfoManager
import com.monistoWallet.core.helpers.DateHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PinUnlockViewModel(
    private val pinComponent: IPinComponent,
    private val lockoutManager: ILockoutManager,
    systemInfoManager: ISystemInfoManager,
    private val timer: OneTimeTimer,
    private val localStorage: ILocalStorage,
) : ViewModel(), OneTimerDelegate {

    private var attemptsLeft: Int? = null

    var pinRandomized by mutableStateOf(localStorage.pinRandomized)
        private set

    var uiState by mutableStateOf(
        PinUnlockViewState(
            enteredCount = 0,
            fingerScannerEnabled = systemInfoManager.biometricAuthSupported && pinComponent.isBiometricAuthEnabled,
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

    fun onBiometricsUnlock() {
        pinComponent.onBiometricUnlock()
        lockoutManager.dropFailedAttempts()
        uiState = uiState.copy(unlocked = true)
    }

    fun onKeyClick(number: Int) {
        if (enteredPin.length < PinModule.PIN_COUNT) {

            enteredPin += number.toString()
            uiState = uiState.copy(
                enteredCount = enteredPin.length
            )

            if (enteredPin.length == PinModule.PIN_COUNT) {
                if (unlock(enteredPin)) {
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

    private fun unlock(pin: String): Boolean {
        if (pinComponent.unlock(pin)) {
            lockoutManager.dropFailedAttempts()
            return true
        } else {
            lockoutManager.didFailUnlock()
            updateLockoutState()
            return false
        }
    }

}
