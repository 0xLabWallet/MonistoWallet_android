package com.monistoWallet.modules.coin.majorholders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.coin.CoinViewFactory
import com.monistoWallet.modules.coin.MajorHolderItem
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.StackBarSlice
import com.wallet0x.marketkit.models.Blockchain

object CoinMajorHoldersModule {
    class Factory(private val coinUid: String, private val blockchain: Blockchain) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val factory = CoinViewFactory(com.monistoWallet.core.App.currencyManager.baseCurrency, com.monistoWallet.core.App.numberFormatter)
            return CoinMajorHoldersViewModel(coinUid, blockchain, com.monistoWallet.core.App.marketKit, factory) as T
        }
    }

    data class UiState(
        val viewState: ViewState,
        val top10Share: String = "",
        val totalHoldersCount: String = "",
        val seeAllUrl: String? = null,
        val chartData: List<StackBarSlice> = emptyList(),
        val topHolders: List<MajorHolderItem> = emptyList(),
        val error: TranslatableString? = null,
    )
}
