package com.monistoWallet.core.managers

import android.os.Handler
import android.os.Looper
import com.monistoWallet.core.App
import com.monistoWallet.core.UnsupportedAccountException
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.core.supportedNftTypes
import com.monistoWallet.entities.Account
import com.monistoWallet.entities.AccountType
import com.monistoWallet.core.BackgroundManager
import com.wallet0x.erc20kit.core.Erc20Kit
import com.wallet0x.ethereumkit.core.EthereumKit
import com.wallet0x.ethereumkit.core.signer.Signer
import com.wallet0x.ethereumkit.models.Address
import com.wallet0x.ethereumkit.models.Chain
import com.wallet0x.ethereumkit.models.FullTransaction
import com.wallet0x.ethereumkit.models.GasPrice
import com.wallet0x.ethereumkit.models.RpcSource
import com.wallet0x.ethereumkit.models.TransactionData
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.nftkit.core.NftKit
import com.wallet0x.nftkit.models.NftType
import com.wallet0x.oneinchkit.OneInchKit
import com.wallet0x.uniswapkit.TokenFactory.UnsupportedChainError
import com.wallet0x.uniswapkit.UniswapKit
import com.wallet0x.uniswapkit.UniswapV3Kit
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.lang.reflect.InvocationTargetException
import java.net.URI

class EvmKitManager(
    val chain: Chain,
    backgroundManager: BackgroundManager,
    private val syncSourceManager: EvmSyncSourceManager
) : BackgroundManager.Listener {

    private val disposables = CompositeDisposable()

    init {
        backgroundManager.registerListener(this)

        syncSourceManager.syncSourceObservable
            .subscribeIO { blockchain ->
                handleUpdateNetwork(blockchain)
            }
            .let {
                disposables.add(it)
            }
    }

    private fun handleUpdateNetwork(blockchainType: BlockchainType) {
        if (blockchainType != evmKitWrapper?.blockchainType) return

        stopEvmKit()

        evmKitUpdatedSubject.onNext(Unit)
    }

    private val kitStartedSubject = BehaviorSubject.createDefault(false)
    val kitStartedObservable: Observable<Boolean> = kitStartedSubject

    var evmKitWrapper: EvmKitWrapper? = null
        private set(value) {
            field = value

            kitStartedSubject.onNext(value != null)
        }

    private var useCount = 0
    var currentAccount: Account? = null
        private set
    private val evmKitUpdatedSubject = PublishSubject.create<Unit>()

    val evmKitUpdatedObservable: Observable<Unit>
        get() = evmKitUpdatedSubject

    val statusInfo: Map<String, Any>?
        get() = evmKitWrapper?.evmKit?.statusInfo()

    @Synchronized
    fun getEvmKitWrapper(account: Account, blockchainType: BlockchainType): EvmKitWrapper {
        if (evmKitWrapper != null && currentAccount != account) {
            stopEvmKit()
        }

        if (this.evmKitWrapper == null) {
            val accountType = account.type
            evmKitWrapper = createKitInstance(accountType, account, blockchainType)
            useCount = 0
            currentAccount = account
        }

        useCount++
        return this.evmKitWrapper!!
    }

    private fun createKitInstance(
        accountType: AccountType,
        account: Account,
        blockchainType: BlockchainType
    ): EvmKitWrapper {
        val syncSource = syncSourceManager.getSyncSource(blockchainType)

        val address: Address
        var signer: Signer? = null

        when (accountType) {
            is AccountType.Mnemonic -> {
                val seed: ByteArray = accountType.seed
                address = Signer.address(seed, chain)
                signer = Signer.getInstance(seed, chain)
            }
            is AccountType.EvmPrivateKey -> {
                address = Signer.address(accountType.key)
                signer = Signer.getInstance(accountType.key, chain)
            }
            is AccountType.EvmAddress -> {
                address = Address(accountType.address)
            }
            else -> throw UnsupportedAccountException()
        }

        val evmKit = EthereumKit.getInstance(
            App.instance,
            address,
            chain,
            syncSource.rpcSource,
            syncSource.transactionSource,
            account.id
        )

        Erc20Kit.addTransactionSyncer(evmKit)
        Erc20Kit.addDecorators(evmKit)

        UniswapKit.addDecorators(evmKit)
        try {
            UniswapV3Kit.addDecorators(evmKit)
        } catch (e: UnsupportedChainError.NoWethAddress) {
            //do nothing
        }
        OneInchKit.addDecorators(evmKit)

        var nftKit: NftKit? = null
        val supportedNftTypes = blockchainType.supportedNftTypes
        if (supportedNftTypes.isNotEmpty()) {
            val nftKitInstance = NftKit.getInstance(App.instance, evmKit)
            supportedNftTypes.forEach {
                when (it) {
                    NftType.Eip721 -> {
                        nftKitInstance.addEip721TransactionSyncer()
                        nftKitInstance.addEip721Decorators()
                    }
                    NftType.Eip1155 -> {
                        nftKitInstance.addEip1155TransactionSyncer()
                        nftKitInstance.addEip1155Decorators()
                    }
                }
            }
            nftKit = nftKitInstance
        }

        evmKit.start()

        return EvmKitWrapper(evmKit, nftKit, blockchainType, signer)
    }

    @Synchronized
    fun unlink(account: Account) {
        if (account == currentAccount) {
            useCount -= 1

            if (useCount < 1) {
                stopEvmKit()
            }
        }
    }

    private fun stopEvmKit() {
        evmKitWrapper?.evmKit?.stop()
        evmKitWrapper = null
        currentAccount = null
    }

    //
    // BackgroundManager.Listener
    //

    override fun willEnterForeground() {
        this.evmKitWrapper?.evmKit?.let { kit ->
            Handler(Looper.getMainLooper()).postDelayed({
                kit.refresh()
            }, 1000)
        }
    }

    override fun didEnterBackground() = Unit
}

val RpcSource.uris: List<URI>
    get() = when (this) {
        is RpcSource.WebSocket -> listOf(uri)
        is RpcSource.Http -> uris
    }

class EvmKitWrapper(
    val evmKit: EthereumKit,
    val nftKit: NftKit?,
    val blockchainType: BlockchainType,
    val signer: Signer?
) {

    fun sendSingle(
        transactionData: TransactionData,
        gasPrice: GasPrice,
        gasLimit: Long,
        nonce: Long?
    ): Single<FullTransaction> {
        return if (signer != null) {
            evmKit.rawTransaction(transactionData, gasPrice, gasLimit, nonce)
                .flatMap { rawTransaction ->
                    val signature = signer.signature(rawTransaction)
                    evmKit.send(rawTransaction, signature)
                }
        } else {
            Single.error(Exception())
        }
    }

}
