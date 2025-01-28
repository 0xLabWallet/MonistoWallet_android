package com.monistoWallet.entities.nft

import com.wallet0x.marketkit.models.BlockchainType

abstract class NftRecord(
    val blockchainType: BlockchainType,
    val balance: Int
) {
    abstract val nftUid: NftUid
    abstract val displayName: String
}