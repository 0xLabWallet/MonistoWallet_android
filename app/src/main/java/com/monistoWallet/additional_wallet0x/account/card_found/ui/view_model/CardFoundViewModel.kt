package com.monistoWallet.additional_wallet0x.account.card_found.ui.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.impl.CardDataManager
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.model.CardSecretDataResponse
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card

class CardFoundViewModel(
    private val cardDataManager: CardDataManager,
) : ViewModel() {

    var selectedCard: Card? by mutableStateOf(null)
        private set

    fun selectCard(card: Card) {
        loadCardData(card)
    }

    fun loadCardData(card: Card) {
        cardDataManager.getCardData(card.id) {
            when(it) {
                is CardSecretDataResponse.Error -> {
                    selectedCard = card
                }

                is CardSecretDataResponse.Success -> {
                    val myCvv = it.data.cvv
                    val myDate = it.data.expiry_date
                    selectedCard = card.copy(card_cvv = myCvv, card_expires = myDate)
                }
            }
        }
    }
}