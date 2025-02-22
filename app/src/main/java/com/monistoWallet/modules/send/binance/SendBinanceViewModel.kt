package com.monistoWallet.modules.send.binance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.collectWith
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.AppLogger
import com.monistoWallet.core.HSCaution
import com.monistoWallet.core.ISendBinanceAdapter
import com.monistoWallet.core.LocalizedException
import com.monistoWallet.entities.Address
import com.monistoWallet.entities.Wallet
import com.monistoWallet.modules.amount.SendAmountService
import com.monistoWallet.modules.contacts.ContactsRepository
import com.monistoWallet.modules.send.SendConfirmationData
import com.monistoWallet.modules.send.SendResult
import com.monistoWallet.modules.xrate.XRateService
import com.monistoWallet.ui.compose.TranslatableString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.net.UnknownHostException

class SendBinanceViewModel(
    val wallet: Wallet,
    private val adapter: ISendBinanceAdapter,
    private val amountService: SendAmountService,
    private val addressService: SendBinanceAddressService,
    private val feeService: SendBinanceFeeService,
    private val xRateService: XRateService,
    private val contactsRepo: ContactsRepository,
    private val showAddressInput: Boolean,
) : ViewModel() {
    val blockchainType = wallet.token.blockchainType
    val feeToken by feeService::feeToken
    val feeTokenMaxAllowedDecimals = feeToken.decimals

    val coinMaxAllowedDecimals = wallet.token.decimals
    val fiatMaxAllowedDecimals = com.monistoWallet.core.App.appConfigProvider.fiatDecimal
    val memoMaxLength = 120

    private var amountState = amountService.stateFlow.value
    private var addressState = addressService.stateFlow.value
    private var feeState = feeService.stateFlow.value
    private var memo: String? = null

    var uiState by mutableStateOf(
        SendBinanceUiState(
            availableBalance = amountState.availableBalance,
            fee = feeState.fee,
            feeCaution = feeState.feeCaution,
            amountCaution = amountState.amountCaution,
            addressError = addressState.addressError,
            canBeSend = amountState.canBeSend && addressState.canBeSend && feeState.canBeSend,
            showAddressInput = showAddressInput,
        )
    )
        private set

    var coinRate by mutableStateOf(xRateService.getRate(wallet.coin.uid))
        private set
    var feeCoinRate by mutableStateOf(xRateService.getRate(feeToken.coin.uid))
        private set
    var sendResult by mutableStateOf<SendResult?>(null)
        private set

    private val logger = AppLogger("Send-${wallet.coin.code}")

    init {
        amountService.stateFlow.collectWith(viewModelScope) {
            handleUpdatedAmountState(it)
        }
        addressService.stateFlow.collectWith(viewModelScope) {
            handleUpdatedAddressState(it)
        }
        feeService.stateFlow.collectWith(viewModelScope) {
            handleUpdatedFeeState(it)
        }
        xRateService.getRateFlow(wallet.coin.uid).collectWith(viewModelScope) {
            coinRate = it
        }
        xRateService.getRateFlow(feeToken.coin.uid).collectWith(viewModelScope) {
            feeCoinRate = it
        }

        feeService.start()
    }

    fun onEnterAmount(amount: BigDecimal?) {
        amountService.setAmount(amount)
    }

    fun onEnterAddress(address: Address?) {
        addressService.setAddress(address)
    }

    fun onEnterMemo(memo: String) {
        this.memo = memo
    }

    private fun handleUpdatedAddressState(addressState: SendBinanceAddressService.State) {
        this.addressState = addressState

        emitState()
    }

    private fun handleUpdatedAmountState(amountState: SendAmountService.State) {
        this.amountState = amountState

        emitState()
    }

    private fun handleUpdatedFeeState(feeState: SendBinanceFeeService.State) {
        this.feeState = feeState

        emitState()
    }

    private fun emitState() {
        uiState = SendBinanceUiState(
            availableBalance = amountState.availableBalance,
            fee = adapter.fee,
            feeCaution = feeState.feeCaution,
            amountCaution = amountState.amountCaution,
            addressError = addressState.addressError,
            canBeSend = amountState.canBeSend && addressState.canBeSend && feeState.canBeSend,
            showAddressInput = showAddressInput,
        )
    }

    fun getConfirmationData(): com.monistoWallet.modules.send.SendConfirmationData {
        val address = addressState.address!!
        val contact = contactsRepo.getContactsFiltered(
            blockchainType,
            addressQuery = address.hex
        ).firstOrNull()
        return com.monistoWallet.modules.send.SendConfirmationData(
            amount = amountState.amount!!,
            fee = feeState.fee,
            address = address,
            contact = contact,
            coin = wallet.coin,
            feeCoin = feeToken.coin,
            memo = memo
        )
    }

    fun onClickSend() {
        viewModelScope.launch {
            send()
        }
    }

    private suspend fun send() = withContext(Dispatchers.IO) {
        val logger = logger.getScopedUnique()
        logger.info("click")

        try {
            sendResult = SendResult.Sending

            val send = adapter.send(
                amountState.amount!!,
                addressState.address!!.hex,
                memo,
                logger
            ).blockingGet()

            logger.info("success")
            sendResult = SendResult.Sent
        } catch (e: Throwable) {
            logger.warning("failed", e)
            sendResult = SendResult.Failed(createCaution(e))
        }
    }

    private fun createCaution(error: Throwable) = when (error) {
        is UnknownHostException -> HSCaution(TranslatableString.ResString(R.string.Hud_Text_NoInternet))
        is LocalizedException -> HSCaution(TranslatableString.ResString(error.errorTextRes))
        else -> HSCaution(TranslatableString.PlainString(error.message ?: ""))
    }

}

data class SendBinanceUiState(
    val availableBalance: BigDecimal,
    val fee: BigDecimal?,
    val feeCaution: HSCaution?,
    val addressError: Throwable?,
    val amountCaution: HSCaution?,
    val canBeSend: Boolean,
    val showAddressInput: Boolean,
)
