package com.monistoWallet.modules.market.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.ChartData
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.managers.CurrencyManager
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.CoinValue
import com.monistoWallet.entities.Currency
import com.monistoWallet.entities.ViewState
import com.monistoWallet.models.ChartPoint
import com.monistoWallet.modules.market.MarketField
import com.monistoWallet.modules.market.MarketItem
import com.monistoWallet.modules.market.MarketModule.ListType
import com.monistoWallet.modules.market.MarketViewItem
import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.TimeDuration
import com.monistoWallet.modules.market.TopMarket
import com.monistoWallet.modules.market.overview.MarketOverviewModule.Board
import com.monistoWallet.modules.market.overview.MarketOverviewModule.BoardHeader
import com.monistoWallet.modules.market.overview.MarketOverviewModule.MarketMetrics
import com.monistoWallet.modules.market.overview.MarketOverviewModule.MarketMetricsPoint
import com.monistoWallet.modules.market.overview.MarketOverviewModule.TopNftCollectionsBoard
import com.monistoWallet.modules.market.overview.MarketOverviewModule.TopPlatformsBoard
import com.monistoWallet.modules.market.overview.MarketOverviewModule.TopSectorsBoard
import com.monistoWallet.modules.market.overview.TopSectorsRepository.Companion.getCategoryMarketData
import com.monistoWallet.modules.market.search.MarketSearchModule.DiscoveryItem.Category
import com.monistoWallet.modules.market.topnftcollections.TopNftCollectionsViewItemFactory
import com.monistoWallet.modules.market.topplatforms.TopPlatformItem
import com.monistoWallet.modules.market.topplatforms.TopPlatformViewItem
import com.monistoWallet.modules.market.topplatforms.TopPlatformsRepository
import com.monistoWallet.modules.metricchart.MetricsType
import com.monistoWallet.modules.nft.NftCollectionItem
import com.monistoWallet.modules.nft.nftCollectionItem
import com.monistoWallet.ui.compose.Select
import com.monistoWallet.ui.extensions.MetricData
import com.wallet0x.marketkit.models.GlobalMarketPoint
import com.wallet0x.marketkit.models.HsTimePeriod
import com.wallet0x.marketkit.models.MarketOverview
import com.wallet0x.marketkit.models.NftPrice
import com.wallet0x.marketkit.models.TopMovers
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

class MarketOverviewViewModel(
    private val service: MarketOverviewService,
    private val topNftCollectionsViewItemFactory: TopNftCollectionsViewItemFactory,
    private val currencyManager: CurrencyManager
) : ViewModel() {

    private val disposables = CompositeDisposable()

    val viewStateLiveData = MutableLiveData<ViewState>(ViewState.Loading)
    val viewItem = MutableLiveData<MarketOverviewModule.ViewItem>()
    val isRefreshingLiveData = MutableLiveData<Boolean>()

    val topNftCollectionsParams: Pair<SortingField, TimeDuration>
        get() = Pair(topNftsSortingField, topNftsTimeDuration)

    var gainersTopMarket: TopMarket = TopMarket.Top100
        private set
    var losersTopMarket: TopMarket = TopMarket.Top100
        private set
    var topNftsTimeDuration: TimeDuration = TimeDuration.SevenDay
        private set
    val topNftsSortingField: SortingField = SortingField.HighestVolume
    var topPlatformsTimeDuration: TimeDuration = TimeDuration.SevenDay
        private set

    var topMovers: TopMovers? = null
    var marketOverview: MarketOverview? = null

    val baseCurrency: Currency
        get() = currencyManager.baseCurrency

    private fun syncViewItems() {
        val topMovers = topMovers ?: return
        val marketOverview = marketOverview ?: return

        val viewItem = getViewItem(
            topMovers,
            marketOverview
        )
        this.viewItem.postValue(viewItem)
        viewStateLiveData.postValue(ViewState.Success)
    }


    init {
        Observable
            .combineLatest(
                service.topMoversObservable,
                service.marketOverviewObservable
            ) { t1, t2 ->
                Pair(t1, t2)
            }
            .subscribeIO { overviewItems ->
                val error = listOfNotNull(
                    overviewItems.first.exceptionOrNull(),
                    overviewItems.second.exceptionOrNull(),
                ).firstOrNull()

                if (error != null) {
                    viewStateLiveData.postValue(ViewState.Error(error))
                } else {
                    topMovers = overviewItems.first.getOrNull()
                    marketOverview = overviewItems.second.getOrNull()

                    if (
                        topMovers != null
                        && marketOverview != null
                    ) {
                        syncViewItems()
                    }
                }
            }.let {
                disposables.add(it)
            }

        service.start()
    }

    private fun getViewItem(
        topMovers: TopMovers,
        marketOverview: MarketOverview
    ): MarketOverviewModule.ViewItem {
        val topPlatformItems = TopPlatformsRepository.getTopPlatformItems(marketOverview.topPlatforms, topPlatformsTimeDuration)
        val coinCategoryItems = marketOverview.coinCategories.map { category ->
            Category(category, getCategoryMarketData(category, baseCurrency))
        }

        val timePeriod = when(topNftsTimeDuration) {
            TimeDuration.OneDay -> HsTimePeriod.Day1
            TimeDuration.SevenDay -> HsTimePeriod.Week1
            TimeDuration.ThirtyDay -> HsTimePeriod.Month1
            TimeDuration.ThreeMonths -> HsTimePeriod.Month3
        }
        val nftCollectionItems = marketOverview.nftCollections.getOrElse(timePeriod) { listOf() }.map { it.nftCollectionItem }

        val topGainersBoard = getBoard(ListType.TopGainers, topMovers)
        val topLosersBoard = getBoard(ListType.TopLosers, topMovers)

        return MarketOverviewModule.ViewItem(
            getMarketMetrics(marketOverview.globalMarketPoints, baseCurrency),
            listOf(topGainersBoard, topLosersBoard),
            topNftCollectionsBoard(nftCollectionItems),
//            topSectorsBoard(coinCategoryItems),
            topPlatformsBoard(topPlatformItems)
        )
    }

    private fun topSectorsBoard(items: List<Category>) =
        TopSectorsBoard(
            title = R.string.Market_Overview_Sectors,
            iconRes = R.drawable.ic_categories_20,
            items = items
        )

    private fun topNftCollectionsBoard(items: List<NftCollectionItem>) =
        TopNftCollectionsBoard(
            title = R.string.Nft_TopCollections,
            iconRes = R.drawable.ic_top_nft_collections_20,
            timeDurationSelect = Select(topNftsTimeDuration, service.timeDurationOptions),
            collections = items.mapIndexed { index, collection ->
                topNftCollectionsViewItemFactory.viewItem(collection, topNftsTimeDuration, index + 1)
            }
        )

    private fun topPlatformsBoard(items: List<TopPlatformItem>) =
        TopPlatformsBoard(
            title = R.string.MarketTopPlatforms_Title,
            iconRes = R.drawable.ic_blocks_20,
            timeDurationSelect = Select(
                topPlatformsTimeDuration,
                service.timeDurationOptions
            ),
            items = items.map { item ->
                TopPlatformViewItem(
                    platform = item.platform,
                    subtitle = Translator.getString(
                        R.string.MarketTopPlatforms_Protocols,
                        item.protocols
                    ),
                    marketCap = formatFiatShortened(item.marketCap, baseCurrency.symbol),
                    marketCapDiff = item.changeDiff,
                    rank = item.rank.toString(),
                    rankDiff = item.rankDiff,
                )
            }
        )

    private fun getBoard(type: ListType, topMovers: TopMovers): Board {
        val topMarket: TopMarket

        val marketInfoList = when (type) {
            ListType.TopGainers -> {
                topMarket = gainersTopMarket

                when (gainersTopMarket) {
                    TopMarket.Top100 -> topMovers.gainers100
                    TopMarket.Top200 -> topMovers.gainers200
                    TopMarket.Top300 -> topMovers.gainers300
                }
            }

            ListType.TopLosers -> {
                topMarket = losersTopMarket

                when (losersTopMarket) {
                    TopMarket.Top100 -> topMovers.losers100
                    TopMarket.Top200 -> topMovers.losers200
                    TopMarket.Top300 -> topMovers.losers300
                }
            }
        }

        val marketItems = marketInfoList.map { MarketItem.createFromCoinMarket(it, baseCurrency) }
        val topList = marketItems.map { MarketViewItem.create(it, type.marketField) }

        val boardHeader = BoardHeader(
            getSectionTitle(type),
            getSectionIcon(type),
            Select(topMarket, service.topMarketOptions)
        )
        return Board(boardHeader, topList, type)
    }

    private fun getMarketMetrics(globalMarketPoints: List<GlobalMarketPoint>, baseCurrency: Currency): MarketMetrics {
        var marketCap: BigDecimal? = null
        var marketCapDiff: BigDecimal? = null
        var defiMarketCap: BigDecimal? = null
        var defiMarketCapDiff: BigDecimal? = null
        var volume24h: BigDecimal? = null
        var volume24hDiff: BigDecimal? = null
        var tvl: BigDecimal? = null
        var tvlDiff: BigDecimal? = null

        if (globalMarketPoints.isNotEmpty()) {
            val startingPoint = globalMarketPoints.first()
            val endingPoint = globalMarketPoints.last()

            marketCap = endingPoint.marketCap
            marketCapDiff = diff(startingPoint.marketCap, marketCap)

            defiMarketCap = endingPoint.defiMarketCap
            defiMarketCapDiff = diff(startingPoint.defiMarketCap, defiMarketCap)

            volume24h = endingPoint.volume24h
            volume24hDiff = diff(startingPoint.volume24h, volume24h)

            tvl = endingPoint.tvl
            tvlDiff = diff(startingPoint.tvl, tvl)
        }

        val totalMarketCapPoints = globalMarketPoints.map { MarketMetricsPoint(it.marketCap, it.timestamp) }
        val volume24Points = globalMarketPoints.map { MarketMetricsPoint(it.volume24h, it.timestamp) }
        val defiMarketCapPoints = globalMarketPoints.map { MarketMetricsPoint(it.defiMarketCap, it.timestamp) }
        val defiTvlPoints = globalMarketPoints.map { MarketMetricsPoint(it.tvl, it.timestamp) }

        return MarketMetrics(
            totalMarketCap = MetricData(
                marketCap?.let { formatFiatShortened(it, baseCurrency.symbol) },
                marketCapDiff,
                getChartData(totalMarketCapPoints),
                MetricsType.TotalMarketCap
            ),
            volume24h = MetricData(
                volume24h?.let { formatFiatShortened(it, baseCurrency.symbol) },
                volume24hDiff,
                getChartData(volume24Points),
                MetricsType.Volume24h
            ),
            defiCap = MetricData(
                defiMarketCap?.let { formatFiatShortened(it, baseCurrency.symbol) },
                defiMarketCapDiff,
                getChartData(defiMarketCapPoints),
                MetricsType.DefiCap
            ),
            defiTvl = MetricData(
                tvl?.let { formatFiatShortened(it, baseCurrency.symbol) },
                tvlDiff,
                getChartData(defiTvlPoints),
                MetricsType.TvlInDefi
            )
        )
    }

    private fun getChartData(marketMetricsPoints: List<MarketMetricsPoint>): ChartData? {
        if (marketMetricsPoints.isEmpty()) return null

        val points = marketMetricsPoints.map { ChartPoint(it.value.toFloat(), it.timestamp) }
        return ChartData(points, true, false)
    }

    private fun formatFiatShortened(value: BigDecimal, symbol: String): String {
        return App.numberFormatter.formatFiatShort(value, symbol, 2)
    }

    private fun getSectionTitle(type: ListType): Int {
        return when (type) {
            ListType.TopGainers -> R.string.RateList_TopGainers
            ListType.TopLosers -> R.string.RateList_TopLosers
        }
    }

    private fun getSectionIcon(type: ListType): Int {
        return when (type) {
            ListType.TopGainers -> R.drawable.ic_circle_up_20
            ListType.TopLosers -> R.drawable.ic_circle_down_20
        }
    }

    private fun refreshWithMinLoadingSpinnerPeriod() {
        service.refresh()
        viewModelScope.launch {
            isRefreshingLiveData.postValue(true)
            delay(1000)
            isRefreshingLiveData.postValue(false)
        }
    }

    private fun diff(sourceValue: BigDecimal, targetValue: BigDecimal): BigDecimal =
        if (sourceValue.compareTo(BigDecimal.ZERO) != 0)
            ((targetValue - sourceValue) * BigDecimal(100)) / sourceValue
        else BigDecimal.ZERO

    fun onSelectTopMarket(topMarket: TopMarket, listType: ListType) {
        when (listType) {
            ListType.TopGainers -> {
                gainersTopMarket = topMarket
                syncViewItems()
            }
            ListType.TopLosers -> {
                losersTopMarket = topMarket
                syncViewItems()
            }
        }
    }

    fun onSelectTopNftsTimeDuration(timeDuration: TimeDuration) {
        topNftsTimeDuration = timeDuration
        syncViewItems()
    }

    fun onSelectTopPlatformsTimeDuration(timeDuration: TimeDuration) {
        topPlatformsTimeDuration = timeDuration
        syncViewItems()
    }

    fun onErrorClick() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun refresh() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun getTopCoinsParams(listType: ListType): Triple<SortingField, TopMarket, MarketField> {
        return when (listType) {
            ListType.TopGainers -> {
                Triple(SortingField.TopGainers, gainersTopMarket, MarketField.PriceDiff)
            }
            ListType.TopLosers -> {
                Triple(SortingField.TopLosers, losersTopMarket, MarketField.PriceDiff)
            }
        }
    }

    override fun onCleared() {
        service.stop()
        disposables.clear()
    }
}

val NftPrice.coinValue: CoinValue
    get() = CoinValue(token, value)
