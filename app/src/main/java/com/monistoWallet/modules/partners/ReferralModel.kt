package com.monistoWallet.modules.partners

class ReferralModel(
    val status: String,
    val completedTasks: Int,
    val maxTasks: Int,
    val referrerCode: String,
    val referralCode: String,
    val referralsList: List<Referral>,
    val usdtBalance: Float,
    val dexnetBalance: Float,
    val allRefQuantity: Int,
    val rangeImageName: String,
    val progress: Float,
    val dataIsHidden: Boolean,
    val accessToken: String
)