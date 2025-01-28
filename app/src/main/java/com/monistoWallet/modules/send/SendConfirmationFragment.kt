package com.monistoWallet.modules.send

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.modules.amount.AmountInputModeViewModel
import com.monistoWallet.modules.send.binance.SendBinanceConfirmationScreen
import com.monistoWallet.modules.send.binance.SendBinanceViewModel
import com.monistoWallet.modules.send.bitcoin.SendBitcoinConfirmationScreen
import com.monistoWallet.modules.send.bitcoin.SendBitcoinViewModel
import com.monistoWallet.modules.send.solana.SendSolanaConfirmationScreen
import com.monistoWallet.modules.send.solana.SendSolanaViewModel
import com.monistoWallet.modules.send.ton.SendTonConfirmationScreen
import com.monistoWallet.modules.send.ton.SendTonViewModel
import com.monistoWallet.modules.send.tron.SendTronConfirmationScreen
import com.monistoWallet.modules.send.tron.SendTronViewModel
import com.monistoWallet.modules.send.zcash.SendZCashConfirmationScreen
import com.monistoWallet.modules.send.zcash.SendZCashViewModel
import com.monistoWallet.core.parcelable
import kotlinx.parcelize.Parcelize

class SendConfirmationFragment : BaseComposeFragment() {
    val amountInputModeViewModel by navGraphViewModels<AmountInputModeViewModel>(R.id.sendXFragment)

    @Composable
    override fun GetContent(navController: NavController) {
        val arguments = requireArguments()
        val sendEntryPointDestId = arguments.getInt(com.monistoWallet.modules.send.SendConfirmationFragment.Companion.sendEntryPointDestIdKey)

        when (arguments.parcelable<com.monistoWallet.modules.send.SendConfirmationFragment.Type>(
            com.monistoWallet.modules.send.SendConfirmationFragment.Companion.typeKey
        )) {
            com.monistoWallet.modules.send.SendConfirmationFragment.Type.Bitcoin -> {
                val sendBitcoinViewModel by navGraphViewModels<SendBitcoinViewModel>(R.id.sendXFragment)

                SendBitcoinConfirmationScreen(
                    navController,
                    sendBitcoinViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId
                )
            }
            com.monistoWallet.modules.send.SendConfirmationFragment.Type.Bep2 -> {
                val sendBinanceViewModel by navGraphViewModels<SendBinanceViewModel>(R.id.sendXFragment)

                SendBinanceConfirmationScreen(
                    navController,
                    sendBinanceViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId
                )
            }
            com.monistoWallet.modules.send.SendConfirmationFragment.Type.ZCash -> {
                val sendZCashViewModel by navGraphViewModels<SendZCashViewModel>(R.id.sendXFragment)

                SendZCashConfirmationScreen(
                    navController,
                    sendZCashViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId
                )
            }
            com.monistoWallet.modules.send.SendConfirmationFragment.Type.Tron -> {
                val sendTronViewModel by navGraphViewModels<SendTronViewModel>(R.id.sendXFragment)
                SendTronConfirmationScreen(
                    navController,
                    sendTronViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId
                )
            }
            com.monistoWallet.modules.send.SendConfirmationFragment.Type.Solana -> {
                val sendSolanaViewModel by navGraphViewModels<SendSolanaViewModel>(R.id.sendXFragment)

                SendSolanaConfirmationScreen(
                    navController,
                    sendSolanaViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId
                )
            }
            com.monistoWallet.modules.send.SendConfirmationFragment.Type.Ton -> {
                val sendTonViewModel by navGraphViewModels<SendTonViewModel>(R.id.sendXFragment)

                SendTonConfirmationScreen(
                    navController,
                    sendTonViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId
                )
            }
            null -> Unit
        }
    }

    @Parcelize
    enum class Type : Parcelable {
        Bitcoin, Bep2, ZCash, Solana, Tron, Ton
    }

    companion object {
        private const val typeKey = "typeKey"
        private const val sendEntryPointDestIdKey = "sendEntryPointDestIdKey"

        fun prepareParams(type: com.monistoWallet.modules.send.SendConfirmationFragment.Type, sendEntryPointDestId: Int) = bundleOf(
            com.monistoWallet.modules.send.SendConfirmationFragment.Companion.typeKey to type,
            com.monistoWallet.modules.send.SendConfirmationFragment.Companion.sendEntryPointDestIdKey to sendEntryPointDestId,
        )
    }
}
