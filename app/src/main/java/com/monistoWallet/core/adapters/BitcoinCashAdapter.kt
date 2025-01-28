package com.monistoWallet.core.adapters

import com.monistoWallet.core.App
import com.monistoWallet.core.ISendBitcoinAdapter
import com.monistoWallet.core.UnsupportedAccountException
import com.monistoWallet.core.kitCoinType
import com.monistoWallet.entities.Wallet
import com.monistoWallet.entities.transactionrecords.TransactionRecord
import com.wallet0x.bitcoincash.BitcoinCashKit
import com.wallet0x.bitcoincash.BitcoinCashKit.NetworkType
import com.wallet0x.bitcoincash.MainNetBitcoinCash
import com.wallet0x.bitcoincore.BitcoinCore
import com.wallet0x.bitcoincore.models.BalanceInfo
import com.wallet0x.bitcoincore.models.BlockInfo
import com.wallet0x.bitcoincore.models.TransactionInfo
import com.monistoWallet.core.BackgroundManager
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.TokenType
import java.math.BigDecimal

class BitcoinCashAdapter(
    override val kit: BitcoinCashKit,
    syncMode: BitcoinCore.SyncMode,
    backgroundManager: BackgroundManager,
    wallet: Wallet,
) : BitcoinBaseAdapter(kit, syncMode, backgroundManager, wallet, confirmationsThreshold), BitcoinCashKit.Listener, ISendBitcoinAdapter {

    constructor(
        wallet: Wallet,
        syncMode: BitcoinCore.SyncMode,
        backgroundManager: BackgroundManager,
        addressType: TokenType.AddressType
    ) : this(createKit(wallet, syncMode, addressType), syncMode, backgroundManager, wallet)

    init {
        kit.listener = this
    }

    //
    // BitcoinBaseAdapter
    //

    override val satoshisInBitcoin: BigDecimal = BigDecimal.valueOf(Math.pow(10.0, decimal.toDouble()))

    //
    // BitcoinCashKit Listener
    //

    override val explorerTitle: String
        get() = "btc.com"

    override fun getTransactionUrl(transactionHash: String): String =
        "https://bch.btc.com/$transactionHash"

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

    override val blockchainType = BlockchainType.BitcoinCash


    companion object {
        private const val confirmationsThreshold = 3

        private fun createKit(
            wallet: Wallet,
            syncMode: BitcoinCore.SyncMode,
            addressType: TokenType.AddressType
        ): BitcoinCashKit {
            val account = wallet.account
            val networkType = getNetworkType(addressType.kitCoinType)
            when (val accountType = account.type) {
                is com.monistoWallet.entities.AccountType.HdExtendedKey -> {
                    return BitcoinCashKit(
                        context = App.instance,
                        extendedKey = accountType.hdExtendedKey,
                        walletId = account.id,
                        syncMode = syncMode,
                        networkType = networkType,
                        confirmationsThreshold = confirmationsThreshold
                    )
                }

                is com.monistoWallet.entities.AccountType.Mnemonic -> {
                    return BitcoinCashKit(
                        context = App.instance,
                        words = accountType.words,
                        passphrase = accountType.passphrase,
                        walletId = account.id,
                        syncMode = syncMode,
                        networkType = networkType,
                        confirmationsThreshold = confirmationsThreshold
                    )
                }

                is com.monistoWallet.entities.AccountType.BitcoinAddress -> {
                    return BitcoinCashKit(
                        context = App.instance,
                        watchAddress = accountType.address,
                        walletId = account.id,
                        syncMode = syncMode,
                        networkType = networkType,
                        confirmationsThreshold = confirmationsThreshold,
                    )
                }

                else -> throw UnsupportedAccountException()
            }

        }

        fun clear(walletId: String) {
            BitcoinCashKit.clear(App.instance, getNetworkType(), walletId)
        }

        private fun getNetworkType(kitCoinType: MainNetBitcoinCash.CoinType = MainNetBitcoinCash.CoinType.Type145) =
            NetworkType.MainNet(kitCoinType)
    }
}
