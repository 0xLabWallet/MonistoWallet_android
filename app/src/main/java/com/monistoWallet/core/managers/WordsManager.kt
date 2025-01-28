package com.monistoWallet.core.managers

import com.monistoWallet.core.IWordsManager
import com.wallet0x.hdwalletkit.Language
import com.wallet0x.hdwalletkit.Mnemonic

class WordsManager(
    private val mnemonic: Mnemonic
) : IWordsManager {

    @Throws
    override fun validateChecksum(words: List<String>) {
        mnemonic.validate(words)
    }

    @Throws
    override fun validateChecksumStrict(words: List<String>) {
        mnemonic.validateStrict(words)
    }

    override fun isWordValid(word: String): Boolean {
        return mnemonic.isWordValid(word, false)
    }

    override fun isWordPartiallyValid(word: String): Boolean {
        return mnemonic.isWordValid(word, true)
    }

    override fun generateWords(count: Int): List<String> {
        val strength = Mnemonic.EntropyStrength.fromWordCount(count)
        return mnemonic.generate(strength, Language.English)
    }
}
