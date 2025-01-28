package com.monistoWallet.modules.createaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.managers.PassphraseValidator
import com.monistoWallet.core.providers.PredefinedBlockchainSettingsProvider
import com.monistoWallet.core.providers.Translator

object CreateAccountModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CreateAccountViewModel(
                com.monistoWallet.core.App.accountFactory,
                com.monistoWallet.core.App.wordsManager,
                com.monistoWallet.core.App.accountManager,
                com.monistoWallet.core.App.walletActivator,
                PassphraseValidator(),
                PredefinedBlockchainSettingsProvider(
                    com.monistoWallet.core.App.restoreSettingsManager,
                    com.monistoWallet.core.App.zcashBirthdayProvider
                )
            ) as T
        }
    }

    enum class Kind(val wordsCount: Int) {
        Mnemonic12(12),
        Mnemonic15(15),
        Mnemonic18(18),
        Mnemonic21(21),
        Mnemonic24(24);

        val title = Translator.getString(R.string.CreateWallet_N_Words, wordsCount)

        val titleLong: String
            get() = if (this == Mnemonic12) Translator.getString(R.string.CreateWallet_N_WordsRecommended, wordsCount)
            else title
    }
}
