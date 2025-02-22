package com.monistoWallet.modules.balance.token

import com.monistoWallet.core.Clearable
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.Wallet
import com.monistoWallet.modules.balance.BalanceAdapterRepository
import com.monistoWallet.modules.balance.BalanceModule
import com.monistoWallet.modules.balance.BalanceXRateRepository
import com.wallet0x.marketkit.models.CoinPrice
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TokenBalanceService(
    private val wallet: Wallet,
    private val xRateRepository: BalanceXRateRepository,
    private val balanceAdapterRepository: com.monistoWallet.modules.balance.BalanceAdapterRepository
) : Clearable {

    private val _balanceItemFlow = MutableStateFlow<BalanceModule.BalanceItem?>(null)
    val balanceItemFlow = _balanceItemFlow.asStateFlow()

    var balanceItem: BalanceModule.BalanceItem? = null
        private set(value) {
            field = value

            _balanceItemFlow.update { value }
        }

    private val disposables = CompositeDisposable()

    val baseCurrency by xRateRepository::baseCurrency

    fun start() {
        balanceAdapterRepository.setWallet(listOf(wallet))
        xRateRepository.setCoinUids(listOf(wallet.coin.uid))

        val latestRates = xRateRepository.getLatestRates()

        balanceItem = BalanceModule.BalanceItem(
            wallet = wallet,
            balanceData = balanceAdapterRepository.balanceData(wallet),
            state = balanceAdapterRepository.state(wallet),
            sendAllowed = balanceAdapterRepository.sendAllowed(wallet),
            coinPrice = latestRates[wallet.coin.uid]
        )

        xRateRepository.itemObservable
            .subscribeIO {
                handleXRateUpdate(it)
            }
            .let {
                disposables.add(it)
            }

        balanceAdapterRepository.readyObservable
            .subscribeIO {
                handleAdapterUpdate()
            }
            .let {
                disposables.add(it)
            }

        balanceAdapterRepository.updatesObservable
            .subscribeIO {
                handleAdapterUpdate()
            }
            .let {
                disposables.add(it)
            }

    }

    private fun handleXRateUpdate(latestRates: Map<String, CoinPrice?>) {
        balanceItem = balanceItem?.copy(
            coinPrice = latestRates[wallet.coin.uid]
        )
    }

    private fun handleAdapterUpdate() {
        balanceItem = balanceItem?.copy(
            balanceData = balanceAdapterRepository.balanceData(wallet),
            state = balanceAdapterRepository.state(wallet),
            sendAllowed = balanceAdapterRepository.sendAllowed(wallet)
        )
    }

    override fun clear() {
        disposables.clear()
        balanceAdapterRepository.clear()
    }
}
