package com.monistoWallet.additional_wallet0x.root.get_card_data.domain.model

import com.monistoWallet.additional_wallet0x.root.get_card_data.data.model.Data

interface SaveCardSecretDataResponse {
    object CardNotFound : SaveCardSecretDataResponse
    class CardFound(val data: Data) : SaveCardSecretDataResponse
}