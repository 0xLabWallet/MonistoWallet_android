package com.monistoWallet.additional_wallet0x.root.get_card_data.domain.model

import com.monistoWallet.additional_wallet0x.root.get_card_data.data.model.Data

interface CardSecretDataResponse {
    class Error(val message: String) : CardSecretDataResponse
    class Success(val data: Data) : CardSecretDataResponse
}