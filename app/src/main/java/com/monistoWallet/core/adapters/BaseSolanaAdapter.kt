package com.monistoWallet.core.adapters

import com.monistoWallet.core.IAdapter
import com.monistoWallet.core.IBalanceAdapter
import com.monistoWallet.core.IReceiveAdapter
import com.monistoWallet.core.managers.SolanaKitWrapper
import com.wallet0x.solanakit.Signer

abstract class BaseSolanaAdapter(
        solanaKitWrapper: SolanaKitWrapper,
        val decimal: Int
) : IAdapter, IBalanceAdapter, IReceiveAdapter {

    val solanaKit = solanaKitWrapper.solanaKit
    protected val signer: Signer? = solanaKitWrapper.signer

    override val debugInfo: String
        get() = solanaKit.debugInfo()

    val statusInfo: Map<String, Any>
        get() = solanaKit.statusInfo()

    // IReceiveAdapter

    override val receiveAddress: String
        get() = solanaKit.receiveAddress

    override val isMainNet: Boolean
        get() = solanaKit.isMainnet

    companion object {
        const val confirmationsThreshold: Int = 12
    }

}
