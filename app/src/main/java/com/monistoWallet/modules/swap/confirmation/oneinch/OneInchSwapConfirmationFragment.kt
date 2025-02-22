package com.monistoWallet.modules.swap.confirmation.oneinch

import androidx.core.os.bundleOf
import androidx.navigation.navGraphViewModels
import com.monistoWallet.R
import com.monistoWallet.core.AppLogger
import com.monistoWallet.modules.evmfee.EvmFeeCellViewModel
import com.monistoWallet.modules.send.evm.settings.SendEvmNonceViewModel
import com.monistoWallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import com.monistoWallet.modules.swap.SwapMainModule.OneInchSwapParameters
import com.monistoWallet.modules.swap.confirmation.BaseSwapConfirmationFragment
import com.monistoWallet.core.parcelable
import com.wallet0x.marketkit.models.BlockchainType

class OneInchSwapConfirmationFragment(
    override val navGraphId: Int = R.id.oneInchConfirmationFragment
) : BaseSwapConfirmationFragment() {

    companion object {
        private const val blockchainTypeKey = "blockchainTypeKey"
        private const val oneInchSwapParametersKey = "oneInchSwapParametersKey"

        fun prepareParams(
            blockchainType: BlockchainType,
            oneInchSwapParameters: OneInchSwapParameters,
            swapEntryPointDestId: Int,
        ) = bundleOf(
            blockchainTypeKey to blockchainType,
            oneInchSwapParametersKey to oneInchSwapParameters,
            swapEntryPointDestIdKey to swapEntryPointDestId
        )
    }

    private val blockchainType by lazy {
        requireArguments().parcelable<BlockchainType>(blockchainTypeKey)!!
    }

    private val oneInchSwapParameters by lazy {
        requireArguments().parcelable<OneInchSwapParameters>(oneInchSwapParametersKey)!!
    }

    override val logger = AppLogger("swap_1inch")

    private val vmFactory by lazy {
        OneInchConfirmationModule.Factory(blockchainType, oneInchSwapParameters)
    }
    override val sendEvmTransactionViewModel by navGraphViewModels<SendEvmTransactionViewModel>(navGraphId) { vmFactory }
    override val feeViewModel by navGraphViewModels<EvmFeeCellViewModel>(navGraphId) { vmFactory }
    override val nonceViewModel by navGraphViewModels<SendEvmNonceViewModel>(navGraphId) { vmFactory }
}
