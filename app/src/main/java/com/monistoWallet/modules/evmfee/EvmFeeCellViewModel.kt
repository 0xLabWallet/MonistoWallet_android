package com.monistoWallet.modules.evmfee

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.monistoWallet.core.ethereum.EvmCoinService
import com.monistoWallet.entities.DataState
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.fee.FeeItem
import io.reactivex.disposables.CompositeDisposable

class EvmFeeCellViewModel(
    val feeService: IEvmFeeService,
    val gasPriceService: IEvmGasPriceService,
    val coinService: EvmCoinService
) : ViewModel() {

    private val disposable = CompositeDisposable()

    val feeLiveData = MutableLiveData<FeeItem?>()
    val viewStateLiveData = MutableLiveData<ViewState>()

    init {
        syncTransactionStatus(feeService.transactionStatus)
        feeService.transactionStatusObservable
            .subscribe { syncTransactionStatus(it) }
            .let { disposable.add(it) }
    }

    override fun onCleared() {
        disposable.clear()
    }

    private fun syncTransactionStatus(transactionStatus: DataState<Transaction>) {
        when (transactionStatus) {
            DataState.Loading -> {}
            is DataState.Error -> {
                viewStateLiveData.postValue(ViewState.Error(transactionStatus.error))
                feeLiveData.postValue(null)
            }
            is DataState.Success -> {
                val transaction = transactionStatus.data

                if (transaction.errors.isNotEmpty()) {
                    viewStateLiveData.postValue(ViewState.Error(transaction.errors.first()))
                } else {
                    viewStateLiveData.postValue(ViewState.Success)
                }

                val feeAmountData = coinService.amountData(transactionStatus.data.gasData.estimatedFee, transactionStatus.data.gasData.isSurcharged)
                val feeViewItem = FeeItem(
                    primary = feeAmountData.primary.getFormattedPlain(),
                    secondary = feeAmountData.secondary?.getFormattedPlain()
                )
                feeLiveData.postValue(feeViewItem)
            }
        }
    }
}
