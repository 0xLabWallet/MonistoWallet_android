package com.monistoWallet.modules.profeatures.yakauthorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.modules.profeatures.HSProFeaturesAdapter

object YakAuthorizationModule {

    @Suppress("UNCHECKED_CAST")
    class Factory : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val configProvider = com.monistoWallet.core.App.appConfigProvider
            val adapter = HSProFeaturesAdapter(configProvider.marketApiBaseUrl, configProvider.marketApiKey)
            val service = YakAuthorizationService(App.proFeatureAuthorizationManager, adapter)

            return YakAuthorizationViewModel(service) as T
        }
    }

}
