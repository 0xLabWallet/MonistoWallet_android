package com.monistoWallet.modules.balance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.R
import com.monistoWallet.core.AdapterState
import com.monistoWallet.core.ILocalStorage
import com.monistoWallet.core.factories.uriScheme
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.core.supported
import com.monistoWallet.core.utils.AddressUriParser
import com.monistoWallet.core.utils.AddressUriResult
import com.monistoWallet.core.utils.ToncoinUriParser
import com.monistoWallet.entities.Account
import com.monistoWallet.entities.AddressUri
import com.monistoWallet.entities.ViewState
import com.monistoWallet.entities.Wallet
import com.monistoWallet.modules.walletconnect.list.WalletConnectListModule
import com.monistoWallet.modules.walletconnect.list.WalletConnectListViewModel
import com.monistoWallet.modules.walletconnect.version2.WC2Manager
import com.monistoWallet.modules.walletconnect.version2.WC2Service
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.TokenType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class BalanceViewModel(
    private val service: BalanceService,
    private val balanceViewItemFactory: BalanceViewItemFactory,
    private val balanceViewTypeManager: BalanceViewTypeManager,
    private val totalBalance: TotalBalance,
    private val localStorage: ILocalStorage,
    private val wc2Service: WC2Service,
    private val wC2Manager: WC2Manager,
    private val addressHandlerFactory: com.monistoWallet.modules.address.AddressHandlerFactory,
) : ViewModel(), ITotalBalance by totalBalance {

    private var balanceViewType = balanceViewTypeManager.balanceViewTypeFlow.value
    private var viewState: ViewState? = null
    private var balanceViewItems = listOf<BalanceViewItem2>()
    private var isRefreshing = false
    private var openSendTokenSelect: OpenSendTokenSelect? = null
    private var errorMessage: String? = null

    var uiState by mutableStateOf(
        BalanceUiState(
            balanceViewItems = balanceViewItems,
            viewState = viewState,
            isRefreshing = isRefreshing,
            headerNote = HeaderNote.None,
            errorMessage = errorMessage,
            openSend = openSendTokenSelect
        )
    )
        private set

    val sortTypes = listOf(BalanceSortType.Value, BalanceSortType.Name, BalanceSortType.PercentGrowth)
    var sortType by service::sortType

    var connectionResult by mutableStateOf<WalletConnectListViewModel.ConnectionResult?>(null)
        private set

    init {
        viewModelScope.launch {
            service.balanceItemsFlow
                .collect { items ->
                    totalBalance.setTotalServiceItems(items?.map {
                        TotalService.BalanceItem(
                            it.balanceData.total,
                            it.state !is AdapterState.Synced,
                            it.coinPrice
                        )
                    })

                    refreshViewItems(items)
                }
        }

        viewModelScope.launch {
            totalBalance.stateFlow.collect {
                refreshViewItems(service.balanceItemsFlow.value)
            }
        }

        viewModelScope.launch {
            balanceViewTypeManager.balanceViewTypeFlow.collect {
                handleUpdatedBalanceViewType(it)
            }
        }

        service.start()

        totalBalance.start(viewModelScope)
    }

    private suspend fun handleUpdatedBalanceViewType(balanceViewType: BalanceViewType) {
        this.balanceViewType = balanceViewType

        service.balanceItemsFlow.value?.let {
            refreshViewItems(it)
        }
    }

    private fun emitState() {
        val newUiState = BalanceUiState(
            balanceViewItems = balanceViewItems,
            viewState = viewState,
            isRefreshing = isRefreshing,
            headerNote = headerNote(),
            errorMessage = errorMessage,
            openSend = openSendTokenSelect
        )

        viewModelScope.launch {
            uiState = newUiState
        }
    }

    private fun headerNote(): HeaderNote {
        val account = service.account ?: return HeaderNote.None
        val nonRecommendedDismissed = localStorage.nonRecommendedAccountAlertDismissedAccounts.contains(account.id)

        return account.headerNote(nonRecommendedDismissed)
    }

    private suspend fun refreshViewItems(balanceItems: List<BalanceModule.BalanceItem>?) {
        withContext(Dispatchers.IO) {
            if (balanceItems != null) {
                viewState = ViewState.Success
                balanceViewItems = balanceItems.map { balanceItem ->
                    balanceViewItemFactory.viewItem2(
                        balanceItem,
                        service.baseCurrency,
                        balanceHidden,
                        service.isWatchAccount,
                        balanceViewType,
                        service.networkAvailable
                    )
                }
            } else {
                viewState = null
                balanceViewItems = listOf()
            }

            emitState()
        }
    }

    fun onHandleRoute() {
        connectionResult = null
    }

    override fun onCleared() {
        totalBalance.stop()
        service.clear()
    }

    fun onRefresh() {
        if (isRefreshing) {
            return
        }

        viewModelScope.launch {
            isRefreshing = true
            emitState()

            service.refresh()
            // A fake 2 seconds 'refresh'
            delay(2300)

            isRefreshing = false
            emitState()
        }
    }

    fun onCloseHeaderNote(headerNote: HeaderNote) {
        when (headerNote) {
            HeaderNote.NonRecommendedAccount -> {
                service.account?.let { account ->
                    localStorage.nonRecommendedAccountAlertDismissedAccounts += account.id
                    emitState()
                }
            }

            else -> Unit
        }
    }

    fun disable(viewItem: BalanceViewItem2) {
        service.disable(viewItem.wallet)
    }

    fun getSyncErrorDetails(viewItem: BalanceViewItem2): SyncError = when {
        service.networkAvailable -> SyncError.Dialog(viewItem.wallet, viewItem.errorMessage)
        else -> SyncError.NetworkNotAvailable()
    }

    fun getReceiveAllowedState(): ReceiveAllowedState? {
        val tmpAccount = service.account ?: return null
        return when {
            tmpAccount.hasAnyBackup -> ReceiveAllowedState.Allowed
            else -> ReceiveAllowedState.BackupRequired(tmpAccount)
        }
    }

    fun getWalletConnectSupportState(): WC2Manager.SupportState {
        return wC2Manager.getWalletConnectSupportState()
    }

    fun handleScannedData(scannedText: String) {
        val wcUriVersion = WalletConnectListModule.getVersionFromUri(scannedText)
        if (wcUriVersion == 2) {
            handleWalletConnectUri(scannedText)
        } else {
            handleAddressData(scannedText)
        }
    }

    private fun uri(text: String): AddressUri? {
        if (AddressUriParser.hasUriPrefix(text)) {
            val abstractUriParse = AddressUriParser(null, null)
            return when (val result = abstractUriParse.parse(text)) {
                is AddressUriResult.Uri -> {
                    if (BlockchainType.supported.map { it.uriScheme }.contains(result.addressUri.scheme))
                        result.addressUri
                    else
                        null
                }

                else -> null
            }
        }
        return null
    }

    private fun handleAddressData(text: String) {
        if (text.contains("//")) {
            //handle this type of uri ton://transfer/<address>
            val toncoinAddress = ToncoinUriParser.getAddress(text) ?: return
            openSendTokenSelect = OpenSendTokenSelect(
                blockchainTypes = listOf(BlockchainType.Ton),
                tokenTypes = null,
                address = toncoinAddress,
                amount = null
            )
            emitState()
            return
        }

        val uri = uri(text)
        if (uri != null) {
            val allowedBlockchainTypes = uri.allowedBlockchainTypes
            var allowedTokenTypes: List<TokenType>? = null
            uri.value<String>(AddressUri.Field.TokenUid)?.let { uid ->
                TokenType.fromId(uid)?.let { tokenType ->
                    allowedTokenTypes = listOf(tokenType)
                }
            }

            openSendTokenSelect = OpenSendTokenSelect(
                blockchainTypes = allowedBlockchainTypes,
                tokenTypes = allowedTokenTypes,
                address = uri.address,
                amount = uri.amount
            )
            emitState()
        } else {
            val chain = addressHandlerFactory.parserChain(null)
            val types = chain.supportedAddressHandlers(text)
            if (types.isEmpty()) {
                errorMessage = Translator.getString(R.string.Balance_Error_InvalidQrCode)
                emitState()
                return
            }

            openSendTokenSelect = OpenSendTokenSelect(
                blockchainTypes = types.map { it.blockchainType },
                tokenTypes = null,
                address = text,
                amount = null
            )
            emitState()
        }
    }

    private fun handleWalletConnectUri(scannedText: String) {
        wc2Service.pair(
            uri = scannedText,
            onSuccess = {
                connectionResult = null
            },
            onError = {
                connectionResult = WalletConnectListViewModel.ConnectionResult.Error
            })
    }

    fun onSendOpened() {
        openSendTokenSelect = null
        emitState()
    }

    fun errorShown() {
        errorMessage = null
        emitState()
    }

    sealed class SyncError {
        class NetworkNotAvailable : SyncError()
        class Dialog(val wallet: Wallet, val errorMessage: String?) : SyncError()
    }
}

sealed class ReceiveAllowedState {
    object Allowed : ReceiveAllowedState()
    data class BackupRequired(val account: Account) : ReceiveAllowedState()
}

class BackupRequiredError(val account: Account, val coinTitle: String) : Error("Backup Required")

data class BalanceUiState(
    val balanceViewItems: List<BalanceViewItem2>,
    val viewState: ViewState?,
    val isRefreshing: Boolean,
    val headerNote: HeaderNote,
    val errorMessage: String?,
    val openSend: OpenSendTokenSelect? = null,
)

data class OpenSendTokenSelect(
    val blockchainTypes: List<BlockchainType>?,
    val tokenTypes: List<TokenType>?,
    val address: String,
    val amount: BigDecimal? = null,
)

sealed class TotalUIState {
    data class Visible(
        val primaryAmountStr: String,
        val secondaryAmountStr: String,
        val dimmed: Boolean
    ) : TotalUIState()

    object Hidden : TotalUIState()

}

enum class HeaderNote {
    None,
    NonStandardAccount,
    NonRecommendedAccount
}

fun Account.headerNote(nonRecommendedDismissed: Boolean): HeaderNote = when {
    nonStandard -> HeaderNote.NonStandardAccount
    nonRecommended -> if (nonRecommendedDismissed) HeaderNote.None else HeaderNote.NonRecommendedAccount
    else -> HeaderNote.None
}