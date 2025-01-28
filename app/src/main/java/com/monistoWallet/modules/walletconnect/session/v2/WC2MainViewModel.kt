package com.monistoWallet.modules.walletconnect.session.v2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.monistoWallet.core.App
import com.monistoWallet.modules.walletconnect.version2.WC2Service
import com.monistoWallet.modules.walletconnect.version2.WC2SessionManager
import com.monistoWallet.core.SingleLiveEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow

class WC2MainViewModel(
    private val wc2Service: WC2Service,
    private val wcSessionManager: WC2SessionManager,
) : ViewModel() {

    val sessionProposalLiveEvent = SingleLiveEvent<Unit>()
    val openWalletConnectRequestLiveEvent = SingleLiveEvent<Long>()

    init {
        viewModelScope.launch {
            wc2Service.eventObservable.asFlow()
                .collect {
                    if (it is WC2Service.Event.WaitingForApproveSession) {
                        sessionProposalLiveEvent.postValue(Unit)
                    }
                }
        }

        viewModelScope.launch {
            wcSessionManager.pendingRequestObservable.asFlow()
                .collect {
                    openWalletConnectRequestLiveEvent.postValue(it)
                }
        }
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WC2MainViewModel(com.monistoWallet.core.App.wc2Service, com.monistoWallet.core.App.wc2SessionManager) as T
        }
    }
}
