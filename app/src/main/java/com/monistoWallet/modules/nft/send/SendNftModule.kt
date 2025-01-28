package com.monistoWallet.modules.nft.send

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.adapters.nft.INftAdapter
import com.monistoWallet.core.managers.EvmKitWrapper
import com.monistoWallet.core.managers.NftMetadataManager
import com.monistoWallet.core.utils.AddressUriParser
import com.monistoWallet.entities.DataState
import com.monistoWallet.entities.nft.EvmNftRecord
import com.monistoWallet.entities.nft.NftUid
import com.monistoWallet.modules.address.AddressParserViewModel
import com.monistoWallet.modules.send.evm.SendEvmAddressService
import com.monistoWallet.modules.send.evm.confirmation.EvmKitWrapperHoldingViewModel

object SendNftModule {

    @Suppress("UNCHECKED_CAST")
    class Factory(
        val evmNftRecord: EvmNftRecord,
        val nftUid: NftUid,
        val nftBalance: Int,
        private val adapter: INftAdapter,
        private val sendEvmAddressService: SendEvmAddressService,
        private val nftMetadataManager: NftMetadataManager,
        private val evmKitWrapper: EvmKitWrapper
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendEip721ViewModel::class.java -> {
                    SendEip721ViewModel(
                        nftUid,
                        adapter,
                        sendEvmAddressService,
                        nftMetadataManager
                    ) as T
                }
                SendEip1155ViewModel::class.java -> {
                    SendEip1155ViewModel(
                        nftUid,
                        nftBalance,
                        adapter,
                        sendEvmAddressService,
                        nftMetadataManager
                    ) as T
                }
                EvmKitWrapperHoldingViewModel::class.java -> {
                    EvmKitWrapperHoldingViewModel(evmKitWrapper) as T
                }
                AddressParserViewModel::class.java -> {
                    AddressParserViewModel(AddressUriParser(nftUid.blockchainType, null), null) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    const val nftUidKey = "nftUidKey"

    fun prepareParams(nftUid: String) = bundleOf(
        nftUidKey to nftUid
    )

    data class SendEip721UiState(
        val name: String,
        val imageUrl: String?,
        val addressError: Throwable?,
        val canBeSend: Boolean
    )

    data class SendEip1155UiState(
        val name: String,
        val imageUrl: String?,
        val addressError: Throwable?,
        val amountState: DataState<Int>?,
        val canBeSend: Boolean
    )

}