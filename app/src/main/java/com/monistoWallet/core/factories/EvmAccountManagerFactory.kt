package com.monistoWallet.core.factories

import com.monistoWallet.core.IAccountManager
import com.monistoWallet.core.IWalletManager
import com.monistoWallet.core.managers.EvmAccountManager
import com.monistoWallet.core.managers.EvmKitManager
import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.core.managers.TokenAutoEnableManager
import com.wallet0x.marketkit.models.BlockchainType

class EvmAccountManagerFactory(
    private val accountManager: IAccountManager,
    private val walletManager: IWalletManager,
    private val marketKit: MarketKitWrapper,
    private val tokenAutoEnableManager: TokenAutoEnableManager
) {

    fun evmAccountManager(blockchainType: BlockchainType, evmKitManager: EvmKitManager) =
        EvmAccountManager(
            blockchainType,
            accountManager,
            walletManager,
            marketKit,
            evmKitManager,
            tokenAutoEnableManager
        )

}
