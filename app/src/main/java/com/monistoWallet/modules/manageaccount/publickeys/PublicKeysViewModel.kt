package com.monistoWallet.modules.manageaccount.publickeys

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.core.managers.EvmBlockchainManager
import com.monistoWallet.entities.Account
import com.monistoWallet.entities.AccountType
import com.monistoWallet.modules.manageaccount.publickeys.PublicKeysModule.ExtendedPublicKey
import com.monistoWallet.modules.manageaccount.showextendedkey.ShowExtendedKeyModule.DisplayKeyType.AccountPublicKey
import com.wallet0x.ethereumkit.core.signer.Signer
import com.wallet0x.hdwalletkit.HDExtendedKey
import com.wallet0x.hdwalletkit.HDWallet
import com.wallet0x.hdwalletkit.Mnemonic
import com.wallet0x.marketkit.models.BlockchainType

class PublicKeysViewModel(
    account: Account,
    evmBlockchainManager: EvmBlockchainManager,
) : ViewModel() {

    var viewState by mutableStateOf(PublicKeysModule.ViewState())
        private set

    init {
        val evmAddress: String? = when (val accountType = account.type) {
            is AccountType.Mnemonic -> {
                val chain = evmBlockchainManager.getChain(BlockchainType.Ethereum)
                Signer.address(accountType.words, accountType.passphrase, chain).eip55
            }
            is AccountType.EvmPrivateKey -> {
                Signer.address(accountType.key).eip55
            }
            is AccountType.EvmAddress -> accountType.address
            is AccountType.SolanaAddress -> accountType.address
            is AccountType.TronAddress -> accountType.address
            else -> null
        }

        val hdExtendedKey = (account.type as? AccountType.HdExtendedKey)?.hdExtendedKey
        var accountPublicKey = AccountPublicKey(false)

        val publicKey = if (account.type is AccountType.Mnemonic) {
            accountPublicKey = AccountPublicKey(true)
            val seed = Mnemonic().toSeed(account.type.words, account.type.passphrase)
            HDExtendedKey(seed, HDWallet.Purpose.BIP44)
        } else if (hdExtendedKey?.derivedType == HDExtendedKey.DerivedType.Master) {
            accountPublicKey = AccountPublicKey(true)
            hdExtendedKey
        } else if (hdExtendedKey?.derivedType == HDExtendedKey.DerivedType.Account && !hdExtendedKey.isPublic) {
            hdExtendedKey
        } else if (hdExtendedKey?.derivedType == HDExtendedKey.DerivedType.Account && hdExtendedKey.isPublic) {
            hdExtendedKey
        } else {
            null
        }

        viewState = PublicKeysModule.ViewState(
            evmAddress = evmAddress,
            extendedPublicKey = publicKey?.let { ExtendedPublicKey(it, accountPublicKey) }
        )
    }

}
