package com.monistoWallet.modules.intro

import androidx.lifecycle.ViewModel
import com.monistoWallet.R
import com.monistoWallet.core.ILocalStorage

class IntroViewModel(
        private val localStorage: ILocalStorage
): ViewModel() {

    fun onStartClicked() {
        localStorage.mainShowedOnce = true
    }

}
