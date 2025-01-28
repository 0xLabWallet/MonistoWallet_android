package com.monistoWallet.modules.send.tron

import com.monistoWallet.R
import com.monistoWallet.core.ISendTronAdapter
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.entities.Address
import com.monistoWallet.ui.compose.components.FormsInputStateWarning
import com.wallet0x.marketkit.models.Token
import com.wallet0x.marketkit.models.TokenType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.wallet0x.tronkit.models.Address as TronAddress

class SendTronAddressService(
    private val adapter: ISendTronAdapter,
    private val token: Token,
    prefilledAddress: String?
) {
    var address: Address? = prefilledAddress?.let { Address(it) }
        private set
    private var addressError: Throwable? = null
    private var tronAddress: TronAddress? = prefilledAddress?.let { TronAddress.fromBase58(it) }
    private var isInactiveAddress: Boolean = false

    private val _stateFlow = MutableStateFlow(
        State(
            address = address,
            tronAddress = tronAddress,
            addressError = addressError,
            isInactiveAddress = isInactiveAddress,
            canBeSend = tronAddress != null && (addressError == null || addressError is FormsInputStateWarning)
        )
    )
    val stateFlow = _stateFlow.asStateFlow()

    suspend fun setAddress(address: Address?) {
        this.address = address

        validateAddress()

        emitState()
    }

    private suspend fun validateAddress() {
        addressError = null
        tronAddress = null
        val address = this.address ?: return

        try {
            val validAddress = TronAddress.fromBase58(address.hex)

            if (!adapter.isAddressActive(validAddress)) {
                isInactiveAddress = true
                addressError = FormsInputStateWarning(Translator.getString(R.string.Tron_AddressNotActive_Warning))
            } else {
                isInactiveAddress = false
            }

            if (token.type == TokenType.Native && adapter.isOwnAddress(validAddress)) {
                addressError = Throwable(Translator.getString(R.string.Tron_SelfSendTrxNotAllowed))
            }

            tronAddress = validAddress
        } catch (e: Exception) {
            isInactiveAddress = false
            addressError = Throwable(Translator.getString(R.string.SwapSettings_Error_InvalidAddress))
        }
    }

    private fun emitState() {
        _stateFlow.update {
            State(
                address = address,
                tronAddress = tronAddress,
                addressError = addressError,
                isInactiveAddress = isInactiveAddress,
                canBeSend = tronAddress != null && (addressError == null || addressError is FormsInputStateWarning)
            )
        }
    }

    data class State(
        val address: Address?,
        val tronAddress: TronAddress?,
        val addressError: Throwable?,
        val isInactiveAddress: Boolean,
        val canBeSend: Boolean
    )
}
