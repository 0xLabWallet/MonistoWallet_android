package com.monistoWallet.modules.send.bitcoin

import com.monistoWallet.core.HSCaution
import com.monistoWallet.core.ISendBitcoinAdapter
import com.monistoWallet.entities.Address
import com.monistoWallet.modules.amount.AmountValidator
import com.wallet0x.bitcoincore.core.IPluginData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

class SendBitcoinAmountService(
    private val adapter: ISendBitcoinAdapter,
    private val coinCode: String,
    private val amountValidator: AmountValidator
) {
    private var amount: BigDecimal? = null
    private var amountCaution: HSCaution? = null

    private var minimumSendAmount: BigDecimal? = null
    private var availableBalance: BigDecimal? = null
    private var validAddress: Address? = null
    private var feeRate: Int? = null
    private var pluginData: Map<Byte, IPluginData>? = null

    private val _stateFlow = MutableStateFlow(
        State(
            amount = amount,
            amountCaution = amountCaution,
            availableBalance = availableBalance,
            canBeSend = false,
        )
    )
    val stateFlow = _stateFlow.asStateFlow()

    private fun emitState() {
        val tmpAmount = amount
        val tmpAmountCaution = amountCaution

        val canBeSend = tmpAmount != null
            && tmpAmount > BigDecimal.ZERO
            && (tmpAmountCaution == null || tmpAmountCaution.isWarning())

        _stateFlow.update {
            State(
                amount = amount,
                amountCaution = amountCaution,
                availableBalance = availableBalance,
                canBeSend = canBeSend
            )
        }
    }

    private fun refreshAvailableBalance() {
        availableBalance = feeRate?.let { adapter.availableBalance(it, validAddress?.hex, pluginData) }
    }

    private fun refreshMinimumSendAmount() {
        minimumSendAmount = adapter.minimumSendAmount(validAddress?.hex)
    }

    private fun validateAmount() {
        amountCaution = amountValidator.validate(
            amount,
            coinCode,
            availableBalance ?: BigDecimal.ZERO,
            minimumSendAmount,
        )
    }

    fun setAmount(amount: BigDecimal?) {
        this.amount = amount

        validateAmount()

        emitState()
    }

    fun setValidAddress(validAddress: Address?) {
        this.validAddress = validAddress

        refreshAvailableBalance()
        refreshMinimumSendAmount()
        validateAmount()

        emitState()
    }

    fun setFeeRate(feeRate: Int?) {
        this.feeRate = feeRate

        refreshAvailableBalance()
        validateAmount()

        emitState()
    }

    fun setPluginData(pluginData: Map<Byte, IPluginData>?) {
        this.pluginData = pluginData

        refreshAvailableBalance()
        validateAmount()

        emitState()
    }

    data class State(
        val amount: BigDecimal?,
        val amountCaution: HSCaution?,
        val availableBalance: BigDecimal?,
        val canBeSend: Boolean
    )
}
