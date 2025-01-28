package com.monistoWallet.modules.evmfee.legacy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.ethereum.EvmCoinService
import com.monistoWallet.core.feePriceScale
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.DataState
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.evmfee.FeeSummaryViewItem
import com.monistoWallet.modules.evmfee.FeeViewItem
import com.monistoWallet.modules.evmfee.GasPriceInfo
import com.monistoWallet.modules.evmfee.IEvmFeeService
import com.monistoWallet.modules.evmfee.Transaction
import com.monistoWallet.modules.fee.FeeItem
import io.reactivex.disposables.CompositeDisposable

class LegacyFeeSettingsViewModel(
    private val gasPriceService: LegacyGasPriceService,
    private val feeService: IEvmFeeService,
    private val coinService: EvmCoinService
) : ViewModel() {

    private val scale = coinService.token.blockchainType.feePriceScale
    private val disposable = CompositeDisposable()

    var feeSummaryViewItem by mutableStateOf<FeeSummaryViewItem?>(null)
        private set

    var feeViewItem by mutableStateOf<FeeViewItem?>(null)
        private set

    init {
        sync(gasPriceService.state)
        gasPriceService.stateObservable
            .subscribeIO {
                sync(it)
            }.let {
                disposable.add(it)
            }

        syncTransactionStatus(feeService.transactionStatus)
        feeService.transactionStatusObservable
            .subscribe { transactionStatus ->
                syncTransactionStatus(transactionStatus)
            }
            .let {
                disposable.add(it)
            }
    }

    fun onSelectGasPrice(gasPrice: Long) {
        gasPriceService.setGasPrice(gasPrice)
    }

    fun onIncrementGasPrice(currentWeiValue: Long) {
        gasPriceService.setGasPrice(currentWeiValue + scale.scaleValue)
    }

    fun onDecrementGasPrice(currentWeiValue: Long) {
        gasPriceService.setGasPrice((currentWeiValue - scale.scaleValue).coerceAtLeast(0))
    }

    private fun syncTransactionStatus(transactionStatus: DataState<Transaction>) {
        syncFeeViewItems(transactionStatus)
    }

    private fun syncFeeViewItems(transactionStatus: DataState<Transaction>) {
        val notAvailable = Translator.getString(R.string.NotAvailable)
        when (transactionStatus) {
            DataState.Loading -> {
                feeSummaryViewItem = FeeSummaryViewItem(null, notAvailable, ViewState.Loading)
            }
            is DataState.Error -> {
                feeSummaryViewItem = FeeSummaryViewItem(null, notAvailable, ViewState.Error(transactionStatus.error))
            }
            is DataState.Success -> {
                val transaction = transactionStatus.data
                val viewState = transaction.errors.firstOrNull()?.let { ViewState.Error(it) } ?: ViewState.Success
                val feeAmountData = coinService.amountData(transactionStatus.data.gasData.estimatedFee, transactionStatus.data.gasData.isSurcharged)
                val feeItem = FeeItem(feeAmountData.primary.getFormattedPlain(), feeAmountData.secondary?.getFormattedPlain())
                val gasLimit = com.monistoWallet.core.App.numberFormatter.format(transactionStatus.data.gasData.gasLimit.toBigDecimal(), 0, 0)

                feeSummaryViewItem = FeeSummaryViewItem(feeItem, gasLimit, viewState)
            }
        }
    }

    private fun sync(state: DataState<GasPriceInfo>) {
        if (state is DataState.Success) {
            feeViewItem = FeeViewItem(weiValue = state.data.gasPrice.max, scale = scale, warnings = state.data.warnings, errors = state.data.errors)
        }
    }

}
