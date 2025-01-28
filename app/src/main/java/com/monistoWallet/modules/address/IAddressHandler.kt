package com.monistoWallet.modules.address

import com.monistoWallet.core.adapters.zcash.ZcashAddressValidator
import com.monistoWallet.entities.Address
import com.monistoWallet.entities.BitcoinAddress
import com.wallet0x.tronkit.account.AddressHandler
import com.wallet0x.binancechainkit.helpers.Crypto
import com.wallet0x.bitcoincore.network.Network
import com.wallet0x.bitcoincore.utils.Base58AddressConverter
import com.wallet0x.bitcoincore.utils.CashAddressConverter
import com.wallet0x.bitcoincore.utils.SegwitAddressConverter
import com.wallet0x.ethereumkit.core.AddressValidator
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.TokenQuery
import com.wallet0x.marketkit.models.TokenType
import com.wallet0x.tonkit.TonKit
import org.web3j.ens.EnsResolver

interface IAddressHandler {
    val blockchainType: BlockchainType
    fun isSupported(value: String): Boolean
    fun parseAddress(value: String): Address
}

class AddressHandlerEns(
    override val blockchainType: BlockchainType,
    private val ensResolver: EnsResolver
) : IAddressHandler {
    private val cache = mutableMapOf<String, Address>()

    override fun isSupported(value: String): Boolean {
        if (!EnsResolver.isValidEnsName(value)) return false

        try {
            cache[value] = Address(ensResolver.resolve(value), value, blockchainType)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun parseAddress(value: String): Address {
        return cache[value]!!
    }
}

class AddressHandlerEvm(override val blockchainType: BlockchainType) : IAddressHandler {

    override fun isSupported(value: String) = try {
        AddressValidator.validate(value)
        true
    } catch (e: AddressValidator.AddressValidationException) {
        false
    }

    override fun parseAddress(value: String): Address {
        val evmAddress = com.wallet0x.ethereumkit.models.Address(value)
        return Address(evmAddress.hex, blockchainType = blockchainType)
    }

}

class AddressHandlerBase58(network: Network, override val blockchainType: BlockchainType) : IAddressHandler {
    private val converter = Base58AddressConverter(network.addressVersion, network.addressScriptVersion)

    override fun isSupported(value: String) = try {
        converter.convert(value)
        true
    } catch (e: Throwable) {
        false
    }

    override fun parseAddress(value: String): Address {
        val address = converter.convert(value)
        return BitcoinAddress(hex = address.stringValue, domain = null, blockchainType = blockchainType, scriptType = address.scriptType)
    }
}

class AddressHandlerBech32(network: Network, override val blockchainType: BlockchainType) : IAddressHandler {
    private val converter = SegwitAddressConverter(network.addressSegwitHrp)

    override fun isSupported(value: String) = try {
        converter.convert(value)
        true
    } catch (e: Throwable) {
        false
    }

    override fun parseAddress(value: String): Address {
        val address = converter.convert(value)
        return BitcoinAddress(hex = address.stringValue, domain = null, blockchainType = blockchainType, scriptType = address.scriptType)
    }
}

class AddressHandlerBitcoinCash(network: Network, override val blockchainType: BlockchainType) : IAddressHandler {
    private val converter = CashAddressConverter(network.addressSegwitHrp)

    override fun isSupported(value: String) = try {
        converter.convert(value)
        true
    } catch (e: Throwable) {
        false
    }

    override fun parseAddress(value: String): Address {
        val address = converter.convert(value)
        return BitcoinAddress(hex = address.stringValue, domain = null, blockchainType = blockchainType, scriptType = address.scriptType)
    }
}

class AddressHandlerBinanceChain : IAddressHandler {
    override val blockchainType = BlockchainType.BinanceChain
    override fun isSupported(value: String) = try {
        Crypto.decodeAddress(value)
        true
    } catch (e: Throwable) {
        false
    }

    override fun parseAddress(value: String): Address {
        Crypto.decodeAddress(value)
        return Address(value, blockchainType = blockchainType)
    }
}

class AddressHandlerSolana : IAddressHandler {
    override fun isSupported(value: String): Boolean {
        return try {
            com.wallet0x.solanakit.models.Address(value)
            true
        } catch (e: Throwable) {
            false
        }
    }

    override val blockchainType = BlockchainType.Solana

    override fun parseAddress(value: String): Address {
        try {
            //simulate steps in Solana kit init
            com.wallet0x.solanakit.models.Address(value)
        } catch (e: Throwable) {
            throw AddressValidator.AddressValidationException(e.message ?: "")
        }

        return Address(value, blockchainType = blockchainType)
    }

}

class AddressHandlerZcash : IAddressHandler {
    override val blockchainType = BlockchainType.Zcash

    override fun isSupported(value: String): Boolean {
        return ZcashAddressValidator.validate(value)
    }

    override fun parseAddress(value: String): Address {
        return Address(value, blockchainType = blockchainType)
    }

}

class AddressHandlerTron : IAddressHandler {
    override val blockchainType = BlockchainType.Tron

    override fun isSupported(value: String) = try {
        com.wallet0x.tronkit.models.Address.fromBase58(value)
        true
    } catch (e: AddressHandler.AddressValidationException) {
        false
    } catch (e: IllegalArgumentException) {
        false
    }

    override fun parseAddress(value: String): Address {
        val tronAddress = com.wallet0x.tronkit.models.Address.fromBase58(value)
        return Address(tronAddress.base58, blockchainType = blockchainType)
    }
}

class AddressHandlerTon : IAddressHandler {
    override val blockchainType = BlockchainType.Ton

    override fun isSupported(value: String) = try {
        TonKit.validate(value)
        true
    } catch (e: Exception) {
        false
    }

    override fun parseAddress(value: String): Address {
        return Address(value, blockchainType = blockchainType)
    }
}

class AddressHandlerPure(override val blockchainType: BlockchainType) : IAddressHandler {

    override fun isSupported(value: String) = true

    override fun parseAddress(value: String) = Address(value, blockchainType = blockchainType)

}