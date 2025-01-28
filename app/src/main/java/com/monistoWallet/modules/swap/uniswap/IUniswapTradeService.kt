package com.monistoWallet.modules.swap.uniswap

import com.monistoWallet.modules.swap.SwapMainModule
import com.monistoWallet.modules.swap.UniversalSwapTradeData
import com.monistoWallet.modules.swap.settings.uniswap.SwapTradeOptions
import com.wallet0x.ethereumkit.models.TransactionData

interface IUniswapTradeService : SwapMainModule.ISwapTradeService {
    var tradeOptions: SwapTradeOptions
    @Throws
    fun transactionData(tradeData: UniversalSwapTradeData): TransactionData
}