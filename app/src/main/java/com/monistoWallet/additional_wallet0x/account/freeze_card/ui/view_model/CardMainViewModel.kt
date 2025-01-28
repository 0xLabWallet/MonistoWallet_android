package com.monistoWallet.additional_wallet0x.account.freeze_card.ui.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.additional_wallet0x.account.card_variants.domain.api.CurrentCardListInteractor
import com.monistoWallet.additional_wallet0x.account.card_variants.domain.model.CardsListVariantsResponse
import com.monistoWallet.additional_wallet0x.account.card_variants.ui.model.CardVariantsScreenState
import com.monistoWallet.additional_wallet0x.account.freeze_card.domain.api.CardFreezeManagerInteractor
import com.monistoWallet.additional_wallet0x.account.freeze_card.domain.model.CardFreezeResponse
import com.monistoWallet.additional_wallet0x.account.freeze_card.ui.model.FreezeCardScreenState
import com.monistoWallet.additional_wallet0x.root.main.domain.api.TokenDatabaseInteractor
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager.Companion.REFRESH_TOKEN_ERROR_CODE
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenResponse
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card

class CardMainViewModel(
    private val tokenDatabase: TokenDatabaseInteractor,
    private val cardFreezeManagerInteractor: CardFreezeManagerInteractor,
    private val currentCardListInteractor: CurrentCardListInteractor,
) : ViewModel() {
    var freezeCardScreenState: FreezeCardScreenState by mutableStateOf(FreezeCardScreenState.Null)
        private set

    var cardVariantsScreenState: CardVariantsScreenState by mutableStateOf(CardVariantsScreenState.Null)
        private set

    fun freezeCard(card: Card) {
        freezeCardScreenState = FreezeCardScreenState.Loading
        val token = (tokenDatabase.getToken() ?: return)
        cardFreezeManagerInteractor.freezeCard(token.access_token, card) {
            when (it) {
                is CardFreezeResponse.Error -> {
                    if (it.message.contains(REFRESH_TOKEN_ERROR_CODE)) {
                        RefreshTokenManager.updateAccessToken(token.refresh_token) {
                            when (it) {
                                is RefreshTokenResponse.Error -> {
                                    freezeCardScreenState = FreezeCardScreenState.Error(it.message)
                                }
                                is RefreshTokenResponse.Success -> {
                                    tokenDatabase.saveToken(it.model)
                                    freezeCard(card)
                                }
                            }
                        }
                        return@freezeCard
                    }
                    freezeCardScreenState = FreezeCardScreenState.Error(it.message)
                }

                is CardFreezeResponse.Success -> {
                    freezeCardScreenState = FreezeCardScreenState.Success(it.message)
                }
            }
        }

    }

    fun unfreezeCard(card: Card) {
        freezeCardScreenState = FreezeCardScreenState.Loading
        val token = (tokenDatabase.getToken() ?: return)
        cardFreezeManagerInteractor.unfreezeCard(token.access_token, card) {
            when (it) {
                is CardFreezeResponse.Error -> {
                    if (it.message.contains(REFRESH_TOKEN_ERROR_CODE)) {
                        RefreshTokenManager.updateAccessToken(token.refresh_token) {
                            when (it) {
                                is RefreshTokenResponse.Error -> {
                                    freezeCardScreenState = FreezeCardScreenState.Error(it.message)
                                }
                                is RefreshTokenResponse.Success -> {
                                    tokenDatabase.saveToken(it.model)
                                    freezeCard(card)
                                }
                            }
                        }
                        return@unfreezeCard
                    }
                    freezeCardScreenState = FreezeCardScreenState.Error(it.message)
                }

                is CardFreezeResponse.Success -> {
                    freezeCardScreenState = FreezeCardScreenState.Success(it.message)
                }
            }
        }
    }

    fun topUpCard() {
        cardVariantsScreenState = CardVariantsScreenState.Loading
        val token = (tokenDatabase.getToken() ?: return)
        currentCardListInteractor.getAllCards(token.access_token) {
            when (it) {
                is CardsListVariantsResponse.Error -> {
                    if (it.message.contains(REFRESH_TOKEN_ERROR_CODE)) {
                        RefreshTokenManager.updateAccessToken(token.refresh_token) {
                            when (it) {
                                is RefreshTokenResponse.Error -> {
                                    freezeCardScreenState = FreezeCardScreenState.Error(it.message)
                                }
                                is RefreshTokenResponse.Success -> {
                                    tokenDatabase.saveToken(it.model)
                                    topUpCard()
                                }
                            }
                        }
                        return@getAllCards
                    }
                    cardVariantsScreenState = CardVariantsScreenState.Error(it.message)
                }
                is CardsListVariantsResponse.Success -> {
                    cardVariantsScreenState = CardVariantsScreenState.Result(it.model)
                }
            }
        }
    }

    fun clearFreezeCardState() {
        freezeCardScreenState = FreezeCardScreenState.Null
    }

}