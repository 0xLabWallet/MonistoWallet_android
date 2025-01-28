package com.monistoWallet.additional_wallet0x.root.get_card_data.domain.api

import com.monistoWallet.additional_wallet0x.root.get_card_data.data.model.Data
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.model.SaveCardSecretDataResponse

interface SaveCardDataRepository {
    fun saveCardData(cardId: String, data: Data)
    fun getCardData(cardId: String) : SaveCardSecretDataResponse
}