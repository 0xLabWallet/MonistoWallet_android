package com.monistoWallet.modules.send

import com.monistoWallet.entities.Address
import com.monistoWallet.modules.contacts.model.Contact
import com.wallet0x.hodler.LockTimeInterval
import com.wallet0x.marketkit.models.Coin
import java.math.BigDecimal

data class SendConfirmationData(
    val amount: BigDecimal,
    val fee: BigDecimal,
    val address: Address,
    val contact: Contact?,
    val coin: Coin,
    val feeCoin: Coin,
    val lockTimeInterval: LockTimeInterval? = null,
    val memo: String? = null,
)
