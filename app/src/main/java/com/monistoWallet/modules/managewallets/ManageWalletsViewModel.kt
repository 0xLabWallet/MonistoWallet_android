package com.monistoWallet.modules.managewallets

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.core.Clearable
import com.monistoWallet.core.badge
import com.monistoWallet.core.iconPlaceholder
import com.monistoWallet.core.imageUrl
import com.monistoWallet.modules.market.ImageSource
import com.monistoWallet.modules.restoreaccount.restoreblockchains.CoinViewItem
import com.wallet0x.marketkit.models.Token
import kotlinx.coroutines.launch

class ManageWalletsViewModel(
    private val service: ManageWalletsService,
    private val clearables: List<Clearable>
) : ViewModel() {

    val viewItemsLiveData = MutableLiveData<List<CoinViewItem<Token>>>()

    init {
        viewModelScope.launch {
            service.itemsFlow.collect {
                sync(it)
            }
        }
    }

    private fun sync(items: List<ManageWalletsService.Item>) {
        val viewItems = items.map { viewItem(it) }
        viewItemsLiveData.postValue(viewItems)
    }

    private fun viewItem(
        item: ManageWalletsService.Item,
    ) = CoinViewItem(
        item = item.token,
        imageSource = com.monistoWallet.modules.market.ImageSource.Remote(item.token.coin.imageUrl, item.token.iconPlaceholder),
        title = item.token.coin.code,
        subtitle = item.token.coin.name,
        enabled = item.enabled,
        hasInfo = item.hasInfo,
        label = item.token.badge
    )

    fun enable(token: Token) {
        service.enable(token)
    }

    fun disable(token: Token) {
        service.disable(token)
    }

    fun updateFilter(filter: String) {
        service.setFilter(filter)
    }

    val addTokenEnabled: Boolean
        get() = service.accountType?.canAddTokens ?: false

    override fun onCleared() {
        clearables.forEach(Clearable::clear)
    }

    data class BirthdayHeightViewItem(
        val blockchainIcon: com.monistoWallet.modules.market.ImageSource,
        val blockchainName: String,
        val birthdayHeight: String
    )
}
