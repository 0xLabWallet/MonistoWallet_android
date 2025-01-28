package com.monistoWallet.additional_wallet0x.root.main.data

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.root.main.domain.api.TokenDatabaseRepository
import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class TokenDatabaseRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : TokenDatabaseRepository {

    private val gson = Gson()
    private val KEYSTORE_ALIAS = "app_token_alias"
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val TRANSFORMATION = "AES/GCM/NoPadding"

    init {
        generateKey()
    }

    override fun saveToken(model: VerificationSuccessModel?) {
        if (model != null) {
            val data = gson.toJson(model)
            val encryptedData = encryptData(data)
            saveEncryptedTokenToStorage(encryptedData)
        } else {
            val data = ""
            val encryptedData = encryptData(data)
            saveEncryptedTokenToStorage(encryptedData)
        }
    }

    override fun getToken(): VerificationSuccessModel? {
        val encryptedData = getEncryptedTokenFromStorage() ?: return null
        val decryptedData = decryptData(encryptedData)
        return gson.fromJson(decryptedData, VerificationSuccessModel::class.java)
    }

    // Генерация ключа в Android Keystore
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

    // Методы для работы с хранилищем
    private fun saveEncryptedTokenToStorage(encryptedToken: String) {
        // Сохрани зашифрованный токен в SharedPreferences
        sharedPreferences.edit()
            .putString("encrypted_app_token", encryptedToken)
            .apply()
    }

    private fun getEncryptedTokenFromStorage(): String? {
        // Получи зашифрованный токен из SharedPreferences
        val result = sharedPreferences.getString("encrypted_app_token", null)
        if (result == "") return null
        return result
    }
}
