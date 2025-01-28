package com.monistoWallet.additional_wallet0x.root.main.ui.model

interface RechargeScreenState {
    object Null : RechargeScreenState
    class Result(val text1: String, val text2: String) : RechargeScreenState
}