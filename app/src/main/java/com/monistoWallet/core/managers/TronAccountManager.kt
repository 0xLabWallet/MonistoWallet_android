package com.monistoWallet.core.managers

import com.monistoWallet.core.AppLogger
import com.monistoWallet.core.IAccountManager
import com.monistoWallet.core.IWalletManager
import com.monistoWallet.entities.EnabledWallet
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.TokenQuery
import com.wallet0x.marketkit.models.TokenType
import com.wallet0x.tronkit.TronKit
import com.wallet0x.tronkit.decoration.NativeTransactionDecoration
import com.wallet0x.tronkit.decoration.UnknownTransactionDecoration
import com.wallet0x.tronkit.decoration.trc20.Trc20TransferEvent
import com.wallet0x.tronkit.models.FullTransaction
import com.wallet0x.tronkit.models.TransferContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.util.concurrent.Executors

class TronAccountManager(
    private val accountManager: IAccountManager,
    private val walletManager: IWalletManager,
    private val marketKit: MarketKitWrapper,
    private val tronKitManager: TronKitManager,
    private val tokenAutoEnableManager: TokenAutoEnableManager
) {
    private val logger = AppLogger("tron-account-manager")
    private val blockchainType = BlockchainType.Tron
    private val singleDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val singleDispatcherCoroutineScope = CoroutineScope(singleDispatcher)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var transactionSubscriptionJob: Job? = null

    fun start() {
        singleDispatcherCoroutineScope.launch {
            tronKitManager.kitStartedFlow
                .collect { started ->
                    handleStarted(started)
                }
        }
    }

    private suspend fun handleStarted(started: Boolean) {
        try {
            if (started) {
                subscribeToTransactions()
            } else {
                stop()
            }
        } catch (exception: Exception) {
            logger.warning("error", exception)
        }
    }

    private fun stop() {
        transactionSubscriptionJob?.cancel()

    }

    private suspend fun subscribeToTransactions() {
        val tronKitWrapper = tronKitManager.tronKitWrapper ?: return
        val account = accountManager.activeAccount ?: return

        transactionSubscriptionJob = coroutineScope.launch {
            tronKitWrapper.tronKit.transactionsFlow
                .collect { (fullTransactions, initial) ->
                    handle(fullTransactions, account, tronKitWrapper, initial)
                }
        }
    }

    private fun handle(fullTransactions: List<FullTransaction>, account: com.monistoWallet.entities.Account, tronKitWrapper: TronKitWrapper, initial: Boolean) {
        val shouldAutoEnableTokens = tokenAutoEnableManager.isAutoEnabled(account, blockchainType)

        if (initial && account.origin == com.monistoWallet.entities.AccountOrigin.Restored && !account.isWatchAccount && !shouldAutoEnableTokens) {
            return
        }

        val address = tronKitWrapper.tronKit.address
        val foundTokens = mutableSetOf<FoundToken>()
        val suspiciousTokenTypes = mutableSetOf<TokenType>()

        for (fullTransaction in fullTransactions) {
            when (val decoration = fullTransaction.decoration) {
                is NativeTransactionDecoration -> {
                    when (decoration.contract) {
                        is TransferContract -> {
                            foundTokens.add(FoundToken(TokenType.Native))
                        }

                        else -> {}
                    }
                }

                is UnknownTransactionDecoration -> {
                    if (decoration.internalTransactions.any { it.to == address }) {
                        foundTokens.add(FoundToken(TokenType.Native))
                    }

                    for (event in decoration.events) {
                        if (event !is Trc20TransferEvent) continue

                        if (event.to == address) {
                            val tokenType = TokenType.Eip20(event.contractAddress.base58)

                            if (decoration.fromAddress == address) {
                                foundTokens.add(FoundToken(tokenType, event.tokenInfo))
                            } else {
                                suspiciousTokenTypes.add(tokenType)
                            }
                        }
                    }
                }
            }
        }

        handle(
            foundTokens = foundTokens.toList(),
            suspiciousTokenTypes = suspiciousTokenTypes.minus(foundTokens.map { it.tokenType }.toSet()).toList(),
            account = account,
            tronKit = tronKitWrapper.tronKit
        )
    }

    private fun handle(
        foundTokens: List<FoundToken>,
        suspiciousTokenTypes: List<TokenType>,
        account: com.monistoWallet.entities.Account,
        tronKit: TronKit
    ) {
        if (foundTokens.isEmpty() && suspiciousTokenTypes.isEmpty()) return

        try {
            val queries = (foundTokens.map { it.tokenType } + suspiciousTokenTypes).map { TokenQuery(blockchainType, it) }
            val tokens = marketKit.tokens(queries)
            val tokenInfos = mutableListOf<TokenInfo>()

            foundTokens.forEach { foundToken ->
                val token = tokens.firstOrNull { it.type == foundToken.tokenType }
                if (token != null) {
                    tokenInfos.add(
                        TokenInfo(
                            type = foundToken.tokenType,
                            coinName = token.coin.name,
                            coinCode = token.coin.code,
                            tokenDecimals = token.decimals
                        )
                    )
                } else if (foundToken.tokenInfo != null) {
                    tokenInfos.add(
                        TokenInfo(
                            type = foundToken.tokenType,
                            coinName = foundToken.tokenInfo.tokenName,
                            coinCode = foundToken.tokenInfo.tokenSymbol,
                            tokenDecimals = foundToken.tokenInfo.tokenDecimal
                        )
                    )
                }
            }

            suspiciousTokenTypes.forEach { tokenType ->
                val token = tokens.firstOrNull { it.type == tokenType }
                if (token != null) {
                    tokenInfos.add(
                        TokenInfo(
                            type = tokenType,
                            coinName = token.coin.name,
                            coinCode = token.coin.code,
                            tokenDecimals = token.decimals
                        )
                    )
                }
            }

            coroutineScope.launch {
                handle(tokenInfos, account, tronKit)
            }
        } catch (ex: Exception) {

        }
    }

    private suspend fun handle(tokenInfos: List<TokenInfo>, account: com.monistoWallet.entities.Account, tronKit: TronKit) = withContext(Dispatchers.IO) {
        val existingWallets = walletManager.activeWallets
        val existingTokenTypeIds = existingWallets.map { it.token.type.id }
        val newTokenInfos = tokenInfos.filter { !existingTokenTypeIds.contains(it.type.id) }

        if (newTokenInfos.isEmpty()) return@withContext

        val tokensWithBalance = newTokenInfos.mapNotNull { tokenInfo ->
            when (val tokenType = tokenInfo.type) {
                TokenType.Native -> {
                    tokenInfo
                }

                is TokenType.Eip20 -> {
                    if (tronKit.getTrc20Balance(tokenType.address) > BigInteger.ZERO) {
                        tokenInfo
                    } else {
                        null
                    }
                }

                else -> {
                    null
                }
            }
        }

        val enabledWallets = tokensWithBalance.map { tokenInfo ->
            EnabledWallet(
                tokenQueryId = TokenQuery(blockchainType, tokenInfo.type).id,
                accountId = account.id,
                coinName = tokenInfo.coinName,
                coinCode = tokenInfo.coinCode,
                coinDecimals = tokenInfo.tokenDecimals
            )
        }

        if (enabledWallets.isNotEmpty() && isActive) {
            walletManager.saveEnabledWallets(enabledWallets)
        }
    }

    data class TokenInfo(
        val type: TokenType,
        val coinName: String,
        val coinCode: String,
        val tokenDecimals: Int
    )

    data class FoundToken(
        val tokenType: TokenType,
        val tokenInfo: com.wallet0x.tronkit.decoration.TokenInfo? = null
    ) {
        override fun equals(other: Any?): Boolean {
            return other is FoundToken && tokenType.id == other.tokenType.id
        }

        override fun hashCode(): Int {
            return tokenType.id.hashCode()
        }
    }

}
