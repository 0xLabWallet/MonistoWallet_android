package com.monistoWallet.core

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import com.monistoWallet.additional_wallet0x.account.card_found.di.cardFoundModule
import com.monistoWallet.additional_wallet0x.account.card_variants.di.allCardsToOrderModule
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.di.changeEmailModule
import com.monistoWallet.additional_wallet0x.settings.change_password.di.changePasswordModule
import com.monistoWallet.additional_wallet0x.account.freeze_card.di.mainCardModule
import com.monistoWallet.additional_wallet0x.account.pay_for_card.di.payModule
import com.monistoWallet.additional_wallet0x.account.recover_password.di.recoverModule
import com.monistoWallet.additional_wallet0x.account.transactions.di.cardTransactionsModule
import com.monistoWallet.additional_wallet0x.no_account.login.di.loginModule
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.di.loginEmailVerificationModule
import com.monistoWallet.additional_wallet0x.no_account.register_verification.di.registerEmailVerificationModule
import com.monistoWallet.additional_wallet0x.no_account.register.di.registerModule
import com.monistoWallet.additional_wallet0x.root.get_card_data.di.cardDataManagerModule
import com.monistoWallet.additional_wallet0x.root.main.di.rootModule
import com.monistoWallet.additional_wallet0x.settings.logout.di.logoutModule
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import com.monistoWallet.core.factories.AccountFactory
import com.monistoWallet.core.factories.AdapterFactory
import com.monistoWallet.core.factories.EvmAccountManagerFactory
import com.monistoWallet.core.managers.AccountCleaner
import com.monistoWallet.core.managers.AccountManager
import com.monistoWallet.core.managers.AdapterManager
import com.monistoWallet.core.managers.AppVersionManager
import com.monistoWallet.core.managers.BackgroundStateChangeListener
import com.monistoWallet.core.managers.BackupManager
import com.monistoWallet.core.managers.BalanceHiddenManager
import com.monistoWallet.core.managers.BaseTokenManager
import com.monistoWallet.core.managers.BinanceKitManager
import com.monistoWallet.core.managers.BtcBlockchainManager
import com.monistoWallet.core.managers.CexAssetManager
import com.monistoWallet.core.managers.CoinManager
import com.monistoWallet.core.managers.ConnectivityManager
import com.monistoWallet.core.managers.CurrencyManager
import com.monistoWallet.core.managers.EvmBlockchainManager
import com.monistoWallet.core.managers.EvmLabelManager
import com.monistoWallet.core.managers.EvmSyncSourceManager
import com.monistoWallet.core.managers.KeyStoreCleaner
import com.monistoWallet.core.managers.LanguageManager
import com.monistoWallet.core.managers.LocalStorageManager
import com.monistoWallet.core.managers.MarketFavoritesManager
import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.core.managers.NetworkManager
import com.monistoWallet.core.managers.NftAdapterManager
import com.monistoWallet.core.managers.NftMetadataManager
import com.monistoWallet.core.managers.NftMetadataSyncer
import com.monistoWallet.core.managers.NumberFormatter
import com.monistoWallet.core.managers.RateAppManager
import com.monistoWallet.core.managers.ReleaseNotesManager
import com.monistoWallet.core.managers.RestoreSettingsManager
import com.monistoWallet.core.managers.SolanaKitManager
import com.monistoWallet.core.managers.SolanaRpcSourceManager
import com.monistoWallet.core.managers.SolanaWalletManager
import com.monistoWallet.core.managers.SpamManager
import com.monistoWallet.core.managers.SubscriptionManager
import com.monistoWallet.core.managers.SystemInfoManager
import com.monistoWallet.core.managers.TermsManager
import com.monistoWallet.core.managers.TokenAutoEnableManager
import com.monistoWallet.core.managers.TorManager
import com.monistoWallet.core.managers.TransactionAdapterManager
import com.monistoWallet.core.managers.TronAccountManager
import com.monistoWallet.core.managers.TronKitManager
import com.monistoWallet.core.managers.UserManager
import com.monistoWallet.core.managers.WalletActivator
import com.monistoWallet.core.managers.WalletManager
import com.monistoWallet.core.managers.WalletStorage
import com.monistoWallet.core.managers.WordsManager
import com.monistoWallet.core.managers.ZcashBirthdayProvider
import com.monistoWallet.core.providers.AppConfigProvider
import com.monistoWallet.core.providers.CexProviderManager
import com.monistoWallet.core.providers.EvmLabelProvider
import com.monistoWallet.core.providers.FeeRateProvider
import com.monistoWallet.core.providers.FeeTokenProvider
import com.monistoWallet.modules.backuplocal.fullbackup.BackupProvider
import com.monistoWallet.modules.profeatures.ProFeaturesAuthorizationManager
import com.monistoWallet.modules.profeatures.storage.ProFeaturesStorage
import com.monistoWallet.widgets.MarketWidgetManager
import com.monistoWallet.widgets.MarketWidgetRepository
import com.monistoWallet.widgets.MarketWidgetWorker
import com.monistoWallet.core.security.EncryptionManager
import com.monistoWallet.core.security.KeyStoreManager
import com.monistoWallet.core.storage.AccountsStorage
import com.monistoWallet.core.storage.AppDatabase
import com.monistoWallet.core.storage.BlockchainSettingsStorage
import com.monistoWallet.core.storage.EnabledWalletsStorage
import com.monistoWallet.core.storage.EvmSyncSourceStorage
import com.monistoWallet.core.storage.NftStorage
import com.monistoWallet.core.storage.RestoreSettingsStorage
import com.monistoWallet.modules.balance.BalanceViewTypeManager
import com.monistoWallet.modules.chart.ChartIndicatorManager
import com.monistoWallet.modules.contacts.ContactsRepository
import com.monistoWallet.modules.keystore.KeyStoreActivity
import com.monistoWallet.modules.launcher.LauncherActivity
import com.monistoWallet.modules.lockscreen.LockScreenActivity
import com.monistoWallet.modules.market.favorites.MarketFavoritesMenuService
import com.monistoWallet.modules.market.topnftcollections.TopNftCollectionsRepository
import com.monistoWallet.modules.market.topnftcollections.TopNftCollectionsViewItemFactory
import com.monistoWallet.modules.market.topplatforms.TopPlatformsRepository
import com.monistoWallet.modules.pin.core.PinDbStorage
import com.monistoWallet.modules.settings.appearance.AppIconService
import com.monistoWallet.modules.settings.appearance.LaunchScreenService
import com.monistoWallet.modules.theme.ThemeService
import com.monistoWallet.modules.theme.ThemeType
import com.monistoWallet.modules.walletconnect.storage.WC2SessionStorage
import com.monistoWallet.modules.walletconnect.version2.WC2Manager
import com.monistoWallet.modules.walletconnect.version2.WC2Service
import com.monistoWallet.modules.walletconnect.version2.WC2SessionManager
import com.wallet0x.ethereumkit.core.EthereumKit
import com.wallet0x.hdwalletkit.Mnemonic
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.logging.Level
import java.util.logging.Logger
import androidx.work.Configuration as WorkConfiguration

class App : CoreApp(), WorkConfiguration.Provider, ImageLoaderFactory {

    companion object : ICoreApp by CoreApp {
        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false
                val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

                return when {
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                val networkInfo = connectivityManager.activeNetworkInfo
                return networkInfo != null && networkInfo.isConnected
            }
        }
        lateinit var preferences: SharedPreferences
        lateinit var feeRateProvider: FeeRateProvider
        lateinit var localStorage: ILocalStorage
        lateinit var marketStorage: IMarketStorage
        lateinit var torKitManager: ITorManager
        lateinit var restoreSettingsStorage: IRestoreSettingsStorage
        lateinit var currencyManager: CurrencyManager
        lateinit var languageManager: LanguageManager

        lateinit var blockchainSettingsStorage: BlockchainSettingsStorage
        lateinit var evmSyncSourceStorage: EvmSyncSourceStorage
        lateinit var btcBlockchainManager: BtcBlockchainManager
        lateinit var wordsManager: WordsManager
        lateinit var networkManager: INetworkManager
        lateinit var backgroundStateChangeListener: BackgroundStateChangeListener
        lateinit var appConfigProvider: AppConfigProvider
        lateinit var adapterManager: IAdapterManager
        lateinit var transactionAdapterManager: TransactionAdapterManager
        lateinit var walletManager: IWalletManager
        lateinit var walletActivator: WalletActivator
        lateinit var tokenAutoEnableManager: TokenAutoEnableManager
        lateinit var walletStorage: IWalletStorage
        lateinit var accountManager: IAccountManager
        lateinit var userManager: UserManager
        lateinit var accountFactory: IAccountFactory
        lateinit var backupManager: IBackupManager
        lateinit var proFeatureAuthorizationManager: ProFeaturesAuthorizationManager
        lateinit var zcashBirthdayProvider: ZcashBirthdayProvider

        lateinit var connectivityManager: ConnectivityManager
        lateinit var appDatabase: AppDatabase
        lateinit var accountsStorage: IAccountsStorage
        lateinit var enabledWalletsStorage: IEnabledWalletStorage
        lateinit var binanceKitManager: BinanceKitManager
        lateinit var solanaKitManager: SolanaKitManager
        lateinit var tronKitManager: TronKitManager
        lateinit var numberFormatter: IAppNumberFormatter
        lateinit var feeCoinProvider: FeeTokenProvider
        lateinit var accountCleaner: IAccountCleaner
        lateinit var rateAppManager: IRateAppManager
        lateinit var coinManager: ICoinManager
        lateinit var wc2Service: WC2Service
        lateinit var wc2SessionManager: WC2SessionManager
        lateinit var wc2Manager: WC2Manager
        lateinit var termsManager: ITermsManager
        lateinit var marketFavoritesManager: MarketFavoritesManager
        lateinit var marketKit: MarketKitWrapper
        lateinit var releaseNotesManager: ReleaseNotesManager
        lateinit var restoreSettingsManager: RestoreSettingsManager
        lateinit var evmSyncSourceManager: EvmSyncSourceManager
        lateinit var evmBlockchainManager: EvmBlockchainManager
        lateinit var solanaRpcSourceManager: SolanaRpcSourceManager
        lateinit var nftMetadataManager: NftMetadataManager
        lateinit var nftAdapterManager: NftAdapterManager
        lateinit var nftMetadataSyncer: NftMetadataSyncer
        lateinit var evmLabelManager: EvmLabelManager
        lateinit var baseTokenManager: BaseTokenManager
        lateinit var balanceViewTypeManager: BalanceViewTypeManager
        lateinit var balanceHiddenManager: BalanceHiddenManager
        lateinit var marketWidgetManager: MarketWidgetManager
        lateinit var marketWidgetRepository: MarketWidgetRepository
        lateinit var contactsRepository: ContactsRepository
        lateinit var subscriptionManager: SubscriptionManager
        lateinit var cexProviderManager: CexProviderManager
        lateinit var cexAssetManager: CexAssetManager
        lateinit var chartIndicatorManager: ChartIndicatorManager
        lateinit var backupProvider: BackupProvider
        lateinit var spamManager: SpamManager
    }

    override fun onCreate() {
        super.onCreate()

        if (!BuildConfig.DEBUG) {
            //Disable logging for lower levels in Release build
            Logger.getLogger("").level = Level.SEVERE
        }

        RxJavaPlugins.setErrorHandler { e: Throwable? ->
            Log.w("RxJava ErrorHandler", e)
        }

        EthereumKit.init()

        instance = this
        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        LocalStorageManager(preferences).apply {
            localStorage = this
            pinSettingsStorage = this
            lockoutStorage = this
            thirdKeyboardStorage = this
            marketStorage = this
        }

        val appConfig = AppConfigProvider(localStorage)
        appConfigProvider = appConfig

        torKitManager = TorManager(
            instance,
            localStorage
        )
        subscriptionManager = SubscriptionManager()

        marketKit = MarketKitWrapper(
            context = this,
            hsApiBaseUrl = appConfig.marketApiBaseUrl,
            hsApiKey = appConfig.marketApiKey,
            cryptoCompareApiKey = appConfig.cryptoCompareApiKey,
            defiYieldApiKey = appConfig.defiyieldProviderApiKey,
            appConfigProvider = appConfigProvider,
            subscriptionManager = subscriptionManager
        )
        marketKit.sync()

        feeRateProvider = FeeRateProvider(appConfigProvider)
        backgroundManager = BackgroundManager(this)

        appDatabase = AppDatabase.getInstance(this)

        blockchainSettingsStorage = BlockchainSettingsStorage(
            appDatabase
        )
        evmSyncSourceStorage = EvmSyncSourceStorage(appDatabase)
        evmSyncSourceManager = EvmSyncSourceManager(
            appConfigProvider,
            blockchainSettingsStorage,
            evmSyncSourceStorage
        )

        btcBlockchainManager = BtcBlockchainManager(
            blockchainSettingsStorage,
            appConfigProvider,
            marketKit
        )

        binanceKitManager = BinanceKitManager()

        accountsStorage = AccountsStorage(appDatabase)
        restoreSettingsStorage = RestoreSettingsStorage(appDatabase)

        AppLog.logsDao = appDatabase.logsDao()

        accountCleaner = AccountCleaner()
        accountManager = AccountManager(
            accountsStorage,
            accountCleaner
        )
        userManager = UserManager(accountManager)

        val proFeaturesStorage = ProFeaturesStorage(appDatabase)
        proFeatureAuthorizationManager = ProFeaturesAuthorizationManager(
            proFeaturesStorage,
            accountManager,
            appConfigProvider
        )

        enabledWalletsStorage = EnabledWalletsStorage(appDatabase)
        walletStorage = WalletStorage(
            marketKit,
            enabledWalletsStorage
        )

        walletManager = WalletManager(
            accountManager,
            walletStorage
        )
        coinManager = CoinManager(
            marketKit,
            walletManager
        )

        solanaRpcSourceManager = SolanaRpcSourceManager(
            blockchainSettingsStorage,
            marketKit
        )
        val solanaWalletManager = SolanaWalletManager(
            walletManager,
            accountManager,
            marketKit
        )
        solanaKitManager = SolanaKitManager(
            appConfigProvider,
            solanaRpcSourceManager, solanaWalletManager, backgroundManager
        )

        tronKitManager = TronKitManager(appConfigProvider, backgroundManager)

        wordsManager = WordsManager(Mnemonic())
        networkManager = NetworkManager()
        accountFactory = AccountFactory(
            accountManager,
            userManager
        )
        backupManager = BackupManager(accountManager)


        KeyStoreManager(
            keyAlias = "MASTER_KEY",
            keyStoreCleaner = KeyStoreCleaner(
                localStorage,
                accountManager,
                walletManager
            ),
            logger = AppLogger("key-store")
        ).apply {
            keyStoreManager = this
            keyProvider = this
        }

        encryptionManager = EncryptionManager(keyProvider)

        walletActivator = WalletActivator(
            walletManager,
            marketKit
        )
        tokenAutoEnableManager = TokenAutoEnableManager(appDatabase.tokenAutoEnabledBlockchainDao())

        val evmAccountManagerFactory = EvmAccountManagerFactory(
            accountManager,
            walletManager,
            marketKit,
            tokenAutoEnableManager
        )
        evmBlockchainManager = EvmBlockchainManager(
            backgroundManager,
            evmSyncSourceManager,
            marketKit,
            evmAccountManagerFactory,
        )

        val tronAccountManager = TronAccountManager(
            accountManager,
            walletManager,
            marketKit,
            tronKitManager,
            tokenAutoEnableManager
        )
        tronAccountManager.start()

        systemInfoManager = SystemInfoManager(appConfigProvider)

        languageManager = LanguageManager()
        currencyManager = CurrencyManager(
            localStorage,
            appConfigProvider
        )
        numberFormatter = NumberFormatter(languageManager)

        connectivityManager = ConnectivityManager(backgroundManager)

        zcashBirthdayProvider = ZcashBirthdayProvider(this)
        restoreSettingsManager = RestoreSettingsManager(
            restoreSettingsStorage,
            zcashBirthdayProvider
        )

        evmLabelManager = EvmLabelManager(
            EvmLabelProvider(),
            appDatabase.evmAddressLabelDao(),
            appDatabase.evmMethodLabelDao(),
            appDatabase.syncerStateDao()
        )

        val adapterFactory = AdapterFactory(
            context = instance,
            btcBlockchainManager = btcBlockchainManager,
            evmBlockchainManager = evmBlockchainManager,
            evmSyncSourceManager = evmSyncSourceManager,
            binanceKitManager = binanceKitManager,
            solanaKitManager = solanaKitManager,
            tronKitManager = tronKitManager,
            backgroundManager = backgroundManager,
            restoreSettingsManager = restoreSettingsManager,
            coinManager = coinManager,
            evmLabelManager = evmLabelManager,
            localStorage = localStorage
        )
        adapterManager = AdapterManager(
            walletManager, adapterFactory,
            btcBlockchainManager,
            evmBlockchainManager,
            binanceKitManager,
            solanaKitManager,
            tronKitManager
        )
        transactionAdapterManager = TransactionAdapterManager(
            adapterManager, adapterFactory
        )

        feeCoinProvider = FeeTokenProvider(marketKit)

        pinComponent = com.monistoWallet.modules.pin.PinComponent(
            pinSettingsStorage = pinSettingsStorage,
            excludedActivityNames = listOf(
                KeyStoreActivity::class.java.name,
                LockScreenActivity::class.java.name,
                LauncherActivity::class.java.name,
            ),
            userManager = userManager,
            pinDbStorage = PinDbStorage(appDatabase.pinDao())
        )

        backgroundStateChangeListener =
            BackgroundStateChangeListener(systemInfoManager, keyStoreManager, pinComponent).apply {
                backgroundManager.registerListener(this)
            }

        rateAppManager = RateAppManager(
            walletManager,
            adapterManager,
            localStorage
        )

        wc2Manager = WC2Manager(
            accountManager,
            evmBlockchainManager
        )

        termsManager = TermsManager(localStorage)

        marketWidgetManager = MarketWidgetManager()
        marketFavoritesManager = MarketFavoritesManager(
            appDatabase,
            marketWidgetManager
        )

        marketWidgetRepository = MarketWidgetRepository(
            marketKit,
            marketFavoritesManager,
            MarketFavoritesMenuService(
                localStorage,
                marketWidgetManager
            ),
            TopNftCollectionsRepository(marketKit),
            TopNftCollectionsViewItemFactory(numberFormatter),
            TopPlatformsRepository(
                marketKit,
                currencyManager, "widget"
            ),
            currencyManager
        )

        releaseNotesManager = ReleaseNotesManager(
            systemInfoManager,
            localStorage,
            appConfigProvider
        )

        setAppTheme()

        val nftStorage = NftStorage(
            appDatabase.nftDao(),
            marketKit
        )
        nftMetadataManager = NftMetadataManager(
            marketKit,
            appConfigProvider, nftStorage
        )
        nftAdapterManager = NftAdapterManager(
            walletManager,
            evmBlockchainManager
        )
        nftMetadataSyncer = NftMetadataSyncer(
            nftAdapterManager,
            nftMetadataManager, nftStorage
        )

        initializeWalletConnectV2(appConfig)

        wc2Service = WC2Service()
        wc2SessionManager = WC2SessionManager(
            accountManager, WC2SessionStorage(
                appDatabase
            ),
            wc2Service,
            wc2Manager
        )

        baseTokenManager = BaseTokenManager(
            coinManager,
            localStorage
        )
        balanceViewTypeManager = BalanceViewTypeManager(localStorage)
        balanceHiddenManager = BalanceHiddenManager(localStorage, backgroundManager)

        contactsRepository = ContactsRepository(marketKit)
        cexProviderManager = CexProviderManager(accountManager)
        cexAssetManager = CexAssetManager(marketKit, appDatabase.cexAssetsDao())
        chartIndicatorManager = ChartIndicatorManager(
            appDatabase.chartIndicatorSettingsDao(),
            localStorage
        )

        backupProvider = BackupProvider(
            localStorage = localStorage,
            languageManager = languageManager,
            walletStorage = enabledWalletsStorage,
            settingsManager = restoreSettingsManager,
            accountManager = accountManager,
            accountFactory = accountFactory,
            walletManager = walletManager,
            restoreSettingsManager = restoreSettingsManager,
            blockchainSettingsStorage = blockchainSettingsStorage,
            evmBlockchainManager = evmBlockchainManager,
            marketFavoritesManager = marketFavoritesManager,
            balanceViewTypeManager = balanceViewTypeManager,
            appIconService = AppIconService(localStorage),
            themeService = ThemeService(localStorage),
            chartIndicatorManager = chartIndicatorManager,
            chartIndicatorSettingsDao = appDatabase.chartIndicatorSettingsDao(),
            balanceHiddenManager = balanceHiddenManager,
            baseTokenManager = baseTokenManager,
            launchScreenService = LaunchScreenService(localStorage),
            currencyManager = currencyManager,
            btcBlockchainManager = btcBlockchainManager,
            evmSyncSourceManager = evmSyncSourceManager,
            evmSyncSourceStorage = evmSyncSourceStorage,
            solanaRpcSourceManager = solanaRpcSourceManager,
            contactsRepository = contactsRepository
        )

        spamManager = SpamManager(localStorage)

        startTasks()

        startKoin {
            androidContext(this@App)
            modules(
                rootModule,
                registerModule,
                registerEmailVerificationModule,
                loginModule,
                loginEmailVerificationModule,
                cardFoundModule,
                cardTransactionsModule,
                mainCardModule,
                allCardsToOrderModule,
                payModule,
                changePasswordModule,
                changeEmailModule,
                logoutModule,
                recoverModule,
                cardDataManagerModule,
            )
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .components {
                add(SvgDecoder.Factory())
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    private fun initializeWalletConnectV2(appConfig: AppConfigProvider) {
        val projectId = appConfig.walletConnectProjectId
        val serverUrl = "wss://${appConfig.walletConnectUrl}?projectId=$projectId"
        val connectionType = ConnectionType.AUTOMATIC
        val appMetaData = Core.Model.AppMetaData(
            name = appConfig.walletConnectAppMetaDataName,
            description = "",
            url = appConfig.walletConnectAppMetaDataUrl,
            icons = listOf(appConfig.walletConnectAppMetaDataIcon),
            redirect = null,
        )

        CoreClient.initialize(
            metaData = appMetaData,
            relayServerUrl = serverUrl,
            connectionType = connectionType,
            application = this,
            onError = { (throwable) ->
                Log.w("AAA", "error", throwable)
            },
        )

        val init = Sign.Params.Init(core = CoreClient)
        SignClient.initialize(init) { (throwable) ->
            Log.w("AAA", "error", throwable)
        }
    }

    private fun setAppTheme() {
        val nightMode = when (localStorage.currentTheme) {
            ThemeType.Light -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeType.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeType.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        if (AppCompatDelegate.getDefaultNightMode() != nightMode) {
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }
    }

    override fun getWorkManagerConfiguration(): WorkConfiguration {
        return if (BuildConfig.DEBUG) {
            WorkConfiguration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .build()
        } else {
            WorkConfiguration.Builder()
                .setMinimumLoggingLevel(Log.ERROR)
                .build()
        }
    }

    override fun localizedContext(): Context {
        return localeAwareContext(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(localeAwareContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeAwareContext(this)
    }

    private fun startTasks() {
        Thread {
            rateAppManager.onAppLaunch()
            nftMetadataSyncer.start()
            pinComponent.initDefaultPinLevel()
            accountManager.clearAccounts()

            AppVersionManager(systemInfoManager, localStorage).apply { storeAppVersion() }

            if (MarketWidgetWorker.hasEnabledWidgets(instance)) {
                MarketWidgetWorker.enqueueWork(instance)
            } else {
                MarketWidgetWorker.cancel(instance)
            }

            evmLabelManager.sync()
            contactsRepository.initialize()

        }.start()
    }
}
