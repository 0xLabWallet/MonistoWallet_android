package com.monistoWallet.core.providers.nft

import com.monistoWallet.entities.nft.*
import com.monistoWallet.entities.nft.NftEventMetadata.EventType
import com.wallet0x.marketkit.models.BlockchainType

interface INftProvider {
    val title: String
    val icon: Int
    suspend fun addressMetadata(blockchainType: BlockchainType, address: String): NftAddressMetadata
    suspend fun extendedAssetMetadata(nftUid: NftUid, providerCollectionUid: String): Pair<NftAssetMetadata, NftCollectionMetadata>
    suspend fun collectionMetadata(blockchainType: BlockchainType, providerUid: String): NftCollectionMetadata
    suspend fun collectionAssetsMetadata(
        blockchainType: BlockchainType,
        providerUid: String,
        paginationData: PaginationData?
    ): Pair<List<NftAssetMetadata>, PaginationData?>

    suspend fun collectionEventsMetadata(
        blockchainType: BlockchainType,
        providerUid: String,
        eventType: EventType?,
        paginationData: PaginationData?
    ): Pair<List<NftEventMetadata>, PaginationData?>

    suspend fun assetEventsMetadata(
        nftUid: NftUid,
        eventType: EventType?,
        paginationData: PaginationData?
    ): Pair<List<NftEventMetadata>, PaginationData?>

    suspend fun assetsBriefMetadata(blockchainType: BlockchainType, nftUids: List<NftUid>): List<NftAssetBriefMetadata>
}

sealed class PaginationData {
    class Cursor(val value: String) : PaginationData()
    class Page(val value: Int) : PaginationData()

    val cursor: String?
        get() = when (this) {
            is Cursor -> value
            is Page -> null
        }

    val page: Int?
        get() = when (this) {
            is Cursor -> null
            is Page -> value
        }
}
