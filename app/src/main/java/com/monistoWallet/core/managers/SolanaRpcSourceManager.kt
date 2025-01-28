package com.monistoWallet.core.managers

import com.monistoWallet.core.storage.BlockchainSettingsStorage
import com.wallet0x.marketkit.models.Blockchain
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.solanakit.models.RpcSource
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SolanaRpcSourceManager(
        private val blockchainSettingsStorage: BlockchainSettingsStorage,
        private val marketKitWrapper: MarketKitWrapper
) {

    private val blockchainType = BlockchainType.Solana
    private val rpcSourceSubjectUpdate = PublishSubject.create<Unit>()

    val rpcSourceUpdateObservable: Observable<Unit>
        get() = rpcSourceSubjectUpdate

    val allRpcSources = listOf(RpcSource.TritonOne, RpcSource.Serum)

    val rpcSource: RpcSource
        get() {
            val rpcSourceName = blockchainSettingsStorage.evmSyncSourceUrl(blockchainType)
            val rpcSource = allRpcSources.firstOrNull { it.name == rpcSourceName }

            return rpcSource ?: allRpcSources[0]
        }

    val blockchain: Blockchain?
        get() = marketKitWrapper.blockchain(blockchainType.uid)

    fun save(rpcSource: RpcSource) {
        blockchainSettingsStorage.save(rpcSource.name, blockchainType)
        rpcSourceSubjectUpdate.onNext(Unit)
    }

}
