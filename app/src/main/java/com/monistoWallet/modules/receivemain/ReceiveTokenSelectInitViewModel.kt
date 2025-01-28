package com.monistoWallet.modules.receivemain

import androidx.lifecycle.ViewModel
import com.monistoWallet.core.App
import com.monistoWallet.entities.Account

class ReceiveTokenSelectInitViewModel : ViewModel() {
    fun getActiveAccount(): Account? {
        return App.accountManager.activeAccount
    }
}
