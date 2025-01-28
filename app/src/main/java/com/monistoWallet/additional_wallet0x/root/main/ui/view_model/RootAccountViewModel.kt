package com.monistoWallet.additional_wallet0x.root.main.ui.view_model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.data.model.RequestPayForCardResponseModel
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.api.RequestPayForCardInteractor
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.model.RequestPayApplyResponse
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.ui.model.RequestPayApplyScreenState
import com.monistoWallet.additional_wallet0x.account.top_up.domain.api.TopUpCardInteractor
import com.monistoWallet.additional_wallet0x.account.top_up.ui.model.TopUpCardScreenState
import com.monistoWallet.additional_wallet0x.root.main.domain.api.TokenDatabaseInteractor
import com.monistoWallet.additional_wallet0x.root.main.ui.model.ErrorScreenState
import com.monistoWallet.additional_wallet0x.root.main.ui.model.RootCardScreenState
import com.monistoWallet.additional_wallet0x.root.main.ui.model.TopUpScreenState
import com.monistoWallet.additional_wallet0x.root.main.ui.model.UserScreenState
import com.monistoWallet.additional_wallet0x.root.model.BaseRechargeSettings
import com.monistoWallet.additional_wallet0x.root.model.RechargeSettings
import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel
import com.monistoWallet.additional_wallet0x.root.sse_buy_card.ui.model.BuyCardScreenState
import com.monistoWallet.additional_wallet0x.root.sse_payment_error_received.data.model.PaymentErrorModel
import com.monistoWallet.additional_wallet0x.root.sse_top_up_received.data.model.SSETopUpReceivedModel
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager.Companion.REFRESH_TOKEN_ERROR_CODE
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenResponse
import com.monistoWallet.additional_wallet0x.root.tokens.SSEClient
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card
import com.monistoWallet.additional_wallet0x.root.tokens.model.SseResponseModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RootAccountViewModel(
    private val tokenDatabase: TokenDatabaseInteractor,
    private val refreshTokenManager: RefreshTokenManager,
    private val sseClient: SSEClient,
    private val topUpCardInteractor: TopUpCardInteractor,
    private val requestPayForCardInteractor: RequestPayForCardInteractor,
) : ViewModel() {
    var screenStateLD: RootCardScreenState by mutableStateOf(RootCardScreenState.Null)
        private set

    var rechargeSSEScreenState: TopUpScreenState by mutableStateOf(TopUpScreenState.Null)
    var rechargeFullData: RechargeSettings? by mutableStateOf(null)

    var errorSSEScreenState: ErrorScreenState by mutableStateOf(ErrorScreenState.Null)

    var userCardsScreenStateLD: UserScreenState by mutableStateOf(UserScreenState.Null)

    init {
        updateAccount()
    }
    fun logout() {
        userEmail = ""
        tokenDatabase.saveToken(null)
        screenStateLD = RootCardScreenState.NoAccount
        sseClient.stopSse()
    }

    private fun updateAccount() {
        Log.d("Wallet0xTag", "RootAccountViewModel.updateAccount")
        screenStateLD = RootCardScreenState.Loading
        viewModelScope.launch {
            val result = tokenDatabase.getToken()
            if (result != null) {
                Log.d("Wallet0xTag", "RootAccountViewModel.HasAccount $result")
                refreshToken {
                    sseClient.updateAccessToken(it)
                    connectWebSocket()
                }
            } else {
                Log.d("Wallet0xTag", "RootAccountViewModel.NoAccount")
                screenStateLD = RootCardScreenState.NoAccount
            }
        }
    }

    fun saveAccountToken(it: VerificationSuccessModel) {
        Log.d("Wallet0xTag", "RootAccountViewModel.saveAccountToken $it")
        tokenDatabase.saveToken(it)
    }

    fun saveAccountTokenAndUpdate(it: VerificationSuccessModel) {
        Log.d("Wallet0xTag", "RootAccountViewModel.saveAccountTokenAndUpdate $it")
        screenStateLD = RootCardScreenState.Loading
        tokenDatabase.saveToken(it)
        updateAccount()
    }


    private fun refreshToken(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val tokens = tokenDatabase.getToken()
            if (tokens == null) {
                screenStateLD = RootCardScreenState.NoAccount
            } else {
                refreshTokenManager.updateAccessToken(tokens.refresh_token) {
                    when (it) {
                        is RefreshTokenResponse.Error -> {
                            screenStateLD = RootCardScreenState.NoAccount
                        }

                        is RefreshTokenResponse.Success -> {
                            Log.d("Wallet0xTag", "RootAccountViewModel.refreshToken ${it.model}")
                            screenStateLD = RootCardScreenState.Account(it.model)
                            saveAccountToken(it.model)
                            onSuccess.invoke(it.model.access_token)
                        }
                    }
                }
            }
        }
    }

    private fun connectWebSocket() {
        viewModelScope.launch {
            sseClient.initSse(object : SSEClient.SSEHandler {
                override fun onError401() {
                    refreshToken {
                        sseClient.updateAccessToken(it)
                    }
                }

                override fun onSSEConnectionOpened() {
                    Log.d("SSEClient", "Connection opened")
                }

                override fun onSSEConnectionClosed() {
                    Log.d("SSEClient", "Connection closed")
                }

                override fun onSSEEventReceived(event: String, message: String) {
                    Log.d("SSEClient", "Event received: $event, $message")
                    when (event) {
                        "user_data" -> {
                            val response = Gson().fromJson(message, SseResponseModel::class.java)
                            userEmail = response.email
                            if (response.cards_list.isNotEmpty()) {
                                userCardsScreenStateLD = UserScreenState.CardsFound(response)
                            } else {
                                userCardsScreenStateLD = UserScreenState.CardsNotFound(response)
                            }
                        }

                        "apply_payment" -> {
                            buyCardSSEScreenState = BuyCardScreenState.Success
                        }

                        "payment_error" -> {
                            val response = Gson().fromJson(message, PaymentErrorModel::class.java).message
                            errorSSEScreenState = ErrorScreenState.Error(response)
                        }

                        "recharge_payment" -> {
                            val response = Gson().fromJson(message, SSETopUpReceivedModel::class.java)
                            rechargeSSEScreenState = TopUpScreenState.Result(response)
                        }

                        "error" -> {
                            //Ошибка на беке(Ничего делать не надо)
                        }
                    }
                }

                override fun onSSEError(t: Throwable) {
                    Log.e("SSEClient", "Error: ${t.message}")
                    viewModelScope.launch {
                        delay(2000)
                        connectWebSocket()
                    }
                }
            })
        }
    }

    fun getAllCardsList(): List<Card> {
        return (userCardsScreenStateLD as UserScreenState.CardsFound).sseModel.cards_list
    }

    var topUpCardScreenState: TopUpCardScreenState by mutableStateOf(TopUpCardScreenState.Null)

    fun requestRecharge(card: Card, baseRechargeSettings: BaseRechargeSettings) {
        if (topUpCardScreenState == TopUpCardScreenState.Null) {
            topUpCardScreenState = TopUpCardScreenState.Loading
            val accessToken = (tokenDatabase.getToken() ?: return).access_token
            topUpCardInteractor.topUpCard(accessToken, card.id, baseRechargeSettings.selectedNetwork, baseRechargeSettings.selectedCurrency) {
                when (it) {
                    is RequestPayApplyResponse.Error -> {
                        topUpCardScreenState = TopUpCardScreenState.Error(it.message)
                    }
                    is RequestPayApplyResponse.Success -> {
                        val settings = RechargeSettings(it.result, baseRechargeSettings.selectedNetwork, baseRechargeSettings.selectedCurrency, baseRechargeSettings.minPay, baseRechargeSettings.maxPay)
                        topUpCardScreenState = TopUpCardScreenState.Success(settings)
                    }
                }
            }
        }
    }

    var buyCardSSEScreenState: BuyCardScreenState by mutableStateOf(BuyCardScreenState.Null)
    var buyCardFullData: RechargeSettings? by mutableStateOf(null)
    var requestPayApplyScreenState: RequestPayApplyScreenState by mutableStateOf(
        RequestPayApplyScreenState.Null)
    fun requestPayApply(cardLayoutId: String, baseRechargeSettings: BaseRechargeSettings) {
        viewModelScope.launch {
            requestPayApplyScreenState = RequestPayApplyScreenState.Loading
            val token = (tokenDatabase.getToken() ?: return@launch)
            requestPayForCardInteractor.requestPayApply(token.access_token, cardLayoutId, baseRechargeSettings.selectedNetwork, baseRechargeSettings.selectedCurrency) {
                when (it) {
                    is RequestPayApplyResponse.Error -> {
                        if (it.message.contains(REFRESH_TOKEN_ERROR_CODE)) {
                            RefreshTokenManager.updateAccessToken(token.refresh_token) {
                                when (it) {
                                    is RefreshTokenResponse.Error -> {
                                        requestPayApplyScreenState = RequestPayApplyScreenState.Error(it.message)
                                    }
                                    is RefreshTokenResponse.Success -> {
                                        tokenDatabase.saveToken(it.model)
                                        requestPayApply(cardLayoutId, baseRechargeSettings)
                                    }
                                }
                            }
                            return@requestPayApply
                        }
                        requestPayApplyScreenState = RequestPayApplyScreenState.Error(it.message)
                    }
                    is RequestPayApplyResponse.Success -> {
                        requestPayApplyScreenState = RequestPayApplyScreenState.Result(it.result, baseRechargeSettings)
                    }
                }
            }
        }
    }
    companion object {
        var userEmail = ""
    }
}