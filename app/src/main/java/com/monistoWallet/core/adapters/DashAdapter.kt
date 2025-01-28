package com.monistoWallet.core.adapters

import com.monistoWallet.core.App
import com.monistoWallet.core.ISendBitcoinAdapter
import com.monistoWallet.core.UnsupportedAccountException
import com.monistoWallet.entities.Wallet
import com.monistoWallet.entities.transactionrecords.TransactionRecord
import com.wallet0x.bitcoincore.BitcoinCore
import com.wallet0x.bitcoincore.models.BalanceInfo
import com.wallet0x.bitcoincore.models.BlockInfo
import com.monistoWallet.core.BackgroundManager
import com.wallet0x.dashkit.DashKit
import com.wallet0x.dashkit.DashKit.NetworkType
import com.wallet0x.dashkit.models.DashTransactionInfo
import com.wallet0x.marketkit.models.BlockchainType
import java.math.BigDecimal

class DashAdapter(
    override val kit: DashKit,
    syncMode: BitcoinCore.SyncMode,
    backgroundManager: BackgroundManager,
    wallet: Wallet,
) : BitcoinBaseAdapter(kit, syncMode, backgroundManager, wallet, confirmationsThreshold), DashKit.Listener, ISendBitcoinAdapter {

    constructor(wallet: Wallet, syncMode: BitcoinCore.SyncMode, backgroundManager: BackgroundManager) :
            this(createKit(wallet, syncMode), syncMode, backgroundManager, wallet)

    init {
        kit.listener = this
    }

    //
    // BitcoinBaseAdapter
    //

    override val satoshisInBitcoin: BigDecimal = BigDecimal.valueOf(Math.pow(10.0, decimal.toDouble()))

    //
    // DashKit Listener
    //

    override val explorerTitle: String
        get() = "dash.org"

    override fun getTransactionUrl(transactionHash: String): String =
        "https://insight.dash.org/insight/tx/$transactionHash"

    override fun onBalanceUpdate(balance: BalanceInfo) {
        balanceUpdatedSubject.onNext(Unit)
    }

    override fun onLastBlockInfoUpdate(blockInfo: BlockInfo) {
        lastBlockUpdatedSubject.onNext(Unit)
    }

    override fun onKitStateUpdate(state: BitcoinCore.KitState) {
        setState(state)
    }

    override fun onTransactionsUpdate(inserted: List<DashTransactionInfo>, updated: List<DashTransactionInfo>) {
        val records = mutableListOf<TransactionRecord>()

        for (info in inserted) {
            records.add(transactionRecord(info))
        }

        for (info in updated) {
            records.add(transactionRecord(info))
        }

        transactionRecordsSubject.onNext(records)
    }

    override fun onTransactionsDelete(hashes: List<String>) {
        // ignored for now
    }

    override val blockchainType = BlockchainType.Dash

    companion object {
        private const val confirmationsThreshold = 3

        private fun createKit(wallet: Wallet, syncMode: BitcoinCore.SyncMode): DashKit {
            val account = wallet.account

            when (val accountType = account.type) {
                is com.monistoWallet.entities.AccountType.HdExtendedKey -> {
                    return DashKit(
                        context = com.monistoWallet.core.App.instance,
                        extendedKey = accountType.hdExtendedKey,
                        walletId = account.id,
                        syncMode = syncMode,
                        networkType = NetworkType.MainNet,
                        confirmationsThreshold = confirmationsThreshold
                    )
                }
                is com.monistoWallet.entities.AccountType.Mnemonic -> {
                    return DashKit(
                        context = com.monistoWallet.core.App.instance,
                        words = accountType.words,
                        passphrase = accountType.passphrase,
                        walletId = account.id,
                        syncMode = syncMode,
                        networkType = NetworkType.MainNet,
                        confirmationsThreshold = confirmationsThreshold
                    )
                }
                is com.monistoWallet.entities.AccountType.BitcoinAddress -> {
                    return DashKit(
                        context = com.monistoWallet.core.App.instance,
                        watchAddress = accountType.address,
                        walletId = account.id,
                        syncMode = syncMode,
                        networkType = NetworkType.MainNet,
                        confirmationsThreshold = confirmationsThreshold
                    )
                }
                else -> throw UnsupportedAccountException()
            }
        }

        fun clear(walletId: String) {
            DashKit.clear(com.monistoWallet.core.App.instance, NetworkType.MainNet, walletId)
        }
    }
}
