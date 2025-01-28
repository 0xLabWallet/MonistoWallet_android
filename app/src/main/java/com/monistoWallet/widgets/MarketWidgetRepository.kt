package com.monistoWallet.widgets

import com.monistoWallet.R
import com.monistoWallet.core.iconUrl
import com.monistoWallet.core.imageUrl
import com.monistoWallet.core.managers.CurrencyManager
import com.monistoWallet.core.managers.MarketFavoritesManager
import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.modules.market.favorites.MarketFavoritesMenuService
import com.monistoWallet.modules.market.sort
import com.monistoWallet.modules.market.topnftcollections.TopNftCollectionsRepository
import com.monistoWallet.modules.market.topnftcollections.TopNftCollectionsViewItemFactory
import com.monistoWallet.modules.market.topplatforms.TopPlatformsRepository
import kotlinx.coroutines.rx2.await
import java.math.BigDecimal

class MarketWidgetRepository(
    private val marketKit: MarketKitWrapper,
    private val favoritesManager: MarketFavoritesManager,
    private val favoritesMenuService: MarketFavoritesMenuService,
    private val topNftCollectionsRepository: TopNftCollectionsRepository,
    private val topNftCollectionsViewItemFactory: TopNftCollectionsViewItemFactory,
    private val topPlatformsRepository: TopPlatformsRepository,
    private val currencyManager: CurrencyManager
) {
    companion object {
        private const val topGainers = 100
        private const val itemsLimit = 5
    }

    private val currency
        get() = currencyManager.baseCurrency

    suspend fun getMarketItems(marketWidgetType: MarketWidgetType): List<MarketWidgetItem> =
        when (marketWidgetType) {
            MarketWidgetType.Watchlist -> {
                getWatchlist()
            }
            MarketWidgetType.TopGainers -> {
                getTopGainers()
            }
            MarketWidgetType.TopNfts -> {
                getTopNtfs()
            }
            MarketWidgetType.TopPlatforms -> {
                getTopPlatforms()
            }
        }

    private suspend fun getTopPlatforms(): List<MarketWidgetItem> {
        val platformItems = topPlatformsRepository.get(
            sortingField = com.monistoWallet.modules.market.SortingField.HighestCap,
            timeDuration = com.monistoWallet.modules.market.TimeDuration.OneDay,
            forceRefresh = true,
            limit = itemsLimit
        )
        return platformItems.map { item ->
            MarketWidgetItem(
                uid = item.platform.uid,
                title = item.platform.name,
                subtitle = Translator.getString(R.string.MarketTopPlatforms_Protocols, item.protocols),
                label = item.rank.toString(),
                value = com.monistoWallet.core.App.numberFormatter.formatFiatShort(
                    item.marketCap,
                    currency.symbol,
                    2
                ),
                diff = item.changeDiff,
                marketCap = null,
                volume = null,
                blockchainTypeUid = null,
                imageRemoteUrl = item.platform.iconUrl
            )
        }
    }

    private suspend fun getTopNtfs(): List<MarketWidgetItem> {
        val nftCollectionViewItems = topNftCollectionsRepository.get(
            sortingField = com.monistoWallet.modules.market.SortingField.HighestVolume,
            timeDuration = com.monistoWallet.modules.market.TimeDuration.SevenDay,
            forceRefresh = true,
            limit = itemsLimit
        ).mapIndexed { index, item ->
            topNftCollectionsViewItemFactory.viewItem(item, com.monistoWallet.modules.market.TimeDuration.SevenDay, index + 1)
        }

        return nftCollectionViewItems.map {
            MarketWidgetItem(
                uid = it.uid,
                title = it.name,
                subtitle = it.floorPrice,
                label = it.order.toString(),
                value = it.volume,
                marketCap = null,
                diff = it.volumeDiff,
                volume = null,
                blockchainTypeUid = it.blockchainType.uid,
                imageRemoteUrl = it.imageUrl ?: ""
            )
        }
    }

    private suspend fun getTopGainers(): List<MarketWidgetItem> {
        val marketItems = marketKit.marketInfosSingle(topGainers, currency.code, false, "widget")
            .await()
            .map { com.monistoWallet.modules.market.MarketItem.createFromCoinMarket(it, currency) }

        val sortedMarketItems = marketItems
            .subList(0, Integer.min(marketItems.size, topGainers))
            .sort(com.monistoWallet.modules.market.SortingField.TopGainers)
            .subList(0, Integer.min(marketItems.size, itemsLimit))

        return sortedMarketItems.map { marketWidgetItem(it, com.monistoWallet.modules.market.MarketField.PriceDiff) }
    }

    private suspend fun getWatchlist(): List<MarketWidgetItem> {
        val favoriteCoins = favoritesManager.getAll()
        var marketItems = listOf<com.monistoWallet.modules.market.MarketItem>()

        if (favoriteCoins.isNotEmpty()) {
            val favoriteCoinUids = favoriteCoins.map { it.coinUid }
            marketItems = marketKit.marketInfosSingle(favoriteCoinUids, currency.code, "widget")
                .await()
                .map { marketInfo ->
                    com.monistoWallet.modules.market.MarketItem.createFromCoinMarket(marketInfo, currency)
                }
                .sort(favoritesMenuService.sortingField)
        }

        return marketItems.map { marketWidgetItem(it, favoritesMenuService.marketField) }
    }

    private fun marketWidgetItem(
        marketItem: com.monistoWallet.modules.market.MarketItem,
        marketField: com.monistoWallet.modules.market.MarketField,
    ): MarketWidgetItem {
        var marketCap: String? = null
        var volume: String? = null
        var diff: BigDecimal? = null

        when (marketField) {
            com.monistoWallet.modules.market.MarketField.MarketCap -> {
                marketCap = com.monistoWallet.core.App.numberFormatter.formatFiatShort(marketItem.marketCap.value, marketItem.marketCap.currency.symbol, 2)
            }
            com.monistoWallet.modules.market.MarketField.Volume -> {
                volume = com.monistoWallet.core.App.numberFormatter.formatFiatShort(marketItem.volume.value, marketItem.volume.currency.symbol, 2)
            }
            com.monistoWallet.modules.market.MarketField.PriceDiff -> {
                diff = marketItem.diff
            }
        }

        return MarketWidgetItem(
            uid = marketItem.fullCoin.coin.uid,
            title = marketItem.fullCoin.coin.name,
            subtitle = marketItem.fullCoin.coin.code,
            label = marketItem.fullCoin.coin.marketCapRank?.toString() ?: "",
            value = com.monistoWallet.core.App.numberFormatter.formatFiatFull(
                marketItem.rate.value,
                marketItem.rate.currency.symbol
            ),
            marketCap = marketCap,
            volume = volume,
            diff = diff,
            blockchainTypeUid = null,
            imageRemoteUrl = marketItem.fullCoin.coin.imageUrl
        )
    }

}
