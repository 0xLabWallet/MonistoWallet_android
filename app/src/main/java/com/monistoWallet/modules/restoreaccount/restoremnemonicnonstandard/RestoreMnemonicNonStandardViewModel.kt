package com.monistoWallet.modules.restoreaccount.restoremnemonicnonstandard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.R
import com.monistoWallet.core.IAccountFactory
import com.monistoWallet.core.managers.WordsManager
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.entities.AccountType
import com.monistoWallet.modules.restoreaccount.restoremnemonic.RestoreMnemonicModule
import com.monistoWallet.modules.restoreaccount.restoremnemonicnonstandard.RestoreMnemonicNonStandardModule.UiState
import com.monistoWallet.core.CoreApp
import com.monistoWallet.core.IThirdKeyboard
import com.wallet0x.hdwalletkit.Language
import com.wallet0x.hdwalletkit.Mnemonic
import com.wallet0x.hdwalletkit.WordList

class RestoreMnemonicNonStandardViewModel(
    accountFactory: IAccountFactory,
    private val wordsManager: WordsManager,
    private val thirdKeyboardStorage: IThirdKeyboard,
) : ViewModel() {

    val mnemonicLanguages = Language.values().toList()

    private var passphraseEnabled: Boolean = false
    private var passphrase: String = ""
    private var passphraseError: String? = null
    private var wordItems: List<RestoreMnemonicModule.WordItem> = listOf()
    private var invalidWordItems: List<RestoreMnemonicModule.WordItem> = listOf()
    private var invalidWordRanges: List<IntRange> = listOf()
    private var error: String? = null
    private var accountType: AccountType? = null
    private var wordSuggestions: RestoreMnemonicModule.WordSuggestions? = null
    private var language = Language.English
    private var text = ""
    private var cursorPosition = 0
    private var mnemonicWordList = WordList.wordList(language)

    var uiState by mutableStateOf(
        UiState(
            passphraseEnabled = passphraseEnabled,
            passphraseError = passphraseError,
            invalidWordRanges = invalidWordRanges,
            error = error,
            accountType = accountType,
            wordSuggestions = wordSuggestions,
            language = language,
        )
    )
        private set

    private val regex = Regex("\\S+")

    val defaultName = accountFactory.getNextAccountName()
    var accountName: String = defaultName
        get() = field.ifBlank { defaultName }
        private set

    val isThirdPartyKeyboardAllowed: Boolean
        get() = CoreApp.thirdKeyboardStorage.isThirdPartyKeyboardAllowed

    private fun emitState() {
        uiState = UiState(
            passphraseEnabled = passphraseEnabled,
            passphraseError = passphraseError,
            invalidWordRanges = invalidWordRanges,
            error = error,
            accountType = accountType,
            wordSuggestions = wordSuggestions,
            language = language,
        )
    }

    private fun processText() {
        wordItems = wordItems(text)
        invalidWordItems = wordItems.filter { !mnemonicWordList.validWord(it.word, false) }

        val wordItemWithCursor = wordItems.find {
            it.range.contains(cursorPosition - 1)
        }

        val invalidWordItemsExcludingCursoredPartiallyValid = when {
            wordItemWithCursor != null && mnemonicWordList.validWord(wordItemWithCursor.word, true) -> {
                invalidWordItems.filter { it != wordItemWithCursor }
            }
            else -> invalidWordItems
        }

        invalidWordRanges = invalidWordItemsExcludingCursoredPartiallyValid.map { it.range }
        wordSuggestions = wordItemWithCursor?.let {
            RestoreMnemonicModule.WordSuggestions(it, mnemonicWordList.fetchSuggestions(it.word))
        }
    }

    fun onTogglePassphrase(enabled: Boolean) {
        passphraseEnabled = enabled
        passphrase = ""
        passphraseError = null

        emitState()
    }

    fun onEnterName(name: String) {
        accountName = name
    }

    fun onEnterPassphrase(passphrase: String) {
        this.passphrase = passphrase
        passphraseError = null

        emitState()
    }

    fun onEnterMnemonicPhrase(text: String, cursorPosition: Int) {
        error = null
        this.text = text
        this.cursorPosition = cursorPosition
        processText()

        emitState()
    }

    fun setMnemonicLanguage(language: Language) {
        this.language = language
        mnemonicWordList = WordList.wordList(language)
        processText()

        emitState()
    }

    fun onProceed() {
        when {
            invalidWordItems.isNotEmpty() -> {
                invalidWordRanges = invalidWordItems.map { it.range }
            }
            wordItems.size !in (Mnemonic.EntropyStrength.values().map { it.wordCount }) -> {
                error = Translator.getString(R.string.Restore_Error_MnemonicWordCount, wordItems.size)
            }
            passphraseEnabled && passphrase.isBlank() -> {
                passphraseError = Translator.getString(R.string.Restore_Error_EmptyPassphrase)
            }
            else -> {
                try {
                    val words = wordItems.map { it.word }
                    wordsManager.validateChecksum(words)

                    accountType = AccountType.Mnemonic(words, passphrase)
                    error = null
                } catch (checksumException: Exception) {
                    error = Translator.getString(R.string.Restore_InvalidChecksum)
                }
            }
        }

        emitState()
    }

    fun onSelectCoinsShown() {
        accountType = null

        emitState()
    }

    fun onAllowThirdPartyKeyboard() {
        thirdKeyboardStorage.isThirdPartyKeyboardAllowed = true
    }

    private fun wordItems(text: String): List<RestoreMnemonicModule.WordItem> {
        return regex.findAll(text.lowercase())
            .map { RestoreMnemonicModule.WordItem(it.value, it.range) }
            .toList()
    }
}
