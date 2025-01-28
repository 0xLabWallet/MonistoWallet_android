package com.monistoWallet.additional_wallet0x.root.get_card_data.domain.impl

import android.util.Log
import com.monistoWallet.additional_wallet0x.root.get_card_data.data.model.Data
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.api.GetCardDataRepository
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.api.SaveCardDataRepository
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.model.CardSecretDataResponse
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.model.SaveCardSecretDataResponse
import com.monistoWallet.additional_wallet0x.root.main.domain.api.TokenDatabaseInteractor

class CardDataManager(
    private val tokenDatabaseInteractor: TokenDatabaseInteractor,
    private val saveCardDataRepository: SaveCardDataRepository,
    private val getCardDataRepository: GetCardDataRepository,
) {
    private fun saveCardData(cardId: String, data: Data) {
        saveCardDataRepository.saveCardData(cardId, data)
    }

    private fun loadCardData(cardId: String, onResponse: (CardSecretDataResponse) -> Unit) {
        val token = tokenDatabaseInteractor.getToken()
        getCardDataRepository.loadCardData((token ?: return).access_token, cardId, onResponse)
    }

    fun getCardData(cardId: String, onResponse: (CardSecretDataResponse) -> Unit) {
        val isContains = saveCardDataRepository.getCardData(cardId)
        when (isContains) {
            is SaveCardSecretDataResponse.CardFound -> {
                onResponse.invoke(CardSecretDataResponse.Success(isContains.data))
            }

            is SaveCardSecretDataResponse.CardNotFound -> {
                loadCardData(cardId) {
                    when (it) {
                        is CardSecretDataResponse.Success -> {
                            saveCardData(cardId, it.data)
                            onResponse.invoke(it)
                        }

                        is CardSecretDataResponse.Error -> {
                            onResponse.invoke(it)
                        }
                    }
                }
            }
        }
    }
}