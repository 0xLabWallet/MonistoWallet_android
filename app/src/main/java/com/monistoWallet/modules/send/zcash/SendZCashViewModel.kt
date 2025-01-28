package com.monistoWallet.modules.send.zcash

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
import com.monistoWallet.core.ISendZcashAdapter
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

class SendZCashViewModel(
    private val adapter: ISendZcashAdapter,
    val wallet: Wallet,
    private val xRateService: XRateService,
    private val amountService: SendAmountService,
    private val addressService: SendZCashAddressService,
    private val memoService: SendZCashMemoService,
    private val contactsRepo: ContactsRepository,
    private val showAddressInput: Boolean
) : ViewModel() {
    val blockchainType = wallet.token.blockchainType
    val coinMaxAllowedDecimals = wallet.token.decimals
    val fiatMaxAllowedDecimals = com.monistoWallet.core.App.appConfigProvider.fiatDecimal
    val memoMaxLength by memoService::memoMaxLength

    private val fee = adapter.fee
    private var amountState = amountService.stateFlow.value
    private var addressState = addressService.stateFlow.value
    private var memoState = memoService.stateFlow.value

    var uiState by mutableStateOf(
        SendZCashUiState(
            fee = fee,
            availableBalance = amountState.availableBalance,
            addressError = addressState.addressError,
            amountCaution = amountState.amountCaution,
            memoIsAllowed = memoState.memoIsAllowed,
            canBeSend = amountState.canBeSend && addressState.canBeSend,
            showAddressInput = showAddressInput,
        )
    )
        private set

    var coinRate by mutableStateOf(xRateService.getRate(wallet.coin.uid))
        private set
    var sendResult by mutableStateOf<SendResult?>(null)
        private set

    private val logger = AppLogger("Send-${wallet.coin.code}")

    init {
        xRateService.getRateFlow(wallet.coin.uid).collectWith(viewModelScope) {
            coinRate = it
        }
        amountService.stateFlow.collectWith(viewModelScope) {
            handleUpdatedAmountState(it)
        }
        addressService.stateFlow.collectWith(viewModelScope) {
            handleUpdatedAddressState(it)
        }
        memoService.stateFlow.collectWith(viewModelScope) {
            handleUpdatedMemoState(it)
        }
    }

    fun onEnterAmount(amount: BigDecimal?) {
        amountService.setAmount(amount)
    }

    fun onEnterAddress(address: Address?) {
        viewModelScope.launch {
            addressService.setAddress(address)
        }
    }

    fun onEnterMemo(memo: String) {
        memoService.setMemo(memo)
    }

    private fun handleUpdatedAmountState(amountState: SendAmountService.State) {
        this.amountState = amountState

        emitState()
    }

    private fun handleUpdatedAddressState(addressState: SendZCashAddressService.State) {
        this.addressState = addressState

        memoService.setAddressType(addressState.addressType)

        emitState()
    }

    private fun handleUpdatedMemoState(memoState: SendZCashMemoService.State) {
        this.memoState = memoState

        emitState()
    }

    private fun emitState() {
        uiState = SendZCashUiState(
            availableBalance = amountState.availableBalance,
            fee = fee,
            addressError = addressState.addressError,
            amountCaution = amountState.amountCaution,
            memoIsAllowed = memoState.memoIsAllowed,
            canBeSend = amountState.canBeSend && addressState.canBeSend,
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
            fee = fee,
            address = address,
            contact = contact,
            coin = wallet.coin,
            feeCoin = wallet.coin,
            memo = memoState.memo
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
                memoState.memo,
                logger
            )

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

data class SendZCashUiState(
    val fee: BigDecimal,
    val availableBalance: BigDecimal,
    val addressError: Throwable?,
    val amountCaution: HSCaution?,
    val memoIsAllowed: Boolean,
    val canBeSend: Boolean,
    val showAddressInput: Boolean,
)
