package com.monistoWallet.modules.restoreaccount.restoreprivatekey

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.R
import com.monistoWallet.core.IAccountFactory
import com.monistoWallet.core.hexToByteArray
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.entities.AccountType
import com.monistoWallet.entities.DataState
import com.monistoWallet.modules.restoreaccount.restoreprivatekey.RestorePrivateKeyModule.RestoreError.EmptyText
import com.monistoWallet.modules.restoreaccount.restoreprivatekey.RestorePrivateKeyModule.RestoreError.NoValidKey
import com.monistoWallet.modules.restoreaccount.restoreprivatekey.RestorePrivateKeyModule.RestoreError.NonPrivateKey
import com.monistoWallet.modules.restoreaccount.restoreprivatekey.RestorePrivateKeyModule.RestoreError.NotSupportedDerivedType
import com.wallet0x.ethereumkit.core.signer.Signer
import com.wallet0x.hdwalletkit.HDExtendedKey
import java.math.BigInteger

class RestorePrivateKeyViewModel(
    accountFactory: IAccountFactory,
) : ViewModel() {

    val defaultName = accountFactory.getNextAccountName()
    var accountName: String = defaultName
        get() = field.ifBlank { defaultName }
        private set

    private var text = ""

    var inputState by mutableStateOf<DataState.Error?>(null)
        private set

    fun onEnterName(name: String) {
        accountName = name
    }

    fun onEnterPrivateKey(input: String) {
        inputState = null
        text = input
    }

    fun resolveAccountType(): AccountType? {
        inputState = null
        return try {
            accountType(text)
        } catch (e: Exception) {
            inputState = DataState.Error(
                Exception(Translator.getString(R.string.Restore_PrivateKey_InvalidKey))
            )
            null
        }
    }

    @Throws(Exception::class)
    private fun accountType(text: String): AccountType {
        val textCleaned = text.trim()

        if (textCleaned.isEmpty()) {
            throw EmptyText
        }

        if (isValidEthereumPrivateKey(textCleaned)) {
            val privateKey = Signer.privateKey(textCleaned)
            return AccountType.EvmPrivateKey(privateKey)
        }

        try {
            val extendedKey = HDExtendedKey(textCleaned)
            if (extendedKey.isPublic) {
                throw NonPrivateKey
            }
            when (extendedKey.derivedType) {
                HDExtendedKey.DerivedType.Master,
                HDExtendedKey.DerivedType.Account -> {
                    return AccountType.HdExtendedKey(extendedKey.serializePrivate())
                }

                else -> throw NotSupportedDerivedType
            }
        } catch (e: Throwable) {
            throw NoValidKey
        }
    }

    private fun isValidEthereumPrivateKey(privateKeyHex: String): Boolean {
        try {
            //key should be 32 bytes long
            privateKeyHex.hexToByteArray().let {
                if (it.size != 32) {
                    return false
                }
            }

            // Convert the hex private key to a BigInteger
            val privateKeyBigInt = BigInteger(privateKeyHex, 16)

            // Define the order of the secp256k1 curve (n)
            val secp256k1Order = BigInteger(
                "fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141",
                16
            )

            // Check if the private key is greater than zero and less than the order
            return privateKeyBigInt > BigInteger.ZERO && privateKeyBigInt < secp256k1Order
        } catch (e: NumberFormatException) {
            return false
        }
    }
}
