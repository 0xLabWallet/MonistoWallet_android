package com.monistoWallet.core.adapters

import com.monistoWallet.core.IAdapter
import com.monistoWallet.core.IBalanceAdapter
import com.monistoWallet.core.IReceiveAdapter
import com.monistoWallet.core.managers.TronKitWrapper
import com.wallet0x.tronkit.models.Address
import com.wallet0x.tronkit.network.Network
import com.wallet0x.tronkit.transaction.Signer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.BigInteger

abstract class BaseTronAdapter(
    tronKitWrapper: TronKitWrapper,
    val decimal: Int
) : IAdapter, IBalanceAdapter, IReceiveAdapter {

    val tronKit = tronKitWrapper.tronKit
    protected val signer: Signer? = tronKitWrapper.signer

    override val debugInfo: String
        get() = ""

    val statusInfo: Map<String, Any>
        get() = tronKit.statusInfo()

    // IReceiveAdapter

    override val isAccountActive: Boolean
        get() = tronKit.isAccountActive

    override val receiveAddress: String
        get() = tronKit.address.base58

    override val isMainNet: Boolean
        get() = tronKit.network == Network.Mainnet

    suspend fun isAddressActive(address: Address): Boolean = withContext(Dispatchers.IO) {
        tronKit.isAccountActive(address)
    }

    fun isOwnAddress(address: Address): Boolean {
        return address == tronKit.address
    }

    protected fun balanceInBigDecimal(balance: BigInteger?, decimal: Int): BigDecimal {
        balance?.toBigDecimal()?.let {
            return scaleDown(it, decimal)
        } ?: return BigDecimal.ZERO
    }

    protected fun scaleDown(amount: BigDecimal, decimals: Int = decimal): BigDecimal {
        return amount.movePointLeft(decimals).stripTrailingZeros()
    }

    protected fun scaleUp(amount: BigDecimal, decimals: Int = decimal): BigInteger {
        return amount.movePointRight(decimals).toBigInteger()
    }

    companion object {
        const val confirmationsThreshold: Int = 19
    }

}
