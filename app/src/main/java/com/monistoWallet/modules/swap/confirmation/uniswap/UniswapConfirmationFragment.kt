package com.monistoWallet.modules.swap.confirmation.uniswap

import androidx.core.os.bundleOf
import androidx.navigation.navGraphViewModels
import com.monistoWallet.R
import com.monistoWallet.core.AppLogger
import com.monistoWallet.modules.evmfee.EvmFeeCellViewModel
import com.monistoWallet.modules.send.evm.SendEvmData
import com.monistoWallet.modules.send.evm.SendEvmModule
import com.monistoWallet.modules.send.evm.settings.SendEvmNonceViewModel
import com.monistoWallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import com.monistoWallet.modules.swap.SwapMainModule
import com.monistoWallet.modules.swap.confirmation.BaseSwapConfirmationFragment
import com.monistoWallet.core.parcelable
import com.wallet0x.ethereumkit.models.Address
import com.wallet0x.ethereumkit.models.TransactionData

class UniswapConfirmationFragment(
    override val navGraphId: Int = R.id.uniswapConfirmationFragment
) : BaseSwapConfirmationFragment() {

    companion object {
        private const val transactionDataKey = "transactionDataKey"
        private const val dexKey = "dexKey"
        private const val additionalInfoKey = "additionalInfoKey"

        fun prepareParams(
            dex: SwapMainModule.Dex,
            transactionData: SendEvmModule.TransactionDataParcelable,
            additionalInfo: SendEvmData.AdditionalInfo?,
            swapEntryPointDestId: Int
        ) = bundleOf(
            dexKey to dex,
            transactionDataKey to transactionData,
            additionalInfoKey to additionalInfo,
            swapEntryPointDestIdKey to swapEntryPointDestId
        )
    }

    private val dex by lazy {
        requireArguments().parcelable<SwapMainModule.Dex>(dexKey)!!
    }

    private val transactionData by lazy {
        val transactionDataParcelable = requireArguments().parcelable<SendEvmModule.TransactionDataParcelable>(transactionDataKey)!!
        TransactionData(
            Address(transactionDataParcelable.toAddress),
            transactionDataParcelable.value,
            transactionDataParcelable.input
        )
    }

    private val additionalInfo by lazy {
        requireArguments().parcelable<SendEvmData.AdditionalInfo>(additionalInfoKey)
    }

    override val logger = AppLogger("swap_uniswap")

    private val vmFactory by lazy {
        UniswapConfirmationModule.Factory(
            dex,
            transactionData,
            additionalInfo
        )
    }
    override val sendEvmTransactionViewModel by navGraphViewModels<SendEvmTransactionViewModel>(navGraphId) { vmFactory }
    override val feeViewModel by navGraphViewModels<EvmFeeCellViewModel>(navGraphId) { vmFactory }
    override val nonceViewModel by navGraphViewModels<SendEvmNonceViewModel>(navGraphId) { vmFactory }

}
