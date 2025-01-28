package com.monistoWallet.modules.contacts

import android.os.Parcelable
import com.wallet0x.marketkit.models.BlockchainType
import kotlinx.parcelize.Parcelize

sealed class Mode : Parcelable {
    @Parcelize
    object Full : Mode()

    @Parcelize
    class AddAddressToExistingContact(val blockchainType: BlockchainType, val address: String) : Mode()

    @Parcelize
    class AddAddressToNewContact(val blockchainType: BlockchainType, val address: String) : Mode()
}
