package com.monistoWallet.additional_wallet0x.root.get_card_data.data.impl

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.api.SaveCardDataRepository
import com.monistoWallet.additional_wallet0x.root.get_card_data.data.model.Data
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.model.SaveCardSecretDataResponse
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SaveCardDataRepositoryImpl(
    val sharedPreferences: SharedPreferences,
) : SaveCardDataRepository {
    private val gson = Gson()
    private val KEYSTORE_ALIAS = "app_token_alias"
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val TRANSFORMATION = "AES/GCM/NoPadding"

    init {
        generateKey()
    }

    private fun generateKey() {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    KEYSTORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
            keyGenerator.generateKey()
        }
    }

    // Шифрование данных
    private fun encryptData(data: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encryptedData = cipher.doFinal(data.toByteArray())

        val outputStream = ByteArrayOutputStream()
        outputStream.write(iv) // Сохраняем IV
        outputStream.write(encryptedData)

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    // Расшифровка данных
    private fun decryptData(encryptedData: String): String {
        val dataBytes = Base64.decode(encryptedData, Base64.DEFAULT)
        val cipher = Cipher.getInstance(TRANSFORMATION)

        val iv = dataBytes.copyOfRange(0, 12) // Извлекаем IV из зашифрованных данных
        val encryptedBytes = dataBytes.copyOfRange(12, dataBytes.size)

        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), GCMParameterSpec(128, iv))
        val decryptedData = cipher.doFinal(encryptedBytes)

        return String(decryptedData)
    }

    // Получение ключа из Keystore
    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        return keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
    }

    override fun saveCardData(cardId: String, data: Data) {
        val newCardData = CardSavedData(cardId, data.cvv, data.expiry_date)
        val list = getList().toMutableList()
        list.add(newCardData)
        val str = gson.toJson(list)
        val encryptedData = encryptData(str)
        sharedPreferences.edit()
            .putString(SAVED_CARDS_DATA, encryptedData)
            .apply()
    }

    private fun getList(): List<CardSavedData> {
        val encryptedJson = sharedPreferences.getString(SAVED_CARDS_DATA, null) ?: return emptyList()
        return try {
            val json = decryptData(encryptedJson)
            val type = object : TypeToken<List<CardSavedData>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getCardData(cardId: String): SaveCardSecretDataResponse {
        val list = getList()
        list.forEach {
            if (it.id == cardId) {
                return SaveCardSecretDataResponse.CardFound(Data(it.cvv, it.date))
            }
        }
        return SaveCardSecretDataResponse.CardNotFound
    }

    private companion object {
        const val SAVED_CARDS_DATA = "SAVED_CARDS_DATA"
        data class CardSavedData(
            val id: String,
            val cvv: String,
            val date: String
        )
    }
}
