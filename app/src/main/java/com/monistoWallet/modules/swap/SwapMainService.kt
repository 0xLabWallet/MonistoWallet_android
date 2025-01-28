package com.monistoWallet.modules.swap

import com.monistoWallet.core.ILocalStorage
import com.monistoWallet.modules.swap.SwapMainModule.Dex
import com.monistoWallet.modules.swap.SwapMainModule.ISwapProvider
import com.wallet0x.marketkit.models.Blockchain
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.Token
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SwapMainService(
    tokenFrom: Token?,
    private val providers: List<ISwapProvider>,
    private val localStorage: ILocalStorage
) {
    var dex: Dex = getDex(tokenFrom)
        private set

    private val _providerUpdatedFlow =
        MutableSharedFlow<ISwapProvider>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val providerUpdatedFlow = _providerUpdatedFlow.asSharedFlow()

    val availableProviders: List<ISwapProvider>
        get() = providers.filter { it.supports(dex.blockchainType) }.sortedBy { it.title }

    fun setProvider(provider: ISwapProvider) {
        if (dex.provider.id != provider.id) {
            dex = Dex(dex.blockchain, provider)
            _providerUpdatedFlow.tryEmit(provider)

            localStorage.setSwapProviderId(dex.blockchainType, provider.id)
        }
    }

    private fun getDex(tokenFrom: Token?): Dex {
        val blockchain = getBlockchainForToken(tokenFrom)
        val provider = getSwapProvider(blockchain.type) ?: throw IllegalStateException("No provider found for ${blockchain.name}")

        return Dex(blockchain, provider)
    }

    private fun getSwapProvider(blockchainType: BlockchainType): ISwapProvider? {
        val providerId = localStorage.getSwapProviderId(blockchainType)
            ?: SwapMainModule.OneInchProvider.id

        val data = providers.firstOrNull { it.id == providerId }
        return data
    }

    private fun getBlockchainForToken(token: Token?) = when (token?.blockchainType) {
        BlockchainType.Ethereum,
        BlockchainType.BinanceSmartChain,
        BlockchainType.Polygon,
        BlockchainType.Avalanche,
        BlockchainType.Optimism,
        BlockchainType.Gnosis,
        BlockchainType.Fantom,
        BlockchainType.ArbitrumOne -> token.blockchain
        null -> Blockchain(BlockchainType.Ethereum, "Ethereum", null) // todo: find better solution
        else -> throw IllegalStateException("Swap not supported for ${token.blockchainType}")
    }

}
