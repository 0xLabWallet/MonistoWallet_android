package com.monistoWallet.additional_wallet0x.root.main.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.monistoWallet.additional_wallet0x.root.tokens.RefreshTokenManager
import com.monistoWallet.additional_wallet0x.root.tokens.SSEClient
import com.monistoWallet.additional_wallet0x.root.main.data.TokenDatabaseRepositoryImpl
import com.monistoWallet.additional_wallet0x.root.main.domain.api.TokenDatabaseInteractor
import com.monistoWallet.additional_wallet0x.root.main.domain.api.TokenDatabaseRepository
import com.monistoWallet.additional_wallet0x.root.main.domain.impl.TokenDatabaseInteractorImpl
import com.monistoWallet.additional_wallet0x.root.main.ui.view_model.RootAccountViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val rootModule = module {
    viewModel {
        RootAccountViewModel(get(), get(), get(), get(), get())
    }

    single<TokenDatabaseInteractor> {
        TokenDatabaseInteractorImpl(get())
    }

    single<TokenDatabaseRepository> {

        val masterKey = MasterKey.Builder(androidContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val encryptedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
            androidContext(),
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        TokenDatabaseRepositoryImpl(
            encryptedPreferences
        )
    }

    single<RefreshTokenManager> {
        RefreshTokenManager(get())
    }

    single<SSEClient> {
        SSEClient()
    }
}

