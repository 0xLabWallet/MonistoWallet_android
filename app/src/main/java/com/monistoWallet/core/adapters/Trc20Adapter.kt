package com.monistoWallet.core.adapters

import com.monistoWallet.core.AdapterState
import com.monistoWallet.core.BalanceData
import com.monistoWallet.core.ISendTronAdapter
import com.monistoWallet.core.managers.TronKitWrapper
import com.monistoWallet.entities.Wallet
import com.wallet0x.tronkit.TronKit.SyncState
import com.wallet0x.tronkit.models.Address
import com.wallet0x.tronkit.transaction.Fee
import io.reactivex.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asFlowable
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class Trc20Adapter(
    tronKitWrapper: TronKitWrapper,
    contractAddress: String,
    wallet: Wallet
) : BaseTronAdapter(tronKitWrapper, wallet.decimal), ISendTronAdapter {

    private val contractAddress: Address = Address.fromBase58(contractAddress)

    // IAdapter

    override fun start() {
        // started via TronKitManager
    }

    override fun stop() {
        // stopped via TronKitManager
    }

    override fun refresh() {
        // refreshed via TronKitManager
    }

    // IBalanceAdapter

    override val balanceState: AdapterState
        get() = convertToAdapterState(tronKit.syncState)

    override val balanceStateUpdatedFlowable: Flowable<Unit>
        get() = tronKit.syncStateFlow.map { }.asFlowable()

    override val balanceData: BalanceData
        get() = BalanceData(balanceInBigDecimal(tronKit.getTrc20Balance(contractAddress.base58), decimal))

    override val balanceUpdatedFlowable: Flowable<Unit>
        get() = tronKit.getTrc20BalanceFlow(contractAddress.base58).map { }.asFlowable()

    // ISendTronAdapter

    override val trxBalanceData: BalanceData
        get() = BalanceData(balanceInBigDecimal(tronKit.trxBalance, TronAdapter.decimal))

    override suspend fun estimateFee(amount: BigDecimal, to: Address): List<Fee> = withContext(Dispatchers.IO) {
        val amountBigInt = amount.movePointRight(decimal).toBigInteger()
        val contract = tronKit.transferTrc20TriggerSmartContract(contractAddress, to, amountBigInt)
        tronKit.estimateFee(contract)
    }

    override suspend fun send(amount: BigDecimal, to: Address, feeLimit: Long?) {
        if (signer == null) throw Exception()
        val amountBigInt = amount.movePointRight(decimal).toBigInteger()
        val contract = tronKit.transferTrc20TriggerSmartContract(contractAddress, to, amountBigInt)

        tronKit.send(contract, signer, feeLimit)
    }

    private fun convertToAdapterState(syncState: SyncState): AdapterState = when (syncState) {
        is SyncState.Synced -> AdapterState.Synced
        is SyncState.NotSynced -> AdapterState.NotSynced(syncState.error)
        is SyncState.Syncing -> AdapterState.Syncing()
    }

}
