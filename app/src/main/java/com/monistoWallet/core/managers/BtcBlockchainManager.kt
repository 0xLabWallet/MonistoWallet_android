package com.monistoWallet.core.managers

import com.monistoWallet.core.providers.AppConfigProvider
import com.monistoWallet.core.storage.BlockchainSettingsStorage
import com.monistoWallet.entities.BtcRestoreMode
import com.monistoWallet.entities.TransactionDataSortMode
import com.wallet0x.bitcoincore.BitcoinCore.SyncMode
import com.wallet0x.marketkit.models.BlockchainType
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class BtcBlockchainManager(
    private val storage: BlockchainSettingsStorage,
    private val appConfigProvider: AppConfigProvider,
    marketKit: MarketKitWrapper,
) {

    private val restoreModeUpdatedSubject = PublishSubject.create<BlockchainType>()
    val restoreModeUpdatedObservable: Observable<BlockchainType> = restoreModeUpdatedSubject

    private val transactionSortModeUpdatedSubject = PublishSubject.create<BlockchainType>()
    val transactionSortModeUpdatedObservable: Observable<BlockchainType> =
        transactionSortModeUpdatedSubject

    private val blockchairSyncEnabledBlockchains = listOf(BlockchainType.Bitcoin, BlockchainType.BitcoinCash)

    private val blockchainTypes = listOf(
        BlockchainType.Bitcoin,
        BlockchainType.BitcoinCash,
        BlockchainType.Litecoin,
        BlockchainType.Dash,
        BlockchainType.ECash,
    )

    val allBlockchains = marketKit.blockchains(blockchainTypes.map { it.uid })

    fun blockchain(blockchainType: BlockchainType) =
        allBlockchains.firstOrNull { blockchainType == it.type }

    private fun defaultRestoreMode(blockchainType: BlockchainType) =
        if (blockchainType in blockchairSyncEnabledBlockchains) BtcRestoreMode.Blockchair else BtcRestoreMode.Hybrid

    fun restoreMode(blockchainType: BlockchainType): BtcRestoreMode {
        return storage.btcRestoreMode(blockchainType) ?: defaultRestoreMode(blockchainType)
    }

    fun availableRestoreModes(blockchainType: BlockchainType) =
        BtcRestoreMode.values().let {
            val values = it.toList()
            if (blockchainType !in blockchairSyncEnabledBlockchains) {
                values - BtcRestoreMode.Blockchair
            } else {
                values
            }
        }

    fun syncMode(blockchainType: BlockchainType, accountOrigin: com.monistoWallet.entities.AccountOrigin): SyncMode {
        if (accountOrigin == com.monistoWallet.entities.AccountOrigin.Created) {
            return SyncMode.Blockchair(appConfigProvider.blockchairApiKey)
        }

        return when (restoreMode(blockchainType)) {
            BtcRestoreMode.Blockchair -> SyncMode.Blockchair(appConfigProvider.blockchairApiKey)
            BtcRestoreMode.Hybrid -> SyncMode.Api()
            BtcRestoreMode.Blockchain -> SyncMode.Full()
        }
    }

    fun save(restoreMode: BtcRestoreMode, blockchainType: BlockchainType) {
        storage.save(restoreMode, blockchainType)
        restoreModeUpdatedSubject.onNext(blockchainType)
    }

    fun transactionSortMode(blockchainType: BlockchainType): TransactionDataSortMode {
        return storage.btcTransactionSortMode(blockchainType) ?: TransactionDataSortMode.Shuffle
    }

    fun save(transactionSortMode: TransactionDataSortMode, blockchainType: BlockchainType) {
        storage.save(transactionSortMode, blockchainType)
        transactionSortModeUpdatedSubject.onNext(blockchainType)
    }
}
