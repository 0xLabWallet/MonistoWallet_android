package com.monistoWallet.core.managers

import android.app.Activity
import android.app.KeyguardManager
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import com.monistoWallet.core.App
import com.monistoWallet.core.providers.AppConfigProvider
import com.monistoWallet.core.ISystemInfoManager

class SystemInfoManager(appConfigProvider: AppConfigProvider) : ISystemInfoManager {

    override val appVersion: String = appConfigProvider.appVersion

    private val biometricManager by lazy { BiometricManager.from(com.monistoWallet.core.App.instance) }

    override val isSystemLockOff: Boolean
        get() {
            val keyguardManager = com.monistoWallet.core.App.instance.getSystemService(Activity.KEYGUARD_SERVICE) as KeyguardManager
            return !keyguardManager.isDeviceSecure
        }

    override val biometricAuthSupported: Boolean
        get() = biometricManager.canAuthenticate(BIOMETRIC_WEAK) == BIOMETRIC_SUCCESS

    override val deviceModel: String
        get() = "${Build.MANUFACTURER} ${Build.MODEL}"

    override val osVersion: String
        get() = "Android ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})"

}
