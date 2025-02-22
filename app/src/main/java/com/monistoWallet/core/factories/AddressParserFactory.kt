package com.monistoWallet.core.factories

import com.monistoWallet.core.managers.EvmBlockchainManager
import com.wallet0x.marketkit.models.BlockchainType

val BlockchainType.uriScheme: String?
    get() {
        if (EvmBlockchainManager.blockchainTypes.contains(this)) {
            return "ethereum"
        }

        return when (this) {
            BlockchainType.Bitcoin -> "bitcoin"
            BlockchainType.BitcoinCash -> "bitcoincash"
            BlockchainType.ECash -> "ecash"
            BlockchainType.Litecoin -> "litecoin"
            BlockchainType.Dash -> "dash"
            BlockchainType.Zcash -> "zcash"
            BlockchainType.Ethereum -> "ethereum"
            BlockchainType.BinanceChain -> "binancecoin"
            BlockchainType.Ton -> "toncoin"
            BlockchainType.Tron -> "tron"
            else -> null
        }
    }

val BlockchainType.removeScheme: Boolean
    get() {
        if (EvmBlockchainManager.blockchainTypes.contains(this)) {
            return true
        }

        return when (this) {
            BlockchainType.Bitcoin,
            BlockchainType.Litecoin,
            BlockchainType.Dash,
            BlockchainType.Zcash,
            BlockchainType.Ethereum,
            BlockchainType.BinanceChain,
            BlockchainType.Ton,
            BlockchainType.Tron -> true

            else -> false
        }
    }