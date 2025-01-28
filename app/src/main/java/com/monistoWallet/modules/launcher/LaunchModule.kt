package com.monistoWallet.modules.launcher

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App

object LaunchModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LaunchViewModel(com.monistoWallet.core.App.accountManager, com.monistoWallet.core.App.pinComponent, com.monistoWallet.core.App.systemInfoManager, com.monistoWallet.core.App.keyStoreManager, com.monistoWallet.core.App.localStorage) as T
        }
    }

    fun start(context: Context) {
        val intent = Intent(context, LauncherActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

}
