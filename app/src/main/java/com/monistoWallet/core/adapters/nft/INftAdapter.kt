package com.monistoWallet.core.adapters.nft

import com.monistoWallet.entities.nft.NftRecord
import com.monistoWallet.entities.nft.NftUid
import com.wallet0x.ethereumkit.models.Address
import com.wallet0x.ethereumkit.models.TransactionData
import kotlinx.coroutines.flow.Flow
import java.math.BigInteger


interface INftAdapter {
    val userAddress: String
    val nftRecordsFlow: Flow<List<NftRecord>>
    val nftRecords: List<NftRecord>
    fun nftRecord(nftUid: NftUid): NftRecord?
    fun sync()
    fun transferEip721TransactionData(
        contractAddress: String,
        to: Address,
        tokenId: String
    ): TransactionData?

    fun transferEip1155TransactionData(
        contractAddress: String,
        to: Address,
        tokenId: String,
        value: BigInteger
    ): TransactionData?
}