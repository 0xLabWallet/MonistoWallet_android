package com.monistoWallet.modules.send.tron

import com.monistoWallet.entities.Address
import com.monistoWallet.modules.contacts.model.Contact
import com.wallet0x.marketkit.models.Coin
import java.math.BigDecimal

data class SendTronConfirmationData(
    val amount: BigDecimal,
    val address: Address,
    val fee: BigDecimal?,
    val activationFee: BigDecimal?,
    val resourcesConsumed: String?,
    val contact: Contact?,
    val coin: Coin,
    val feeCoin: Coin,
    val isInactiveAddress: Boolean,
    val memo: String? = null,
)
