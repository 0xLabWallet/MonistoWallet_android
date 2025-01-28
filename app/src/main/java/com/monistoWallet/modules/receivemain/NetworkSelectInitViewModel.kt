package com.monistoWallet.modules.receivemain

import androidx.lifecycle.ViewModel
import com.monistoWallet.core.App

class NetworkSelectInitViewModel(coinUid: String) : ViewModel() {
    val fullCoin = com.monistoWallet.core.App.marketKit.fullCoins(listOf(coinUid)).firstOrNull()
    val activeAccount = com.monistoWallet.core.App.accountManager.activeAccount
}
