package com.monistoWallet.additional_wallet0x.account.card_variants.ui.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.additional_wallet0x.account.card_variants.domain.api.CurrentCardListInteractor
import com.monistoWallet.additional_wallet0x.account.card_variants.domain.model.CardsListVariantsResponse
import com.monistoWallet.additional_wallet0x.account.card_variants.ui.model.CardVariantsScreenState
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.api.RequestPayForCardInteractor
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.model.RequestPayApplyResponse
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.ui.model.RequestPayApplyScreenState
import com.monistoWallet.additional_wallet0x.root.main.domain.api.TokenDatabaseInteractor
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager.Companion.REFRESH_TOKEN_ERROR_CODE
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenResponse
import kotlinx.coroutines.launch

class BuyCardViewModel(
    private val tokenDatabaseInteractor: TokenDatabaseInteractor,
    private val currentCardListInteractor: CurrentCardListInteractor,
) : ViewModel() {
    var cardVariantsScreenState: CardVariantsScreenState by mutableStateOf(CardVariantsScreenState.Null)
        private set

    fun getCurrentCardsList() {
        cardVariantsScreenState = CardVariantsScreenState.Loading
        val token = (tokenDatabaseInteractor.getToken() ?: return)
        currentCardListInteractor.getAllCards(token.access_token) {
            when (it) {
                is CardsListVariantsResponse.Error -> {
                    if (it.message.contains(REFRESH_TOKEN_ERROR_CODE)) {
                        RefreshTokenManager.updateAccessToken(token.refresh_token) {
                            when (it) {
                                is RefreshTokenResponse.Error -> {
                                    cardVariantsScreenState = CardVariantsScreenState.Error(it.message)
                                }
                                is RefreshTokenResponse.Success -> {
                                    tokenDatabaseInteractor.saveToken(it.model)
                                    getCurrentCardsList()
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
}