package com.monistoWallet.modules.managewallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.enablecoin.restoresettings.RestoreSettingsService
import com.monistoWallet.modules.enablecoin.restoresettings.RestoreSettingsViewModel
import com.monistoWallet.modules.receivemain.FullCoinsProvider

object ManageWalletsModule {

    class Factory : ViewModelProvider.Factory {

        private val restoreSettingsService by lazy {
            RestoreSettingsService(com.monistoWallet.core.App.restoreSettingsManager, com.monistoWallet.core.App.zcashBirthdayProvider)
        }

        private val manageWalletsService by lazy {
            val activeAccount = com.monistoWallet.core.App.accountManager.activeAccount
            ManageWalletsService(
                com.monistoWallet.core.App.walletManager,
                restoreSettingsService,
                com.monistoWallet.core.App.accountManager.activeAccount?.let { account ->
                    FullCoinsProvider(App.marketKit, account)
                },
                activeAccount
            )
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                RestoreSettingsViewModel::class.java -> {
                    RestoreSettingsViewModel(restoreSettingsService, listOf(restoreSettingsService)) as T
                }
                ManageWalletsViewModel::class.java -> {
                    ManageWalletsViewModel(manageWalletsService, listOf(manageWalletsService)) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }
}
