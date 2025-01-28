package com.monistoWallet.core.managers

import com.monistoWallet.core.IAccountCleaner
import com.monistoWallet.core.adapters.BinanceAdapter
import com.monistoWallet.core.adapters.BitcoinAdapter
import com.monistoWallet.core.adapters.BitcoinCashAdapter
import com.monistoWallet.core.adapters.DashAdapter
import com.monistoWallet.core.adapters.ECashAdapter
import com.monistoWallet.core.adapters.Eip20Adapter
import com.monistoWallet.core.adapters.EvmAdapter
import com.monistoWallet.core.adapters.SolanaAdapter
import com.monistoWallet.core.adapters.TronAdapter
import com.monistoWallet.core.adapters.zcash.ZcashAdapter

class AccountCleaner : IAccountCleaner {

    override fun clearAccounts(accountIds: List<String>) {
        accountIds.forEach { clearAccount(it) }
    }

    private fun clearAccount(accountId: String) {
        BinanceAdapter.clear(accountId)
        BitcoinAdapter.clear(accountId)
        BitcoinCashAdapter.clear(accountId)
        ECashAdapter.clear(accountId)
        DashAdapter.clear(accountId)
        EvmAdapter.clear(accountId)
        Eip20Adapter.clear(accountId)
        ZcashAdapter.clear(accountId)
        SolanaAdapter.clear(accountId)
        TronAdapter.clear(accountId)
    }

}
