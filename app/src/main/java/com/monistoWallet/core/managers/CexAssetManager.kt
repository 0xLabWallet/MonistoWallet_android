package com.monistoWallet.core.managers

import com.monistoWallet.core.providers.CexAsset
import com.monistoWallet.core.providers.CexAssetRaw
import com.monistoWallet.core.storage.CexAssetsDao
import com.monistoWallet.entities.Account
import java.math.BigDecimal

class CexAssetManager(marketKit: MarketKitWrapper, private val cexAssetsDao: CexAssetsDao) {

    private val coins = marketKit.allCoins().map { it.uid to it }.toMap()
    private val allBlockchains = marketKit.allBlockchains().map { it.uid to it }.toMap()

    private fun buildCexAsset(cexAssetRaw: CexAssetRaw): CexAsset {
        return CexAsset(
            id = cexAssetRaw.id,
            name = cexAssetRaw.name,
            freeBalance = cexAssetRaw.freeBalance,
            lockedBalance = cexAssetRaw.lockedBalance,
            depositEnabled = cexAssetRaw.depositEnabled,
            withdrawEnabled = cexAssetRaw.withdrawEnabled,
            depositNetworks = cexAssetRaw.depositNetworks.map { it.cexDepositNetwork(allBlockchains[it.blockchainUid]) },
            withdrawNetworks = cexAssetRaw.withdrawNetworks.map { it.cexWithdrawNetwork(allBlockchains[it.blockchainUid]) },
            coin = coins[cexAssetRaw.coinUid],
            decimals = cexAssetRaw.decimals
        )
    }

    fun saveAllForAccount(cexAssetRaws: List<CexAssetRaw>, account: com.monistoWallet.entities.Account) {
        cexAssetsDao.delete(account.id)
        cexAssetsDao.insert(cexAssetRaws)
    }

    fun get(account: com.monistoWallet.entities.Account, assetId: String): CexAsset? {
        return cexAssetsDao.get(account.id, assetId)
            ?.let { buildCexAsset(it) }
    }

    fun getAllForAccount(account: com.monistoWallet.entities.Account): List<CexAsset> {
        return cexAssetsDao.getAllForAccount(account.id)
            .map {
                buildCexAsset(it)
            }
    }

    fun getWithBalance(account: com.monistoWallet.entities.Account): List<CexAsset> {
        return cexAssetsDao.getAllForAccount(account.id)
            .filter { it.freeBalance > BigDecimal.ZERO }
            .map {
                buildCexAsset(it)
            }
    }

}