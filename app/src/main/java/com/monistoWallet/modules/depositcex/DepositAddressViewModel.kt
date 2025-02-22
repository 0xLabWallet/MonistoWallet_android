package com.monistoWallet.modules.depositcex

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.providers.CexAsset
import com.monistoWallet.core.providers.CexDepositNetwork
import com.monistoWallet.core.providers.CexProviderManager
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.receive.address.ReceiveAddressModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal

class DepositAddressViewModel(
    private val cexAsset: CexAsset,
    private val network: CexDepositNetwork?,
    cexProviderManager: CexProviderManager
) : ViewModel() {
    private val cexProvider = cexProviderManager.cexProviderFlow.value

    private var viewState: ViewState = ViewState.Loading
    private var address = ""
    private var uri = ""
    private var amount: BigDecimal? = null
    private var memo: String? = null
    private val networkName = network?.name ?: cexAsset.depositNetworks.firstOrNull()?.name ?: ""
    private val watchAccount = false

    var uiState by mutableStateOf(
        ReceiveAddressModule.UiState(
            viewState = viewState,
            address = address,
            uri = uri,
            networkName = networkName,
            watchAccount = watchAccount,
            additionalItems = getAdditionalData(),
            amount = amount,
            alertText = getAlertText(memo != null)
        )
    )

    init {
        setInitialData()
    }

    private fun setInitialData() {
        viewState = ViewState.Loading
        emitState()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cexAddress = cexProvider?.getAddress(cexAsset.id, network?.id)
                if (cexAddress == null) {
                    viewState = ViewState.Error(Throwable("No address"))
                } else {
                    if (cexAddress.tag.isNotBlank()) {
                        memo = cexAddress.tag
                    }
                    address = cexAddress.address
                    uri = cexAddress.address
                    viewState = ViewState.Success
                }
            } catch (t: Throwable) {
                viewState = ViewState.Error(t)
            }
            emitState()
        }
    }

    private fun emitState() {
        uiState = ReceiveAddressModule.UiState(
            viewState = viewState,
            address = address,
            uri = uri,
            networkName = networkName,
            watchAccount = watchAccount,
            additionalItems = getAdditionalData(),
            amount = amount,
            alertText = getAlertText(memo != null)
        )
    }

    private fun getAdditionalData(): List<ReceiveAddressModule.AdditionalData> {
        val items = mutableListOf<ReceiveAddressModule.AdditionalData>()

        memo?.let {
            items.add(
                ReceiveAddressModule.AdditionalData.Memo(
                    value = it
                )
            )
        }

        amount?.let {
            items.add(
                ReceiveAddressModule.AdditionalData.Amount(
                    value = it.toString()
                )
            )
        }

        return items
    }

    private fun getAlertText(hasMemo: Boolean): ReceiveAddressModule.AlertText {
        return when {
            hasMemo -> ReceiveAddressModule.AlertText.Critical(
                Translator.getString(R.string.Balance_Receive_AddressMemoAlert)
            )

            else -> ReceiveAddressModule.AlertText.Normal(
                Translator.getString(R.string.Balance_Receive_AddressAlert)
            )
        }
    }

    fun onErrorClick() {
        setInitialData()
    }

    fun setAmount(amount: BigDecimal?) {
        amount?.let {
            if (it <= BigDecimal.ZERO) {
                this.amount = null
                emitState()
                return
            }
        }
        this.amount = amount
        emitState()
    }

    class Factory(private val cexAsset: CexAsset, private val network: CexDepositNetwork?) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return com.monistoWallet.modules.depositcex.DepositAddressViewModel(
                cexAsset,
                network,
                com.monistoWallet.core.App.cexProviderManager
            ) as T
        }
    }
}
