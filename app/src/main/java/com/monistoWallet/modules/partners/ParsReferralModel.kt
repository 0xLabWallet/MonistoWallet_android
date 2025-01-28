package com.monistoWallet.modules.partners

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseData(
//    @SerialName("result")
    val result: String,
//    @SerialName("data")
    val data: DataInfo
)

@Serializable
data class DataInfo(
    @SerialName("user_info")
    val user_info: UserInfo,

    @SerialName("referral_system")
    val referral_system: ReferralSystem
)

@Serializable
data class ReferralSystem(
    @SerialName("status")
    val status: String,
    @SerialName("referrer_code")
    val referrer_code: String,
    @SerialName("referral_code")
    val referral_code: String,
    @SerialName("usdt_balance")
    val usdt_balance: Float,
    @SerialName("dexnet_balance")
    val dexnet_balance: Float,
    @SerialName("completed_tasks")
    val completed_tasks: Int,
    @SerialName("max_tasks")
    val max_tasks: Int,
    @SerialName("all_ref_quantity")
    val all_ref_quantity: Int,
    val referrals: List<Referral>
)

@Serializable
data class UserInfo(
    @SerialName("uuid")
    val uuid: String,
    @SerialName("full_name")
    val full_name: String,
    @SerialName("access_token")
    val access_token: String
)

@Serializable
data class Referral(
    @SerialName("full_name")
    val full_name: String,
    @SerialName("uuid")
    val uuid: String,
    @SerialName("status")
    val status: String,
    @SerialName("ref_quantity")
    val ref_quantity: Int
)
