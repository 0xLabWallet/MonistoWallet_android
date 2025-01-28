package com.monistoWallet.core

import com.google.gson.JsonObject
import com.monistoWallet.core.adapters.zcash.ZcashAdapter
import com.monistoWallet.core.managers.ActiveAccountState
import com.monistoWallet.core.managers.Bep2TokenInfoService
import com.monistoWallet.core.managers.EvmKitWrapper
import com.monistoWallet.core.providers.FeeRates
import com.monistoWallet.core.utils.AddressUriResult
import com.monistoWallet.entities.Account
import com.monistoWallet.entities.AppVersion
import com.monistoWallet.entities.EnabledWallet
import com.monistoWallet.entities.LastBlockInfo
import com.monistoWallet.entities.LaunchPage
import com.monistoWallet.entities.RestoreSettingRecord
import com.monistoWallet.entities.SyncMode
import com.monistoWallet.entities.TransactionDataSortMode
import com.monistoWallet.entities.Wallet
import com.monistoWallet.entities.transactionrecords.TransactionRecord
import com.monistoWallet.modules.amount.AmountInputType
import com.monistoWallet.modules.balance.BalanceSortType
import com.monistoWallet.modules.balance.BalanceViewType
import com.monistoWallet.modules.main.MainModule
import com.monistoWallet.modules.settings.appearance.AppIcon
import com.monistoWallet.modules.settings.security.autolock.AutoLockInterval
import com.monistoWallet.modules.settings.security.tor.TorStatus
import com.monistoWallet.modules.settings.terms.TermsModule
import com.monistoWallet.modules.theme.ThemeType
import com.monistoWallet.modules.transactions.FilterTransactionType
import com.wallet0x.binancechainkit.BinanceChainKit
import com.wallet0x.bitcoincore.core.IPluginData
import com.wallet0x.ethereumkit.models.Address
import com.wallet0x.ethereumkit.models.TransactionData
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.Token
import com.wallet0x.marketkit.models.TokenQuery
import com.wallet0x.solanakit.models.FullTransaction
import com.wallet0x.tronkit.transaction.Fee
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal
import java.util.Date
import com.wallet0x.solanakit.models.Address as SolanaAddress
import com.wallet0x.tronkit.models.Address as TronAddress

interface IAdapterManager {
    val adaptersReadyObservable: Flowable<Map<Wallet, IAdapter>>
    fun refresh()
    fun getAdapterForWallet(wallet: Wallet): IAdapter?
    fun getAdapterForToken(token: Token): IAdapter?
    fun getBalanceAdapterForWallet(wallet: Wallet): IBalanceAdapter?
    fun getReceiveAdapterForWallet(wallet: Wallet): IReceiveAdapter?
    fun refreshAdapters(wallets: List<Wallet>)
    fun refreshByWallet(wallet: Wallet)
}

interface ILocalStorage {
    var marketSearchRecentCoinUids: List<String>
    var zcashAccountIds: Set<String>
    var autoLockInterval: AutoLockInterval
    var chartIndicatorsEnabled: Boolean
    var amountInputType: AmountInputType?
    var baseCurrencyCode: String?
    var authToken: String?
    val appId: String?

    var baseBitcoinProvider: String?
    var baseLitecoinProvider: String?
    var baseEthereumProvider: String?
    var baseDashProvider: String?
    var baseBinanceProvider: String?
    var baseZcashProvider: String?
    var syncMode: SyncMode?
    var sortType: BalanceSortType
    var appVersions: List<AppVersion>
    var isAlertNotificationOn: Boolean
    var isLockTimeEnabled: Boolean
    var encryptedSampleText: String?
    var bitcoinDerivation: com.monistoWallet.entities.AccountType.Derivation?
    var torEnabled: Boolean
    var appLaunchCount: Int
    var rateAppLastRequestTime: Long
    var balanceHidden: Boolean
    var balanceAutoHideEnabled: Boolean
    var balanceTotalCoinUid: String?
    var termsAccepted: Boolean
    var mainShowedOnce: Boolean
    var notificationId: String?
    var notificationServerTime: Long
    var currentTheme: ThemeType
    var balanceViewType: BalanceViewType?
    var changelogShownForAppVersion: String?
    var ignoreRootedDeviceWarning: Boolean
    var launchPage: LaunchPage?
    var appIcon: AppIcon?
    var mainTab: MainModule.MainNavigation?
    var marketFavoritesSortingField: com.monistoWallet.modules.market.SortingField?
    var marketFavoritesMarketField: com.monistoWallet.modules.market.MarketField?
    var relaunchBySettingChange: Boolean
    var cardsTabEnabled: Boolean
    var cryptoWalletTabEnabled: Boolean
    val cardsTabEnabledFlow: StateFlow<Boolean>
    val cryptoWalletTabEnabledFlow: StateFlow<Boolean>
    var nonRecommendedAccountAlertDismissedAccounts: Set<String>
    var personalSupportEnabled: Boolean
    var hideSuspiciousTransactions: Boolean
    var pinRandomized: Boolean

    fun getSwapProviderId(blockchainType: BlockchainType): String?
    fun setSwapProviderId(blockchainType: BlockchainType, providerId: String)

    fun clear()
}

interface IRestoreSettingsStorage {
    fun restoreSettings(accountId: String, blockchainTypeUid: String): List<RestoreSettingRecord>
    fun restoreSettings(accountId: String): List<RestoreSettingRecord>
    fun save(restoreSettingRecords: List<RestoreSettingRecord>)
    fun deleteAllRestoreSettings(accountId: String)
}

interface IMarketStorage {
    var currentMarketTab: com.monistoWallet.modules.market.MarketModule.Tab?
}

interface IAccountManager {
    val hasNonStandardAccount: Boolean
    val activeAccount: Account?
    val activeAccountStateFlow: Flow<ActiveAccountState>
    val isAccountsEmpty: Boolean
    val accounts: List<Account>
    val accountsFlowable: Flowable<List<Account>>
    val accountsDeletedFlowable: Flowable<Unit>
    val newAccountBackupRequiredFlow: StateFlow<Account?>

    fun setActiveAccountId(activeAccountId: String?)
    fun account(id: String): Account?
    fun save(account: Account)
    fun import(accounts: List<Account>)
    fun update(account: Account)
    fun delete(id: String)
    fun clear()
    fun clearAccounts()
    fun onHandledBackupRequiredNewAccount()
    fun setLevel(level: Int)
    fun updateAccountLevels(accountIds: List<String>, level: Int)
    fun updateMaxLevel(level: Int)
}

interface IBackupManager {
    val allBackedUp: Boolean
    val allBackedUpFlowable: Flowable<Boolean>
}

interface IAccountFactory {
    fun account(
        name: String,
        type: com.monistoWallet.entities.AccountType,
        origin: com.monistoWallet.entities.AccountOrigin,
        backedUp: Boolean,
        fileBackedUp: Boolean
    ): Account
    fun watchAccount(name: String, type: com.monistoWallet.entities.AccountType): Account
    fun getNextWatchAccountName(): String
    fun getNextAccountName(): String
    fun getNextCexAccountName(cexType: com.monistoWallet.entities.CexType): String
}

interface IWalletStorage {
    fun wallets(account: Account): List<Wallet>
    fun save(wallets: List<Wallet>)
    fun delete(wallets: List<Wallet>)
    fun handle(newEnabledWallets: List<EnabledWallet>)
    fun clear()
}

interface IRandomProvider {
    fun getRandomNumbers(count: Int, maxIndex: Int): List<Int>
}

interface INetworkManager {
    suspend fun getMarkdown(host: String, path: String): String
    suspend fun getReleaseNotes(host: String, path: String): JsonObject
    fun getTransaction(host: String, path: String, isSafeCall: Boolean): Flowable<JsonObject>
    fun getTransactionWithPost(
        host: String,
        path: String,
        body: Map<String, Any>
    ): Flowable<JsonObject>

    fun ping(host: String, url: String, isSafeCall: Boolean): Flowable<Any>
    fun getEvmInfo(host: String, path: String): Single<JsonObject>
    suspend fun getBep2Tokens(): List<Bep2TokenInfoService.Bep2Token>
}

interface IClipboardManager {
    fun copyText(text: String)
    fun getCopiedText(): String
    val hasPrimaryClip: Boolean
}

interface IWordsManager {
    fun validateChecksum(words: List<String>)
    fun validateChecksumStrict(words: List<String>)
    fun isWordValid(word: String): Boolean
    fun isWordPartiallyValid(word: String): Boolean
    fun generateWords(count: Int = 12): List<String>
}

sealed class AdapterState {
    object Synced : AdapterState()
    data class Syncing(val progress: Int? = null, val lastBlockDate: Date? = null) : AdapterState()
    data class SearchingTxs(val count: Int) : AdapterState()
    data class NotSynced(val error: Throwable) : AdapterState()

    override fun toString(): String {
        return when (this) {
            is Synced -> "Synced"
            is Syncing -> "Syncing ${progress?.let { "${it * 100}" } ?: ""} lastBlockDate: $lastBlockDate"
            is SearchingTxs -> "SearchingTxs count: $count"
            is NotSynced -> "NotSynced ${error.javaClass.simpleName} - message: ${error.message}"
            else -> "$this"
        }
    }
}

interface IBinanceKitManager {
    val binanceKit: BinanceChainKit?
    val statusInfo: Map<String, Any>?

    fun binanceKit(wallet: Wallet): BinanceChainKit
    fun unlink(account: Account)
}

interface ITransactionsAdapter {
    val explorerTitle: String
    val transactionsState: AdapterState
    val transactionsStateUpdatedFlowable: Flowable<Unit>

    val lastBlockInfo: LastBlockInfo?
    val lastBlockUpdatedFlowable: Flowable<Unit>

    fun getTransactionsAsync(
        from: TransactionRecord?,
        token: Token?,
        limit: Int,
        transactionType: FilterTransactionType
    ): Single<List<TransactionRecord>>

    fun getRawTransaction(transactionHash: String): String? = null

    fun getTransactionRecordsFlowable(
        token: Token?,
        transactionType: FilterTransactionType
    ): Flowable<List<TransactionRecord>>

    fun getTransactionUrl(transactionHash: String): String
}

class UnsupportedFilterException : Exception()

interface IBalanceAdapter {
    val balanceState: AdapterState
    val balanceStateUpdatedFlowable: Flowable<Unit>

    val balanceData: BalanceData
    val balanceUpdatedFlowable: Flowable<Unit>

    fun sendAllowed() = balanceState is AdapterState.Synced
}

data class BalanceData(val available: BigDecimal, val locked: BigDecimal = BigDecimal.ZERO) {
    val total get() = available + locked
}

interface IReceiveAdapter {
    val receiveAddress: String
    val isMainNet: Boolean

    val isAccountActive: Boolean
        get() = true
}

interface ISendBitcoinAdapter {
    val balanceData: BalanceData
    val blockchainType: BlockchainType
    fun availableBalance(
        feeRate: Int,
        address: String?,
        pluginData: Map<Byte, IPluginData>?
    ): BigDecimal

    fun minimumSendAmount(address: String?): BigDecimal?
    fun fee(
        amount: BigDecimal,
        feeRate: Int,
        address: String?,
        pluginData: Map<Byte, IPluginData>?
    ): BigDecimal?

    fun validate(address: String, pluginData: Map<Byte, IPluginData>?)
    fun send(
        amount: BigDecimal,
        address: String,
        feeRate: Int,
        pluginData: Map<Byte, IPluginData>?,
        transactionSorting: TransactionDataSortMode?,
        logger: AppLogger
    ): Single<Unit>
}

interface ISendEthereumAdapter {
    val evmKitWrapper: EvmKitWrapper
    val balanceData: BalanceData

    fun getTransactionData(amount: BigDecimal, address: Address): TransactionData
}

interface ISendBinanceAdapter {
    val availableBalance: BigDecimal
    val availableBinanceBalance: BigDecimal
    val fee: BigDecimal

    fun validate(address: String)
    fun send(amount: BigDecimal, address: String, memo: String?, logger: AppLogger): Single<Unit>
}

interface ISendZcashAdapter {
    val availableBalance: BigDecimal
    val fee: BigDecimal

    suspend fun validate(address: String): ZcashAdapter.ZCashAddressType
    suspend fun send(amount: BigDecimal, address: String, memo: String, logger: AppLogger): Long
}

interface IAdapter {
    fun start()
    fun stop()
    fun refresh()

    val debugInfo: String
}

interface ISendSolanaAdapter {
    val availableBalance: BigDecimal
    suspend fun send(amount: BigDecimal, to: SolanaAddress): FullTransaction
}

interface ISendTonAdapter {
    val availableBalance: BigDecimal
    suspend fun send(amount: BigDecimal, address: String)
    suspend fun estimateFee() : BigDecimal
}

interface ISendTronAdapter {
    val balanceData: BalanceData
    val trxBalanceData: BalanceData

    suspend fun estimateFee(amount: BigDecimal, to: TronAddress): List<Fee>
    suspend fun send(amount: BigDecimal, to: TronAddress, feeLimit: Long?)
    suspend fun isAddressActive(address: TronAddress): Boolean
    fun isOwnAddress(address: TronAddress): Boolean
}

interface IAccountsStorage {
    val isAccountsEmpty: Boolean

    fun getActiveAccountId(level: Int): String?
    fun setActiveAccountId(level: Int, id: String?)
    fun allAccounts(accountsMinLevel: Int): List<Account>
    fun save(account: Account)
    fun update(account: Account)
    fun delete(id: String)
    fun getNonBackedUpCount(): Flowable<Int>
    fun clear()
    fun getDeletedAccountIds(): List<String>
    fun clearDeleted()
    fun updateLevels(accountIds: List<String>, level: Int)
    fun updateMaxLevel(level: Int)
}

interface IEnabledWalletStorage {
    val enabledWallets: List<EnabledWallet>
    fun enabledWallets(accountId: String): List<EnabledWallet>
    fun save(enabledWallets: List<EnabledWallet>)
    fun delete(enabledWallets: List<EnabledWallet>)
    fun deleteAll()
}

interface IWalletManager {
    val activeWallets: List<Wallet>
    val activeWalletsUpdatedObservable: Observable<List<Wallet>>

    fun save(wallets: List<Wallet>)
    fun saveEnabledWallets(enabledWallets: List<EnabledWallet>)
    fun delete(wallets: List<Wallet>)
    fun clear()
    fun handle(newWallets: List<Wallet>, deletedWallets: List<Wallet>)
    fun getWallets(account: Account): List<Wallet>
}

interface IAppNumberFormatter {
    fun format(
        value: Number,
        minimumFractionDigits: Int,
        maximumFractionDigits: Int,
        prefix: String = "",
        suffix: String = ""
    ): String

    fun formatCoinFull(
        value: BigDecimal,
        code: String?,
        coinDecimals: Int,
    ): String

    fun formatCoinShort(
        value: BigDecimal,
        code: String?,
        coinDecimals: Int
    ): String

    fun formatNumberShort(
        value: BigDecimal,
        maximumFractionDigits: Int
    ): String

    fun formatFiatFull(
        value: BigDecimal,
        symbol: String
    ): String

    fun formatFiatShort(
        value: BigDecimal,
        symbol: String,
        currencyDecimals: Int
    ): String

    fun formatValueAsDiff(value: com.monistoWallet.modules.market.Value): String
}

interface IFeeRateProvider {
    val feeRateChangeable: Boolean get() = false
    suspend fun getFeeRates() : FeeRates
}

interface IAddressParser {
    fun parse(addressUri: String): AddressUriResult
}

interface IAccountCleaner {
    fun clearAccounts(accountIds: List<String>)
}

interface ITorManager {
    fun start()
    fun stop(): Single<Boolean>
    fun setTorAsEnabled()
    fun setTorAsDisabled()
    val isTorEnabled: Boolean
    val torStatusFlow: StateFlow<TorStatus>
}

interface IRateAppManager {
    val showRateAppFlow: Flow<Boolean>

    fun onBalancePageActive()
    fun onBalancePageInactive()
    fun onAppLaunch()
}

interface ICoinManager {
    fun getToken(query: TokenQuery): Token?
}

interface ITermsManager {
    val termsAcceptedSignalFlow: Flow<Boolean>
    val terms: List<TermsModule.TermType>
    val allTermsAccepted: Boolean
    fun acceptTerms()
}

interface Clearable {
    fun clear()
}
