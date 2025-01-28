package com.monistoWallet.modules.swap.confirmation.oneinch

import com.monistoWallet.core.AppLogger
import com.monistoWallet.core.Clearable
import com.monistoWallet.core.managers.EvmKitWrapper
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.DataState
import com.monistoWallet.modules.send.evm.SendEvmData
import com.monistoWallet.modules.send.evm.settings.SendEvmSettingsService
import com.monistoWallet.modules.sendevmtransaction.ISendEvmTransactionService
import com.monistoWallet.modules.sendevmtransaction.SendEvmTransactionService
import com.monistoWallet.modules.swap.SwapMainModule.OneInchSwapParameters
import com.monistoWallet.modules.swap.SwapViewItemHelper
import com.wallet0x.ethereumkit.models.Address
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.RoundingMode

class OneInchSendEvmTransactionService(
    private val evmKitWrapper: EvmKitWrapper,
    private val feeService: OneInchFeeService,
    override val settingsService: SendEvmSettingsService,
    private val helper: SwapViewItemHelper
) : ISendEvmTransactionService, Clearable {

    private val evmKit = evmKitWrapper.evmKit

    private val disposable = CompositeDisposable()

    private val stateSubject = PublishSubject.create<SendEvmTransactionService.State>()
    override var state: SendEvmTransactionService.State = SendEvmTransactionService.State.NotReady()
        private set(value) {
            field = value
            stateSubject.onNext(value)
        }
    override val stateObservable: Flowable<SendEvmTransactionService.State> =
        stateSubject.toFlowable(BackpressureStrategy.BUFFER)

    override var txDataState: SendEvmTransactionService.TxDataState = SendEvmTransactionService.TxDataState(
        null,
        getAdditionalInfo(feeService.parameters),
        null
    )
        private set

    private val sendStateSubject = PublishSubject.create<SendEvmTransactionService.SendState>()
    override var sendState: SendEvmTransactionService.SendState = SendEvmTransactionService.SendState.Idle
        private set(value) {
            field = value
            sendStateSubject.onNext(value)
        }
    override val sendStateObservable: Flowable<SendEvmTransactionService.SendState> =
        sendStateSubject.toFlowable(BackpressureStrategy.BUFFER)

    override val ownAddress: Address
        get() = evmKit.receiveAddress


    override suspend fun start() = withContext(Dispatchers.IO) {
        launch {
            settingsService.stateFlow
                .collect {
                    sync(it)
                }
        }

        settingsService.start()
    }

    private fun sync(settingsState: DataState<SendEvmSettingsService.Transaction>) {
        when (settingsState) {
            is DataState.Error -> {
                state = SendEvmTransactionService.State.NotReady(errors = listOf(settingsState.error))
            }
            DataState.Loading -> {
                state = SendEvmTransactionService.State.NotReady()
            }
            is DataState.Success -> {
                val transaction = settingsState.data
                syncTxDataState(transaction)

                state = if (transaction.errors.isNotEmpty()) {
                    SendEvmTransactionService.State.NotReady(transaction.warnings, transaction.errors)
                } else {
                    SendEvmTransactionService.State.Ready(transaction.warnings)
                }
            }
        }
    }

    private fun syncTxDataState(transaction: SendEvmSettingsService.Transaction) {
        val transactionData = transaction.transactionData
        val decoration = evmKit.decorate(transactionData)
        val additionalInfo = getAdditionalInfo(feeService.parameters)

        txDataState = SendEvmTransactionService.TxDataState(
            transactionData,
            additionalInfo,
            decoration
        )
    }

    private fun getAdditionalInfo(parameters: OneInchSwapParameters): SendEvmData.AdditionalInfo {
        return parameters.let {
            val sellPrice = it.amountTo.divide(it.amountFrom, it.tokenFrom.decimals, RoundingMode.HALF_EVEN).stripTrailingZeros()
            val buyPrice = it.amountFrom.divide(it.amountTo, it.tokenTo.decimals, RoundingMode.HALF_EVEN).stripTrailingZeros()
            val (primaryPrice, _) = helper.prices(sellPrice, buyPrice, it.tokenFrom, it.tokenTo)
            val swapInfo = SendEvmData.OneInchSwapInfo(
                tokenFrom = it.tokenFrom,
                tokenTo = it.tokenTo,
                amountFrom = it.amountFrom,
                estimatedAmountTo = it.amountTo,
                slippage = it.slippage,
                recipient = it.recipient,
                price = primaryPrice
            )
            SendEvmData.AdditionalInfo.OneInchSwap(swapInfo)
        }
    }

    override fun send(logger: AppLogger) {
        if (state !is SendEvmTransactionService.State.Ready) {
            logger.info("state is not Ready: ${state.javaClass.simpleName}")
            return
        }
        val txConfig = settingsService.state.dataOrNull ?: return

        sendState = SendEvmTransactionService.SendState.Sending
        logger.info("sending tx")

        evmKitWrapper.sendSingle(
            txConfig.transactionData,
            txConfig.gasData.gasPrice,
            txConfig.gasData.gasLimit,
            txConfig.nonce
        )
            .subscribeIO({ fullTransaction ->
                sendState = SendEvmTransactionService.SendState.Sent(fullTransaction.transaction.hash)
                logger.info("success")
            }, { error ->
                sendState = SendEvmTransactionService.SendState.Failed(error)
                logger.warning("failed", error)
            })
            .let { disposable.add(it) }
    }

    override fun methodName(input: ByteArray): String? = null

    override fun clear() {
        disposable.clear()
        settingsService.clear()
    }
}
