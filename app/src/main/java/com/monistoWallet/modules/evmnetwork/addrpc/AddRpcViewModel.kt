package com.monistoWallet.modules.evmnetwork.addrpc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.R
import com.monistoWallet.core.managers.EvmSyncSourceManager
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.modules.swap.settings.Caution
import com.wallet0x.marketkit.models.Blockchain
import java.net.MalformedURLException
import java.net.URI

class AddRpcViewModel(
    private val blockchain: Blockchain,
    private val evmSyncSourceManager: EvmSyncSourceManager
) : ViewModel() {

    private var url = ""
    private var auth: String? = null
    private var urlCaution: Caution? = null

    var viewState by mutableStateOf(AddRpcViewState(null))
        private set

    fun onEnterBasicAuth(basicAuth: String) {
        auth = basicAuth.trim()
    }

    fun onEnterRpcUrl(enteredUrl: String) {
        urlCaution = null
        url = enteredUrl.trim()
        syncState()
    }

    fun onScreenClose() {
        viewState = AddRpcViewState()
    }

    fun onAddClick() {
        val sourceUri: URI

        try {
            sourceUri = URI(url)
            val hasRequiredProtocol = listOf("https", "wss").contains(sourceUri.scheme)
            if (!hasRequiredProtocol) {
                throw MalformedURLException()
            }
        } catch (e: MalformedURLException) {
            urlCaution = Caution(Translator.getString(R.string.AddEvmSyncSource_Error_InvalidUrl), Caution.Type.Error)
            syncState()
            return
        }

        val existingSources = evmSyncSourceManager.allSyncSources(blockchain.type)

        if (existingSources.any { it.uri == sourceUri}) {
            urlCaution = Caution(Translator.getString(R.string.AddEvmSyncSource_Warning_UrlExists), Caution.Type.Warning)
            syncState()
            return
        }

        evmSyncSourceManager.saveSyncSource(blockchain.type, url, auth)

        viewState = AddRpcViewState(null, true)
    }

    private fun syncState() {
        viewState = AddRpcViewState(urlCaution)
    }
}

data class AddRpcViewState(
    val urlCaution: Caution? = null,
    val closeScreen: Boolean = false
)