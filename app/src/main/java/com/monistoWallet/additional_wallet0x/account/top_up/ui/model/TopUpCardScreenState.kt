package com.monistoWallet.additional_wallet0x.account.top_up.ui.model

import com.monistoWallet.additional_wallet0x.root.model.RechargeSettings

interface TopUpCardScreenState {
    object Null : TopUpCardScreenState
    object Loading : TopUpCardScreenState
    class Error(val message: String) : TopUpCardScreenState
    class Success(val model: RechargeSettings) : TopUpCardScreenState
}