package com.monistoWallet.modules.partners

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubreferralData(
    val result: String,
    val data: SubreferralDataClass
)

@Serializable
data class SubreferralDataClass(
    @SerialName("referrals")
    val referrals: List<Referral>
)
