package com.monistoWallet.additional_wallet0x.account.pay_for_card.ui.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PayViewModel : ViewModel() {
    var onTimeFinished by mutableStateOf(false)
    private val initialTime = 15 * 60 * 1000L
    private val _timeLeft = MutableStateFlow(initialTime)
    val timeLeft: StateFlow<Long> = _timeLeft

    // Переменная для управления состоянием таймера
    private var countdownJob: Job? = null

    init {
        // Запуск таймера при создании ViewModel
        if (_timeLeft.value > 0) {
            startCountdown()
        }
    }

    fun startCountdown() {
        if (countdownJob != null) return

        countdownJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000L)
                _timeLeft.value -= 1000L
            }
            onCountdownFinished()
        }
    }

    private fun onCountdownFinished() {
        onTimeFinished = true
        stopCountdown() // Очищаем Job при завершении
    }

    fun stopCountdown() {
        countdownJob?.cancel() // Останавливаем таймер
        countdownJob = null
    }

    fun resetTimer() {
        // Обнуляем данные для нового таймера
        _timeLeft.value = initialTime
        onTimeFinished = false
    }
}