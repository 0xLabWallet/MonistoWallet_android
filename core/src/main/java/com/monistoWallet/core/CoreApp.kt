package com.monistoWallet.core

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.LocaleList
import com.monistoWallet.core.helpers.LocaleHelper
import java.util.Locale

abstract class CoreApp : Application() {

    companion object : ICoreApp {
        override lateinit var backgroundManager: BackgroundManager
        override lateinit var encryptionManager: IEncryptionManager
        override lateinit var systemInfoManager: ISystemInfoManager
        override lateinit var keyStoreManager: IKeyStoreManager
        override lateinit var keyProvider: IKeyProvider
        override lateinit var pinComponent: IPinComponent
        override lateinit var pinSettingsStorage: IPinSettingsStorage
        override lateinit var lockoutStorage: ILockoutStorage
        override lateinit var thirdKeyboardStorage: IThirdKeyboard

        override lateinit var instance: CoreApp
    }

    abstract fun localizedContext(): Context

    fun localeAwareContext(base: Context): Context {
        return LocaleHelper.onAttach(base)
    }

    fun getLocale(): Locale {
        return LocaleHelper.getLocale(this)
    }

    fun setLocale(currentLocale: Locale) {
        Locale.setDefault(currentLocale)
        val resources = this.resources
        val config = resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(currentLocale))
        } else {
            @Suppress("DEPRECATION")
            config.setLocale(currentLocale)
        }
        config.setLayoutDirection(currentLocale)

        val updatedContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
            this
        }
        resources.updateConfiguration(config, resources.displayMetrics)

        LocaleHelper.setLocale(updatedContext, currentLocale)
    }
    fun isLocaleRTL(): Boolean {
        return LocaleHelper.isRTL(Locale.getDefault())
    }
}
