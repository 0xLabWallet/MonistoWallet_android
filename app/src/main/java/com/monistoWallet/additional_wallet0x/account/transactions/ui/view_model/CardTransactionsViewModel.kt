package com.monistoWallet.additional_wallet0x.account.transactions.ui.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.additional_wallet0x.account.transactions.domain.api.CardTransactionsInteractor
import com.monistoWallet.additional_wallet0x.account.transactions.domain.model.CardListTransactionsResponse
import com.monistoWallet.additional_wallet0x.account.transactions.ui.model.TransactionsScreenState
import com.monistoWallet.additional_wallet0x.root.main.domain.api.TokenDatabaseInteractor
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager.Companion.REFRESH_TOKEN_ERROR_CODE
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenResponse
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card

class CardTransactionsViewModel(
    private val tokenDatabase: TokenDatabaseInteractor,
    private val cardTransactionsInteractor: CardTransactionsInteractor,
) : ViewModel() {
    var cardTransactionsScreenState: TransactionsScreenState by mutableStateOf(TransactionsScreenState.Null)
        private set

    fun getCardTransactions(card: Card) {
        cardTransactionsScreenState = TransactionsScreenState.Loading
        val token = tokenDatabase.getToken() ?: return
        cardTransactionsInteractor.getTransactionsList(token.access_token, card) {
            when (it) {
                is CardListTransactionsResponse.Error -> {
                    if (it.message.contains(REFRESH_TOKEN_ERROR_CODE)) {
                        RefreshTokenManager.updateAccessToken(token.refresh_token) {
                            when (it) {
                                is RefreshTokenResponse.Error -> {
                                    cardTransactionsScreenState = TransactionsScreenState.Error(it.message)
                                }
                                is RefreshTokenResponse.Success -> {
                                    tokenDatabase.saveToken(it.model)
                                    getCardTransactions(card)
                                }
                            }
                        }
                        return@getTransactionsList
                    }
                    cardTransactionsScreenState = TransactionsScreenState.Error(it.message)
                }

                is CardListTransactionsResponse.Success -> {
                    if (it.data.data.transactions.isEmpty()) {
                        cardTransactionsScreenState = TransactionsScreenState.NotFound
                    } else {
                        cardTransactionsScreenState = TransactionsScreenState.Success(it.data.data.transactions)
                    }

                }
            }
        }
    }

    fun clearHistory() {
        cardTransactionsScreenState = TransactionsScreenState.Null
    }
}