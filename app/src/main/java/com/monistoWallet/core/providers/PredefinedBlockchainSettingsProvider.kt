package com.monistoWallet.core.providers

import com.monistoWallet.core.managers.RestoreSettings
import com.monistoWallet.core.managers.RestoreSettingsManager
import com.monistoWallet.core.managers.ZcashBirthdayProvider
import com.monistoWallet.entities.Account
import com.wallet0x.marketkit.models.BlockchainType

class PredefinedBlockchainSettingsProvider(
    private val manager: RestoreSettingsManager,
    private val zcashBirthdayProvider: ZcashBirthdayProvider
) {

    fun prepareNew(account: com.monistoWallet.entities.Account, blockchainType: BlockchainType) {
        val settings = RestoreSettings()
        when (blockchainType) {
            BlockchainType.Zcash -> {
                settings.birthdayHeight = zcashBirthdayProvider.getLatestCheckpointBlockHeight()
            }
            else -> {}
        }
        if (settings.isNotEmpty()) {
            manager.save(settings, account, blockchainType)
        }
    }
}
