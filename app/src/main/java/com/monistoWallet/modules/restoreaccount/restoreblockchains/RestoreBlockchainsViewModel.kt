package com.monistoWallet.modules.restoreaccount.restoreblockchains

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import com.monistoWallet.R
import com.monistoWallet.core.Clearable
import com.monistoWallet.core.description
import com.monistoWallet.core.imageUrl
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.modules.market.ImageSource
import com.wallet0x.marketkit.models.Blockchain
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.CompositeDisposable

class RestoreBlockchainsViewModel(
    private val service: RestoreBlockchainsService,
    private val clearables: List<Clearable>
) : ViewModel() {

    val viewItemsLiveData = MutableLiveData<List<CoinViewItem<Blockchain>>>()
    val disableBlockchainLiveData = MutableLiveData<String>()
    var restored by mutableStateOf(false)
        private set
    val restoreEnabledLiveData: LiveData<Boolean>
        get() = service.canRestore.toFlowable(BackpressureStrategy.DROP).toLiveData()

    private var disposables = CompositeDisposable()

    init {
        service.itemsObservable
            .subscribeIO { sync(it) }
            .let { disposables.add(it) }

        service.cancelEnableBlockchainObservable
            .subscribeIO { disableBlockchainLiveData.postValue(it.uid) }
            .let { disposables.add(it) }

        sync(service.items)
    }

    private fun sync(items: List<RestoreBlockchainsService.Item>) {
        val viewItems = items.map { viewItem(it) }
        viewItemsLiveData.postValue(viewItems)
    }

    private fun viewItem(
        item: RestoreBlockchainsService.Item,
    ) = CoinViewItem(
        item = item.blockchain,
        imageSource = com.monistoWallet.modules.market.ImageSource.Remote(item.blockchain.type.imageUrl, R.drawable.ic_platform_placeholder_32),
        title = item.blockchain.name,
        subtitle = item.blockchain.description,
        enabled = item.enabled,
        hasSettings = item.hasSettings,
        hasInfo = false
    )

    fun enable(blockchain: Blockchain) {
        service.enable(blockchain)
    }

    fun disable(blockchain: Blockchain) {
        service.disable(blockchain)
    }

    fun onClickSettings(blockchain: Blockchain) {
        service.configure(blockchain)
    }

    fun onRestore() {
        service.restore()
        restored = true
    }

    override fun onCleared() {
        clearables.forEach(Clearable::clear)
        disposables.clear()
    }
}
