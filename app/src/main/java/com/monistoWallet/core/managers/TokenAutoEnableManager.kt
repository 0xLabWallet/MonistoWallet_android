package com.monistoWallet.core.managers

import com.monistoWallet.core.storage.TokenAutoEnabledBlockchainDao
import com.monistoWallet.entities.TokenAutoEnabledBlockchain
import com.wallet0x.marketkit.models.BlockchainType

class TokenAutoEnableManager(
    private val tokenAutoEnabledBlockchainDao: TokenAutoEnabledBlockchainDao
) {
    fun markAutoEnable(account: com.monistoWallet.entities.Account, blockchainType: BlockchainType) {
        tokenAutoEnabledBlockchainDao.insert(TokenAutoEnabledBlockchain(account.id, blockchainType))
    }

    fun isAutoEnabled(account: com.monistoWallet.entities.Account, blockchainType: BlockchainType): Boolean {
        return tokenAutoEnabledBlockchainDao.get(account.id, blockchainType) != null
    }
}
