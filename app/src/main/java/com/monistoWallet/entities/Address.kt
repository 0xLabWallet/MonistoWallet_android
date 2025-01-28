package com.monistoWallet.entities

import android.os.Parcelable
import com.wallet0x.bitcoincore.core.purpose
import com.wallet0x.bitcoincore.transactions.scripts.ScriptType
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.TokenType
import com.wallet0x.hdwalletkit.HDWallet
import kotlinx.parcelize.Parcelize

@Parcelize
open class Address(
    val hex: String,
    val domain: String? = null,
    val blockchainType: BlockchainType? = null,
) : Parcelable {
    val title: String
        get() = domain ?: hex
}

class BitcoinAddress(
    hex: String,
    domain: String?,
    blockchainType: BlockchainType?,
    val scriptType: ScriptType
) : Address(hex, domain, blockchainType)

private val ScriptType.derivation: TokenType.Derivation
    get() = when (this.purpose!!) {
        HDWallet.Purpose.BIP44 -> TokenType.Derivation.Bip44
        HDWallet.Purpose.BIP49 -> TokenType.Derivation.Bip49
        HDWallet.Purpose.BIP84 -> TokenType.Derivation.Bip84
        HDWallet.Purpose.BIP86 -> TokenType.Derivation.Bip86
    }

val BitcoinAddress.tokenType: TokenType
    get() = when (this.blockchainType) {
        BlockchainType.Bitcoin -> TokenType.Derived(this.scriptType.derivation)
        BlockchainType.BitcoinCash -> TokenType.AddressTyped(TokenType.AddressType.Type145)
        BlockchainType.ECash -> TokenType.Native
        BlockchainType.Litecoin -> TokenType.Derived(this.scriptType.derivation)
        BlockchainType.Dash -> TokenType.Native
        BlockchainType.Dexnet,
        BlockchainType.Zcash,
        BlockchainType.Ethereum,
        BlockchainType.BinanceSmartChain,
        BlockchainType.BinanceChain,
        BlockchainType.Polygon,
        BlockchainType.Avalanche,
        BlockchainType.Optimism,
        BlockchainType.ArbitrumOne,
        BlockchainType.Solana,
        BlockchainType.Gnosis,
        BlockchainType.Fantom,
        BlockchainType.Tron,
        BlockchainType.Ton,
        is BlockchainType.Unsupported,
        null -> TokenType.Unsupported("", "")
    }
