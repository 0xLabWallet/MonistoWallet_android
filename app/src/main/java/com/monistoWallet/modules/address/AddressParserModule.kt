package com.monistoWallet.modules.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.utils.AddressUriParser
import com.wallet0x.marketkit.models.Token
import java.math.BigDecimal

object AddressParserModule {
    class Factory(private val token: Token, private val prefilledAmount: BigDecimal?) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddressParserViewModel(AddressUriParser(token.blockchainType, token.type), prefilledAmount) as T
        }
    }
}
