package com.monistoWallet.modules.pin.set

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App

object PinSetModule {

    class Factory(private val forDuress: Boolean) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PinSetViewModel(com.monistoWallet.core.App.pinComponent, forDuress) as T
        }
    }

    enum class SetStage {
        Enter,
        Confirm
    }

    data class PinSetViewState(
        val stage: SetStage,
        val enteredCount: Int,
        val finished: Boolean,
        val reverseSlideAnimation: Boolean,
        val error: String?,
    )

}