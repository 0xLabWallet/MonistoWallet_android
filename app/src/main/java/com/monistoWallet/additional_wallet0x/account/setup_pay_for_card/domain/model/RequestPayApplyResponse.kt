package com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.model

import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.data.model.RequestPayForCardResponseModel

interface RequestPayApplyResponse {
    class Error(val message: String) : RequestPayApplyResponse
    class Success(val result: RequestPayForCardResponseModel) : RequestPayApplyResponse
}