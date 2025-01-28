package com.monistoWallet.core.managers

import android.os.Handler
import android.os.Looper
import com.monistoWallet.core.BackgroundManager
import com.monistoWallet.core.UnsupportedAccountException
import com.monistoWallet.core.providers.AppConfigProvider
import com.wallet0x.tronkit.TronKit
import com.wallet0x.tronkit.models.Address
import com.wallet0x.tronkit.network.Network
import com.wallet0x.tronkit.transaction.Signer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TronKitManager(
    private val appConfigProvider: AppConfigProvider,
    backgroundManager: BackgroundManager
) : BackgroundManager.Listener {
    private val network = Network.Mainnet
    private val _kitStartedFlow = MutableStateFlow(false)
    val kitStartedFlow: StateFlow<Boolean> = _kitStartedFlow

    var tronKitWrapper: TronKitWrapper? = null
        private set(value) {
            field = value

            _kitStartedFlow.update { value != null }
        }

    private var useCount = 0
    var currentAccount: com.monistoWallet.entities.Account? = null
        private set

    val statusInfo: Map<String, Any>?
        get() = tronKitWrapper?.tronKit?.statusInfo()

    init {
        backgroundManager.registerListener(this)
    }

    @Synchronized
    fun getTronKitWrapper(account: com.monistoWallet.entities.Account): TronKitWrapper {
        if (this.tronKitWrapper != null && currentAccount != account) {
            stopKit()
        }

        if (this.tronKitWrapper == null) {
            val accountType = account.type
            this.tronKitWrapper = when (accountType) {
                is com.monistoWallet.entities.AccountType.Mnemonic -> {
                    createKitInstance(accountType, account)
                }

                is com.monistoWallet.entities.AccountType.TronAddress -> {
                    createKitInstance(accountType, account)
                }

                else -> throw UnsupportedAccountException()
            }
            startKit()
            useCount = 0
            currentAccount = account
        }

        useCount++
        return this.tronKitWrapper!!
    }

    private fun createKitInstance(
        accountType: com.monistoWallet.entities.AccountType.Mnemonic,
        account: com.monistoWallet.entities.Account
    ): TronKitWrapper {
        val seed = accountType.seed
        val signer = Signer.getInstance(seed, network)

        val kit = TronKit.getInstance(
            application = com.monistoWallet.core.App.instance,
            walletId = account.id,
            seed = seed,
            network = network,
            tronGridApiKeys = appConfigProvider.trongridApiKeys
        )

        return TronKitWrapper(kit, signer)
    }

    private fun createKitInstance(
        accountType: com.monistoWallet.entities.AccountType.TronAddress,
        account: com.monistoWallet.entities.Account
    ): TronKitWrapper {
        val address = accountType.address

        val kit = TronKit.getInstance(
            application = com.monistoWallet.core.App.instance,
            address = Address.fromBase58(address),
            network = network,
            walletId = account.id,
            tronGridApiKeys = appConfigProvider.trongridApiKeys
        )

        return TronKitWrapper(kit, null)
    }

    @Synchronized
    fun unlink(account: com.monistoWallet.entities.Account) {
        if (account == currentAccount) {
            useCount -= 1

            if (useCount < 1) {
                stopKit()
            }
        }
    }

    private fun stopKit() {
        tronKitWrapper?.tronKit?.stop()
        tronKitWrapper = null
        currentAccount = null
    }

    private fun startKit() {
        tronKitWrapper?.tronKit?.start()
    }

    //
    // BackgroundManager.Listener
    //

    override fun willEnterForeground() {
        this.tronKitWrapper?.tronKit?.let { kit ->
            Handler(Looper.getMainLooper()).postDelayed({
                kit.refresh()
            }, 1000)
        }
    }

    override fun didEnterBackground() = Unit
}

class TronKitWrapper(val tronKit: TronKit, val signer: Signer?)
