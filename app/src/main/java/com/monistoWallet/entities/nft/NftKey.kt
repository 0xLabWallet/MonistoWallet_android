package com.monistoWallet.entities.nft

import com.monistoWallet.entities.Account
import com.wallet0x.marketkit.models.BlockchainType

data class NftKey(
    val account: Account,
    val blockchainType: BlockchainType
)