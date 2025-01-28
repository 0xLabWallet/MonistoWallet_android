package com.monistoWallet.modules.restoreaccount.restoreblockchains

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.entities.AccountType
import com.monistoWallet.modules.enablecoin.blockchaintokens.BlockchainTokensService
import com.monistoWallet.modules.enablecoin.blockchaintokens.BlockchainTokensViewModel
import com.monistoWallet.modules.enablecoin.restoresettings.RestoreSettingsService
import com.monistoWallet.modules.enablecoin.restoresettings.RestoreSettingsViewModel
import com.monistoWallet.modules.market.ImageSource

object RestoreBlockchainsModule {

    class Factory(
        private val accountName: String,
        private val accountType: AccountType,
        private val manualBackup: Boolean,
        private val fileBackup: Boolean
    ) : ViewModelProvider.Factory {

        private val restoreSettingsService by lazy {
            RestoreSettingsService(App.restoreSettingsManager, App.zcashBirthdayProvider)
        }
        private val blockchainTokensService by lazy {
            BlockchainTokensService()
        }

        private val restoreSelectCoinsService by lazy {
            RestoreBlockchainsService(
                accountName,
                accountType,
                manualBackup,
                fileBackup,
                App.accountFactory,
                App.accountManager,
                App.walletActivator,
                App.walletManager,
                App.marketKit,
                App.tokenAutoEnableManager,
                blockchainTokensService,
                restoreSettingsService
            )
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                RestoreSettingsViewModel::class.java -> {
                    RestoreSettingsViewModel(
                        restoreSettingsService,
                        listOf(restoreSettingsService)
                    ) as T
                }
                RestoreBlockchainsViewModel::class.java -> {
                    RestoreBlockchainsViewModel(
                        restoreSelectCoinsService,
                        listOf(restoreSelectCoinsService)
                    ) as T
                }
                BlockchainTokensViewModel::class.java -> {
                    BlockchainTokensViewModel(blockchainTokensService) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }
}

data class CoinViewItem<T>(
    val item: T,
    val imageSource: ImageSource,
    val title: String,
    val subtitle: String,
    val enabled: Boolean,
    val hasSettings: Boolean = false,
    val hasInfo: Boolean = false,
    val label: String? = null,
)
