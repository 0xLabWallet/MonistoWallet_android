package com.monistoWallet.entities.nft

data class NftAssetBriefMetadata(
    val nftUid: NftUid,
    val providerCollectionUid: String,
    val name: String?,
    val imageUrl: String?,
    val previewImageUrl: String?
)