package com.monistoWallet.modules.send.binance

import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.HSCaution
import com.monistoWallet.core.ISendBinanceAdapter
import com.monistoWallet.core.providers.FeeTokenProvider
import com.monistoWallet.ui.compose.TranslatableString
import com.wallet0x.marketkit.models.Token
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

class SendBinanceFeeService(
    private val adapter: ISendBinanceAdapter,
    private val token: Token,
    private val feeTokenProvider: FeeTokenProvider
) {
    private val feeTokenData = feeTokenProvider.feeTokenData(token)
    val feeToken = feeTokenData?.first ?: token

    private val fee = adapter.fee

    private var feeCaution: HSCaution? = null

    private val _stateFlow = MutableStateFlow(
        State(
            fee = fee,
            feeCaution = feeCaution,
            canBeSend = false
        )
    )
    val stateFlow = _stateFlow.asStateFlow()

    fun start() {
        validate()

        emitState()
    }

    private fun validate() {
        feeCaution = feeTokenData?.let { (feeToken, tokenProtocol) ->
            if (fee > adapter.availableBinanceBalance) {
                val feeTokenName = feeToken.coin.name
                val feeTokenCode = feeToken.coin.code
                val formattedFee = com.monistoWallet.core.App.numberFormatter.formatCoinFull(fee, feeTokenCode,8)

                HSCaution(
                    s = TranslatableString.ResString(R.string.Swap_ErrorInsufficientBalance),
                    description = TranslatableString.ResString(
                        R.string.Send_Token_InsufficientFeeAlert, token.coin.code, tokenProtocol,
                        feeTokenName, formattedFee
                    )
                )
            } else {
                null
            }
        }
    }

    private fun emitState() {
        _stateFlow.update {
            State(
                fee = fee,
                feeCaution = feeCaution,
                canBeSend = feeCaution == null
            )
        }
    }

    data class State(
        val fee: BigDecimal,
        val feeCaution: HSCaution?,
        val canBeSend: Boolean
    )
}
