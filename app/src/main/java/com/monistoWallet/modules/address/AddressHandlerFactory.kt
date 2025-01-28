package com.monistoWallet.modules.address

import com.monistoWallet.core.supported
import com.wallet0x.bitcoincash.MainNetBitcoinCash
import com.wallet0x.bitcoinkit.MainNet
import com.wallet0x.dashkit.MainNetDash
import com.wallet0x.ecash.MainNetECash
import com.wallet0x.litecoinkit.MainNetLitecoin
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.TokenQuery
import com.wallet0x.marketkit.models.TokenType

class AddressHandlerFactory {

    private fun parserChainHandlers(blockchainType: BlockchainType): List<IAddressHandler> {
        val addressHandlers = mutableListOf<IAddressHandler>()
        when (blockchainType) {
            BlockchainType.Bitcoin -> {
                val network = MainNet()
                addressHandlers.add(
                    AddressHandlerBase58(
                        network,
                        blockchainType
                    )
                )
                addressHandlers.add(
                    AddressHandlerBech32(
                        network,
                        blockchainType
                    )
                )
            }

            BlockchainType.BitcoinCash -> {
                val network = MainNetBitcoinCash()
                addressHandlers.add(
                    AddressHandlerBase58(
                        network,
                        blockchainType
                    )
                )
                addressHandlers.add(
                    AddressHandlerBitcoinCash(
                        network,
                        blockchainType
                    )
                )
            }

            BlockchainType.ECash -> {
                val network = MainNetECash()
                addressHandlers.add(
                    AddressHandlerBase58(
                        network,
                        blockchainType
                    )
                )
                addressHandlers.add(
                    AddressHandlerBitcoinCash(
                        network,
                        blockchainType
                    )
                )
            }

            BlockchainType.Litecoin -> {
                val network = MainNetLitecoin()
                addressHandlers.add(
                    AddressHandlerBase58(
                        network,
                        blockchainType
                    )
                )
                addressHandlers.add(
                    AddressHandlerBech32(
                        network,
                        blockchainType
                    )
                )
            }

            BlockchainType.Dash -> {
                val network = MainNetDash()
                addressHandlers.add(
                    AddressHandlerBase58(
                        network,
                        blockchainType
                    )
                )
            }

            BlockchainType.BinanceChain -> {
                addressHandlers.add(AddressHandlerBinanceChain())
            }

            BlockchainType.Zcash -> {
                addressHandlers.add(AddressHandlerZcash())
            }

            BlockchainType.Ethereum,
            BlockchainType.Dexnet,
            BlockchainType.BinanceSmartChain,
            BlockchainType.Polygon,
            BlockchainType.Avalanche,
            BlockchainType.Optimism,
            BlockchainType.Gnosis,
            BlockchainType.Fantom,
            BlockchainType.ArbitrumOne -> {
                addressHandlers.add(
                    AddressHandlerEvm(
                        blockchainType
                    )
                )
            }

            BlockchainType.Solana -> {
                addressHandlers.add(AddressHandlerSolana())
            }

            BlockchainType.Tron -> {
                addressHandlers.add(AddressHandlerTron())
            }

            BlockchainType.Ton -> {
                addressHandlers.add(AddressHandlerTon())
            }

            is BlockchainType.Unsupported -> {
            }
        }
        return addressHandlers
    }

    private fun domainHandlers(blockchainType: BlockchainType): List<IAddressHandler> {
        val domainAddressHandlers = mutableListOf<IAddressHandler>()
        when (blockchainType) {
            BlockchainType.Ethereum,
            BlockchainType.BinanceSmartChain,
            BlockchainType.Polygon,
            BlockchainType.Dexnet,
            BlockchainType.Avalanche,
            BlockchainType.Optimism,
            BlockchainType.Gnosis,
            BlockchainType.Fantom,
            BlockchainType.ArbitrumOne -> {
                domainAddressHandlers.add(
                    AddressHandlerEns(
                        blockchainType,
                        EnsResolverHolder.resolver
                    )
                )
            }

            else -> {}
        }
        return domainAddressHandlers
    }

    fun parserChain(blockchainType: BlockchainType?, withEns: Boolean = false): AddressParserChain {
        val addressHandlers = mutableListOf<IAddressHandler>()
        val domainHandlers = mutableListOf<IAddressHandler>()

        blockchainType?.let {
            addressHandlers.addAll(parserChainHandlers(it))
            if (withEns) {
                domainHandlers.addAll(domainHandlers(it))
            }
        } ?: run {
            BlockchainType.supported.forEach {
                addressHandlers.addAll(parserChainHandlers(it))
                if (withEns) {
                    domainHandlers.addAll(domainHandlers(it))
                }
            }
        }

        return AddressParserChain(addressHandlers, domainHandlers)
    }

    fun parserChain(blockchainTypes: List<BlockchainType>, blockchainTypesWithEns: List<BlockchainType>): AddressParserChain {
        val addressHandlers = mutableListOf<IAddressHandler>()
        val domainHandlers = mutableListOf<IAddressHandler>()

        for (blockchainType in blockchainTypes) {
            addressHandlers.addAll(parserChainHandlers(blockchainType))
        }

        for (blockchainType in blockchainTypesWithEns) {
            domainHandlers.addAll(domainHandlers(blockchainType))
        }

        return AddressParserChain(addressHandlers, domainHandlers)
    }

}