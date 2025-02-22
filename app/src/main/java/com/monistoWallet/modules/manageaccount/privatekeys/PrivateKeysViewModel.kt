package com.monistoWallet.modules.manageaccount.privatekeys

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.core.managers.EvmBlockchainManager
import com.monistoWallet.core.toRawHexString
import com.monistoWallet.entities.Account
import com.monistoWallet.entities.AccountType
import com.monistoWallet.modules.manageaccount.showextendedkey.ShowExtendedKeyModule
import com.wallet0x.ethereumkit.core.signer.Signer
import com.wallet0x.hdwalletkit.HDExtendedKey
import com.wallet0x.hdwalletkit.HDWallet
import com.wallet0x.hdwalletkit.Mnemonic
import com.wallet0x.marketkit.models.BlockchainType
import java.math.BigInteger

class PrivateKeysViewModel(
    account: Account,
    evmBlockchainManager: EvmBlockchainManager,
) : ViewModel() {

    var viewState by mutableStateOf(PrivateKeysModule.ViewState())
        private set

    init {

        val ethereumPrivateKey = when (val accountType = account.type) {
            is AccountType.Mnemonic -> {
                val chain = evmBlockchainManager.getChain(BlockchainType.Ethereum)
                toHexString(Signer.privateKey(accountType.words, accountType.passphrase, chain))
            }
            is AccountType.EvmPrivateKey -> toHexString(accountType.key)
            else -> null
        }

        val hdExtendedKey = (account.type as? AccountType.HdExtendedKey)?.hdExtendedKey

        val bip32RootKey = if (account.type is AccountType.Mnemonic) {
            val seed = Mnemonic().toSeed(account.type.words, account.type.passphrase)
            HDExtendedKey(seed, HDWallet.Purpose.BIP44)
        } else if (hdExtendedKey?.derivedType == HDExtendedKey.DerivedType.Master) {
            hdExtendedKey
        } else {
            null
        }

        var accountExtendedDisplayType = ShowExtendedKeyModule.DisplayKeyType.AccountPrivateKey(true)
        val accountExtendedPrivateKey = bip32RootKey
            ?: if (hdExtendedKey?.derivedType == HDExtendedKey.DerivedType.Account && !hdExtendedKey.isPublic) {
                accountExtendedDisplayType = ShowExtendedKeyModule.DisplayKeyType.AccountPrivateKey(false)
                hdExtendedKey
            } else {
                null
            }

        viewState = PrivateKeysModule.ViewState(
            evmPrivateKey = ethereumPrivateKey,
            bip32RootKey = bip32RootKey?.let {
                PrivateKeysModule.ExtendedKey(it, ShowExtendedKeyModule.DisplayKeyType.Bip32RootKey)
            },
            accountExtendedPrivateKey = accountExtendedPrivateKey?.let {
                PrivateKeysModule.ExtendedKey(it, accountExtendedDisplayType)
            }
        )
    }

    private fun toHexString(key: BigInteger): String {
        return key.toByteArray().let {
            if (it.size > 32) {
                it.copyOfRange(1, it.size)
            } else {
                it
            }.toRawHexString()
        }
    }
}
