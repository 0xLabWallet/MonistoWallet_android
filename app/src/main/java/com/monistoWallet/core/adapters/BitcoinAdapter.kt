package com.monistoWallet.core.adapters

import com.monistoWallet.core.App
import com.monistoWallet.core.ISendBitcoinAdapter
import com.monistoWallet.core.UnsupportedAccountException
import com.monistoWallet.core.purpose
import com.monistoWallet.entities.Wallet
import com.monistoWallet.entities.transactionrecords.TransactionRecord
import com.wallet0x.bitcoincore.BitcoinCore
import com.wallet0x.bitcoincore.models.BalanceInfo
import com.wallet0x.bitcoincore.models.BlockInfo
import com.wallet0x.bitcoincore.models.TransactionInfo
import com.wallet0x.bitcoinkit.BitcoinKit
import com.wallet0x.bitcoinkit.BitcoinKit.NetworkType
import com.monistoWallet.core.BackgroundManager
import com.monistoWallet.entities.AccountType
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.TokenType
import java.math.BigDecimal

class BitcoinAdapter(
    override val kit: BitcoinKit,
    syncMode: BitcoinCore.SyncMode,
    backgroundManager: BackgroundManager,
    wallet: Wallet,
) : BitcoinBaseAdapter(kit, syncMode, backgroundManager, wallet, confirmationsThreshold), BitcoinKit.Listener, ISendBitcoinAdapter {

    constructor(
        wallet: Wallet,
        syncMode: BitcoinCore.SyncMode,
        backgroundManager: BackgroundManager,
        derivation: TokenType.Derivation
    ) : this(
        createKit(wallet, syncMode, derivation),
        syncMode,
        backgroundManager,
        wallet
    )

    init {
        kit.listener = this
    }

    //
    // BitcoinBaseAdapter
    //

    override val satoshisInBitcoin: BigDecimal = BigDecimal.valueOf(Math.pow(10.0, decimal.toDouble()))

    //
    // BitcoinKit Listener
    //

    override val explorerTitle: String
        get() = "blockchair.com"


    override fun getTransactionUrl(transactionHash: String): String =
        "https://blockchair.com/bitcoin/transaction/$transactionHash"

    override fun onBalanceUpdate(balance: BalanceInfo) {
        balanceUpdatedSubject.onNext(Unit)
    }

    override fun onLastBlockInfoUpdate(blockInfo: BlockInfo) {
        lastBlockUpdatedSubject.onNext(Unit)
    }

    override fun onKitStateUpdate(state: BitcoinCore.KitState) {
        setState(state)
    }

    override fun onTransactionsUpdate(inserted: List<TransactionInfo>, updated: List<TransactionInfo>) {
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

    override val blockchainType = BlockchainType.Bitcoin


    companion object {
        private const val confirmationsThreshold = 3

        private fun createKit(
            wallet: Wallet,
            syncMode: BitcoinCore.SyncMode,
            derivation: TokenType.Derivation
        ): BitcoinKit {
            val account = wallet.account

            when (val accountType = account.type) {
                is AccountType.HdExtendedKey -> {
                    return BitcoinKit(
                        context = App.instance,
                        extendedKey = accountType.hdExtendedKey,
                        purpose = derivation.purpose,
                        walletId = account.id,
                        syncMode = syncMode,
                        networkType = NetworkType.MainNet,
                        confirmationsThreshold = confirmationsThreshold
                    )
                }
                is AccountType.Mnemonic -> {
                    return BitcoinKit(
                        context = App.instance,
                        words = accountType.words,
                        passphrase = accountType.passphrase,
                        walletId = account.id,
                        syncMode = syncMode,
                        networkType = NetworkType.MainNet,
                        confirmationsThreshold = confirmationsThreshold,
                        purpose = derivation.purpose
                    )
                }
                is com.monistoWallet.entities.AccountType.BitcoinAddress -> {
                    return BitcoinKit(
                        context = App.instance,
                        watchAddress =  accountType.address,
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
            BitcoinKit.clear(App.instance, NetworkType.MainNet, walletId)
        }
    }
}
