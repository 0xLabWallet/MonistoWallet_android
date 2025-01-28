package com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.ui.model

import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.data.model.RequestPayForCardResponseModel
import com.monistoWallet.additional_wallet0x.root.model.BaseRechargeSettings

interface RequestPayApplyScreenState {
    object Null : RequestPayApplyScreenState
    object Loading : RequestPayApplyScreenState
    class Error(val message: String) : RequestPayApplyScreenState
    class Result(val model: RequestPayForCardResponseModel, val baseRechargeSettings: BaseRechargeSettings) : RequestPayApplyScreenState
}