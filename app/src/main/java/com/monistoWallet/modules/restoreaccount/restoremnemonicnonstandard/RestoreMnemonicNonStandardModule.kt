package com.monistoWallet.modules.restoreaccount.restoremnemonicnonstandard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.entities.AccountType
import com.monistoWallet.modules.restoreaccount.restoremnemonic.RestoreMnemonicModule
import com.wallet0x.hdwalletkit.Language

object RestoreMnemonicNonStandardModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RestoreMnemonicNonStandardViewModel(
                App.accountFactory,
                App.wordsManager,
                App.thirdKeyboardStorage,
            ) as T
        }
    }

    data class UiState(
        val passphraseEnabled: Boolean,
        val passphraseError: String?,
        val invalidWordRanges: List<IntRange>,
        val error: String?,
        val accountType: AccountType?,
        val wordSuggestions: RestoreMnemonicModule.WordSuggestions?,
        val language: Language,
    )
}
