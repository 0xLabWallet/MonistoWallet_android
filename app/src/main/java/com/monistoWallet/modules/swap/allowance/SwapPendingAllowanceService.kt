package com.monistoWallet.modules.swap.allowance

import com.monistoWallet.core.IAdapterManager
import com.monistoWallet.core.adapters.Eip20Adapter
import com.monistoWallet.entities.transactionrecords.evm.ApproveTransactionRecord
import com.monistoWallet.modules.swap.allowance.SwapPendingAllowanceState.Approved
import com.monistoWallet.modules.swap.allowance.SwapPendingAllowanceState.Approving
import com.monistoWallet.modules.swap.allowance.SwapPendingAllowanceState.NA
import com.monistoWallet.modules.swap.allowance.SwapPendingAllowanceState.Revoked
import com.monistoWallet.modules.swap.allowance.SwapPendingAllowanceState.Revoking
import com.wallet0x.marketkit.models.Token
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.concurrent.Executors

enum class SwapPendingAllowanceState {
    NA, Revoking, Revoked, Approving, Approved;

    fun loading() = this == Revoking || this == Approving
}

class SwapPendingAllowanceService(
    private val adapterManager: IAdapterManager,
    private val allowanceService: SwapAllowanceService
) {
    private var token: Token? = null
    private var pendingAllowance: BigDecimal? = null

    private val singleDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val coroutineScope = CoroutineScope(singleDispatcher)

    private val stateSubject = PublishSubject.create<SwapPendingAllowanceState>()
    var state: SwapPendingAllowanceState = NA
        private set(value) {
            if (field != value) {
                field = value
                stateSubject.onNext(value)
            }
        }
    val stateObservable: Observable<SwapPendingAllowanceState> = stateSubject

    init {
        coroutineScope.launch {
            allowanceService.stateFlow
                .collect { sync() }
        }
    }

    fun set(token: Token?) {
        this.token = token
        pendingAllowance = null

        syncAllowance()
    }

    fun syncAllowance() {
        val coin = token ?: return
        val adapter = adapterManager.getAdapterForToken(coin) as? Eip20Adapter ?: return

        adapter.pendingTransactions.forEach { transaction ->
            if (transaction is ApproveTransactionRecord) {
                pendingAllowance = transaction.value.decimalValue
            }
        }

        sync()
    }

    fun onCleared() {
        coroutineScope.cancel()
    }

    private fun sync() {
        val pendingAllowance = pendingAllowance
        val allowanceState = allowanceService.state

        if (pendingAllowance == null || allowanceState == null || allowanceState !is SwapAllowanceService.State.Ready) {
            state = NA
            return
        }

        val pendingAllowanceConfirmed = allowanceState.allowance.value.compareTo(pendingAllowance) == 0

        state = if (pendingAllowance.compareTo(BigDecimal.ZERO) == 0) {
            when {
                pendingAllowanceConfirmed -> Revoked
                else -> Revoking
            }
        } else {
            when {
                pendingAllowanceConfirmed -> Approved
                else -> Approving
            }
        }
    }

}
