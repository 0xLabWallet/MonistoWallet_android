package com.monistoWallet.core.providers

import com.monistoWallet.core.IFeeRateProvider
import com.wallet0x.feeratekit.FeeRateKit
import com.wallet0x.feeratekit.model.FeeProviderConfig
import com.wallet0x.feeratekit.providers.MempoolSpaceProvider
import io.reactivex.Single
import kotlinx.coroutines.rx2.await
import java.math.BigInteger

class FeeRateProvider(appConfig: AppConfigProvider) {

    private val feeRateKit: FeeRateKit by lazy {
        FeeRateKit(
            FeeProviderConfig(
                ethEvmUrl = "${appConfig.marketApiBaseUrl}/v1/ethereum-rpc/mainnet",
                ethEvmAuth = appConfig.marketApiKey,
                bscEvmUrl = FeeProviderConfig.defaultBscEvmUrl(),
                mempoolSpaceUrl = appConfig.mempoolSpaceUrl
            )
        )
    }

    fun bitcoinFeeRate(): Single<MempoolSpaceProvider.RecommendedFees> {
        return feeRateKit.bitcoin()
    }

    fun litecoinFeeRate(): Single<BigInteger> {
        return feeRateKit.litecoin()
    }

    fun bitcoinCashFeeRate(): Single<BigInteger> {
        return feeRateKit.bitcoinCash()
    }

    fun dashFeeRate(): Single<BigInteger> {
        return feeRateKit.dash()
    }

}

class BitcoinFeeRateProvider(private val feeRateProvider: FeeRateProvider) : IFeeRateProvider {
    override val feeRateChangeable = true

    override suspend fun getFeeRates(): FeeRates {
        val bitcoinFeeRate = feeRateProvider.bitcoinFeeRate().await()
        return FeeRates(bitcoinFeeRate.halfHourFee, bitcoinFeeRate.minimumFee)
    }
}

class LitecoinFeeRateProvider(private val feeRateProvider: FeeRateProvider) : IFeeRateProvider {
    override suspend fun getFeeRates(): FeeRates {
        val feeRate = feeRateProvider.litecoinFeeRate().await()
        return FeeRates(feeRate.toInt())
    }
}

class BitcoinCashFeeRateProvider(private val feeRateProvider: FeeRateProvider) : IFeeRateProvider {
    override suspend fun getFeeRates(): FeeRates {
        val feeRate = feeRateProvider.bitcoinCashFeeRate().await()
        return FeeRates(feeRate.toInt())
    }
}

class DashFeeRateProvider(private val feeRateProvider: FeeRateProvider) : IFeeRateProvider {
    override suspend fun getFeeRates(): FeeRates {
        val feeRate = feeRateProvider.dashFeeRate().await()
        return FeeRates(feeRate.toInt())
    }
}

class ECashFeeRateProvider : IFeeRateProvider {
    override suspend fun getFeeRates(): FeeRates {
        return FeeRates(1)
    }
}

data class FeeRates(
    val recommended: Int,
    val minimum: Int = 0,
)