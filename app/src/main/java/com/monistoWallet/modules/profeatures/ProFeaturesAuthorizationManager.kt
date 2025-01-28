package com.monistoWallet.modules.profeatures

import androidx.annotation.CheckResult
import com.monistoWallet.core.IAccountManager
import com.monistoWallet.core.orNull
import com.monistoWallet.core.providers.AppConfigProvider
import com.monistoWallet.core.storage.SecretString
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.Account
import com.monistoWallet.entities.AccountType
import com.monistoWallet.modules.profeatures.storage.ProFeaturesSessionKey
import com.monistoWallet.modules.profeatures.storage.ProFeaturesStorage
import com.wallet0x.ethereumkit.core.Eip1155Provider
import com.wallet0x.ethereumkit.core.signer.EthSigner
import com.wallet0x.ethereumkit.core.signer.Signer
import com.wallet0x.ethereumkit.crypto.CryptoUtils
import com.wallet0x.ethereumkit.crypto.EIP712Encoder
import com.wallet0x.ethereumkit.models.Address
import com.wallet0x.ethereumkit.models.Chain
import com.wallet0x.ethereumkit.models.RpcSource
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.util.*

class ProFeaturesAuthorizationManager(
    private val storage: ProFeaturesStorage,
    private val accountManager: IAccountManager,
    private val appConfigProvider: AppConfigProvider
) {

    data class AccountData(
        val id: String,
        val address: Address
    )

    private val disposable = CompositeDisposable()
    private val contractAddress = Address("0x495f947276749ce646f68ac8c248420045cb7b5e")

    private val _sessionKeyFlow = MutableStateFlow<ProFeaturesSessionKey?>(null)
    val sessionKeyFlow = _sessionKeyFlow.asStateFlow()

    private val getAllAccountData: List<AccountData>
        get() {
            val accounts = mutableListOf<Account>()

            val activeAccount = accountManager.activeAccount
            activeAccount?.let { accounts.add(it) }

            val inactiveAccounts = accountManager.accounts.filter { it.id != activeAccount?.id }
            accounts.addAll(inactiveAccounts)

            return accounts.mapNotNull { (id, _, type) ->
                when (type) {
                    is AccountType.EvmPrivateKey -> {
                        val address = Signer.address(type.key)
                        AccountData(id, address)
                    }

                    is AccountType.Mnemonic -> {
                        val address = Signer.address(type.seed, Chain.Ethereum)
                        AccountData(id, address)
                    }

                    else -> null
                }
            }
        }

    init {
        accountManager.accountsDeletedFlowable
            .subscribeIO {
                handleDeletedAccounts()
            }
            .let {
                disposable.add(it)
            }
    }

    fun getSessionKey(nftType: ProNft): ProFeaturesSessionKey? =
        storage.get(nftType)

    fun saveSessionKey(nft: ProNft, accountData: AccountData, key: String) {
        val sessionKey = ProFeaturesSessionKey(
            nft.keyName,
            accountData.id,
            accountData.address.eip55,
            SecretString(key)
        )

        storage.add(sessionKey)
        _sessionKeyFlow.update { sessionKey }
    }

    suspend fun getNFTHolderAccountData(nftType: ProNft): AccountData? =
        withContext(Dispatchers.IO) {
            val accounts = getAllAccountData
            val provider = Eip1155Provider.instance(
                RpcSource.ethereumInfuraHttp(
                    appConfigProvider.infuraProjectId,
                    appConfigProvider.infuraProjectSecret
                )
            )

            return@withContext first1155TokenHolder(
                provider,
                nftType.tokenId,
                accounts
            ).await().orNull
        }

    fun signMessage(accountData: AccountData, message: String): ByteArray {
        val account = accountManager.account(accountData.id) ?: throw Exception("Account not found")
        val privateKey = when (account.type) {
            is AccountType.EvmPrivateKey -> {
                account.type.key
            }

            is AccountType.Mnemonic -> {
                Signer.privateKey(account.type.seed, Chain.Ethereum)
            }

            else -> throw Exception("AccountType not supported")
        }

        val ethSigner = EthSigner(privateKey, CryptoUtils, EIP712Encoder())

        return ethSigner.signByteArray(message.toByteArray(Charsets.UTF_8))
    }

    private fun first1155TokenHolder(
        provider: Eip1155Provider,
        tokenId: BigInteger,
        accounts: List<AccountData>
    ): Single<Optional<AccountData>> {
        val firstAccount = accounts.firstOrNull() ?: return Single.just(Optional.ofNullable(null))

        return provider.getTokenBalance(contractAddress, tokenId, firstAccount.address)
            .flatMap { balance ->
                if (balance > BigInteger.ZERO) return@flatMap Single.just(Optional.of(firstAccount))

                return@flatMap first1155TokenHolder(
                    provider,
                    tokenId,
                    accounts.subList(1, accounts.size)
                )
            }
    }

    private fun handleDeletedAccounts() {
        val accountIds = accountManager.accounts.map { it.id }

        storage.deleteAllExcept(accountIds)
    }

}
