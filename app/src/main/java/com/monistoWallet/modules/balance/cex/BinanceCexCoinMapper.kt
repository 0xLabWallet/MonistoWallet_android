package com.monistoWallet.modules.balance.cex

import com.monistoWallet.core.customCoinPrefix
import com.monistoWallet.core.managers.MarketKitWrapper
import com.wallet0x.marketkit.models.Coin
import com.wallet0x.marketkit.models.TokenQuery

class BinanceCexCoinMapper(marketKit: MarketKitWrapper) {
    private val coins = marketKit.allCoins().map { it.uid to it }.toMap()

    fun getCoin(asset: String): Coin {
        return mapBinanceAssetToCoin[asset]
            ?.let { coinUid ->
                coins[coinUid]
            } ?: Coin(
            uid = "${TokenQuery.customCoinPrefix}${asset}",
            name = asset,
            code = asset
        )
    }

    companion object {
        private val mapBinanceAssetToCoin = mapOf(
            "USDT" to "tether",
            "BUSD" to "binance-usd",
            "AGIX" to "singularitynet",
            "SUSHI" to "sushi",
            "GMT" to "stepn",
            "CAKE" to "pancakeswap-token",
            "ETH" to "ethereum",
            "ETHW" to "ethereum-pow-iou",
            "BTC" to "bitcoin",
            "BNB" to "binancecoin",
            "SOL" to "solana",
            "QI" to "benqi",
            "BSW" to "biswap",
        )

    }

}
