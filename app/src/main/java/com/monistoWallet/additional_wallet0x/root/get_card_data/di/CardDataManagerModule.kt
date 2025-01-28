package com.monistoWallet.additional_wallet0x.root.get_card_data.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.monistoWallet.additional_wallet0x.root.get_card_data.data.impl.GetCardDataRepositoryImpl
import com.monistoWallet.additional_wallet0x.root.get_card_data.data.impl.SaveCardDataRepositoryImpl
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.api.GetCardDataRepository
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.api.SaveCardDataRepository
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.impl.CardDataManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val cardDataManagerModule = module {
    single<CardDataManager> {
        CardDataManager(get(), get(), get())
    }

    single<SaveCardDataRepository> {
        val masterKey = MasterKey.Builder(androidContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val encryptedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
            androidContext(),
            "secure_prefs_2",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        SaveCardDataRepositoryImpl(encryptedPreferences)
    }

    single<GetCardDataRepository> {
        GetCardDataRepositoryImpl()
    }
}