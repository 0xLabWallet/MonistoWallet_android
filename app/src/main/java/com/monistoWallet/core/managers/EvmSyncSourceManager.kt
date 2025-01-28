package com.monistoWallet.core.managers

import android.net.Uri
import com.monistoWallet.core.providers.AppConfigProvider
import com.monistoWallet.core.storage.BlockchainSettingsStorage
import com.monistoWallet.core.storage.EvmSyncSourceStorage
import com.monistoWallet.entities.EvmSyncSource
import com.monistoWallet.entities.EvmSyncSourceRecord
import com.wallet0x.ethereumkit.models.RpcSource
import com.wallet0x.ethereumkit.models.RpcSource.Companion.dexnetRpcHttp
import com.wallet0x.ethereumkit.models.TransactionSource
import com.wallet0x.marketkit.models.BlockchainType
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.net.URI

class EvmSyncSourceManager(
    private val appConfigProvider: AppConfigProvider,
    private val blockchainSettingsStorage: BlockchainSettingsStorage,
    private val evmSyncSourceStorage: EvmSyncSourceStorage,
) {

    private val syncSourceSubject = PublishSubject.create<BlockchainType>()

    private val _syncSourcesUpdatedFlow =
        MutableSharedFlow<BlockchainType>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val syncSourcesUpdatedFlow = _syncSourcesUpdatedFlow.asSharedFlow()

    private fun defaultTransactionSource(blockchainType: BlockchainType): TransactionSource {
        return when (blockchainType) {
            BlockchainType.Ethereum -> TransactionSource.ethereumEtherscan(appConfigProvider.etherscanApiKey)
            BlockchainType.BinanceSmartChain -> TransactionSource.bscscan(appConfigProvider.bscscanApiKey)
            BlockchainType.Dexnet -> TransactionSource.dexnet("")
            BlockchainType.Polygon -> TransactionSource.polygonscan(appConfigProvider.polygonscanApiKey)
            BlockchainType.Avalanche -> TransactionSource.snowtrace(appConfigProvider.snowtraceApiKey)
            BlockchainType.Optimism -> TransactionSource.optimisticEtherscan(appConfigProvider.optimisticEtherscanApiKey)
            BlockchainType.ArbitrumOne -> TransactionSource.arbiscan(appConfigProvider.arbiscanApiKey)
            BlockchainType.Gnosis -> TransactionSource.gnosis(appConfigProvider.gnosisscanApiKey)
            BlockchainType.Fantom -> TransactionSource.fantom(appConfigProvider.ftmscanApiKey)
            else -> throw Exception("Non-supported EVM blockchain")
        }
    }

    val syncSourceObservable: Observable<BlockchainType>
        get() = syncSourceSubject

    fun defaultSyncSources(blockchainType: BlockchainType): List<EvmSyncSource> {
        return when (val type = blockchainType) {
            BlockchainType.Ethereum -> listOf(
                evmSyncSource(
                    type,
                    "BlocksDecoded",
                    RpcSource.Http(listOf(URI("${appConfigProvider.marketApiBaseUrl}/v1/ethereum-rpc/mainnet")), appConfigProvider.marketApiKey),
                    defaultTransactionSource(type)
                ),
                evmSyncSource(
                    type,
                    "LlamaNodes",
                    RpcSource.Http(listOf(URI("https://eth.llamarpc.com")), null),
                    defaultTransactionSource(type)
                )
            )
            BlockchainType.Dexnet -> listOf(
                evmSyncSource(type, "Dexnet Node", dexnetRpcHttp(), defaultTransactionSource(type))
            )
            BlockchainType.BinanceSmartChain -> listOf(
                evmSyncSource(
                    type,
                    "Binance",
                    RpcSource.binanceSmartChainHttp(),
                    defaultTransactionSource(type)
                ),
                evmSyncSource(
                    type,
                    "BSC RPC",
                    RpcSource.bscRpcHttp(),
                    defaultTransactionSource(type)
                ),
                evmSyncSource(
                    type,
                    "Omnia",
                    RpcSource.Http(listOf(URI("https://endpoints.omniatech.io/v1/bsc/mainnet/public")), null),
                    defaultTransactionSource(type)
                )
            )
            BlockchainType.Polygon -> listOf(
                evmSyncSource(
                    type,
                    "Polygon RPC",
                    RpcSource.polygonRpcHttp(),
                    defaultTransactionSource(type)
                ),
                evmSyncSource(
                    type,
                    "LlamaNodes",
                    RpcSource.Http(listOf(URI("https://polygon.llamarpc.com")), null),
                    defaultTransactionSource(type)
                )
            )
            BlockchainType.Avalanche -> listOf(
                evmSyncSource(
                    type,
                    "Avax Network",
                    RpcSource.avaxNetworkHttp(),
                    defaultTransactionSource(type)
                ),
                evmSyncSource(
                    type,
                    "PublicNode",
                    RpcSource.Http(listOf(URI("https://avalanche-evm.publicnode.com")), null),
                    defaultTransactionSource(type)
                )
            )
            BlockchainType.Optimism -> listOf(
                evmSyncSource(
                    type,
                    "Optimism",
                    RpcSource.optimismRpcHttp(),
                    defaultTransactionSource(type)
                ),
                evmSyncSource(
                    type,
                    "Omnia",
                    RpcSource.Http(
                        listOf(URI("https://endpoints.omniatech.io/v1/op/mainnet/public")),
                        null
                    ),
                    defaultTransactionSource(type)
                )
            )
            BlockchainType.ArbitrumOne -> listOf(
                evmSyncSource(
                    type,
                    "Arbitrum",
                    RpcSource.arbitrumOneRpcHttp(),
                    defaultTransactionSource(type)
                ),
                evmSyncSource(
                    type,
                    "Omnia",
                    RpcSource.Http(listOf(URI("https://endpoints.omniatech.io/v1/arbitrum/one/public")), null),
                    defaultTransactionSource(type)
                )
            )
            BlockchainType.Gnosis -> listOf(
                evmSyncSource(
                    type,
                    "Gnosis Chain",
                    RpcSource.gnosisRpcHttp(),
                    defaultTransactionSource(type)
                ),
                evmSyncSource(
                    type,
                    "Ankr",
                    RpcSource.Http(listOf(URI("https://rpc.ankr.com/gnosis")), null),
                    defaultTransactionSource(type)
                )
            )
            BlockchainType.Fantom -> listOf(
                evmSyncSource(
                    type,
                    "Fantom Chain",
                    RpcSource.fantomRpcHttp(),
                    defaultTransactionSource(type)
                ),
                evmSyncSource(
                    type,
                    "Ankr",
                    RpcSource.Http(listOf(URI("https://rpc.ankr.com/fantom")), null),
                    defaultTransactionSource(type)
                )
            )
            else -> listOf()
        }
    }

    fun customSyncSources(blockchainType: BlockchainType): List<EvmSyncSource> {
        val records = evmSyncSourceStorage.evmSyncSources(blockchainType)
        return try {
            records.mapNotNull { record ->
                val uri = Uri.parse(record.url)
                val rpcSource = when (uri.scheme) {
                    "http",
                    "https" -> RpcSource.Http(listOf(URI(record.url)), record.auth)
                    "ws",
                    "wss" -> RpcSource.WebSocket(URI(record.url), record.auth)
                    else -> return@mapNotNull null
                }
                EvmSyncSource(
                    id = blockchainType.uid + "|" + record.url,
                    name = uri.host ?: "",
                    rpcSource = rpcSource,
                    transactionSource = defaultTransactionSource(blockchainType)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun evmSyncSource(
        blockchainType: BlockchainType,
        name: String,
        rpcSource: RpcSource,
        transactionSource: TransactionSource
    ) =
        EvmSyncSource(
            id = "${blockchainType.uid}|${name}|${transactionSource.name}|${
                rpcSource.uris.joinToString(separator = ",") { it.toString() }
            }",
            name = name,
            rpcSource = rpcSource,
            transactionSource = transactionSource
        )

    fun allSyncSources(blockchainType: BlockchainType): List<EvmSyncSource> =
        defaultSyncSources(blockchainType) + customSyncSources(blockchainType)

    fun getSyncSource(blockchainType: BlockchainType): EvmSyncSource {
        val syncSources = allSyncSources(blockchainType)

        val syncSourceUrl = blockchainSettingsStorage.evmSyncSourceUrl(blockchainType)
        val syncSource = syncSources.firstOrNull { it.uri.toString() == syncSourceUrl }

        return syncSource ?: syncSources[0]
    }

    fun getHttpSyncSource(blockchainType: BlockchainType): EvmSyncSource? {
        val syncSources = allSyncSources(blockchainType)
        blockchainSettingsStorage.evmSyncSourceUrl(blockchainType)?.let { url ->
            syncSources.firstOrNull { it.uri.toString() == url && it.isHttp }?.let { syncSource ->
                return syncSource
            }
        }

        return syncSources.firstOrNull { it.isHttp }
    }

    fun save(syncSource: EvmSyncSource, blockchainType: BlockchainType) {
        blockchainSettingsStorage.save(syncSource.uri.toString(), blockchainType)
        syncSourceSubject.onNext(blockchainType)
    }

    fun saveSyncSource(blockchainType: BlockchainType, url: String, auth: String?) {
        val record = EvmSyncSourceRecord(
            blockchainTypeUid = blockchainType.uid,
            url = url,
            auth = auth
        )

        evmSyncSourceStorage.save(record)

        customSyncSources(blockchainType).firstOrNull { it.uri.toString() == url }?.let {
            save(it, blockchainType)
        }

        _syncSourcesUpdatedFlow.tryEmit(blockchainType)
    }

    fun delete(syncSource: EvmSyncSource, blockchainType: BlockchainType) {
        val isCurrent = getSyncSource(blockchainType) == syncSource

        evmSyncSourceStorage.delete(blockchainType.uid, syncSource.uri.toString())

        if (isCurrent) {
            syncSourceSubject.onNext(blockchainType)
        }

        _syncSourcesUpdatedFlow.tryEmit(blockchainType)
    }

}
