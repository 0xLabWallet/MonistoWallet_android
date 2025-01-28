package com.monistoWallet.modules.market.topnftcollections

import com.monistoWallet.modules.market.SortingField
import com.monistoWallet.modules.market.TimeDuration
import com.monistoWallet.modules.nft.NftCollectionItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class TopNftCollectionsService(
    sortingField: com.monistoWallet.modules.market.SortingField,
    timeDuration: com.monistoWallet.modules.market.TimeDuration,
    private val topNftCollectionsRepository: TopNftCollectionsRepository
) {
    private var topNftsJob: Job? = null

    private val _nftCollectionsItem = MutableStateFlow<Result<List<com.monistoWallet.modules.nft.NftCollectionItem>>?>(null)
    val topNftCollections = _nftCollectionsItem.filterNotNull()

    val sortingFields = listOf(
        com.monistoWallet.modules.market.SortingField.HighestVolume,
        com.monistoWallet.modules.market.SortingField.LowestVolume,
        com.monistoWallet.modules.market.SortingField.TopGainers,
        com.monistoWallet.modules.market.SortingField.TopLosers
    )
    var sortingField: com.monistoWallet.modules.market.SortingField = sortingField
        private set

    val timeDurations = com.monistoWallet.modules.market.TimeDuration.values().toList()
    var timeDuration: com.monistoWallet.modules.market.TimeDuration = timeDuration
        private set

    suspend fun start() {
        update(true)
    }

    suspend fun refresh() {
        update(true)
    }

    private suspend fun update(forceRefresh: Boolean) = withContext(Dispatchers.IO) {
        topNftsJob?.cancel()

        topNftsJob = launch {
            try {
                val topNfts = topNftCollectionsRepository.get(
                    sortingField = sortingField,
                    timeDuration = timeDuration,
                    forceRefresh = forceRefresh,
                    limit = 100
                )
                _nftCollectionsItem.emit(Result.success(topNfts))
            } catch (cancellation: CancellationException) {
                // do nothing
            } catch (error: Exception) {
                _nftCollectionsItem.emit(Result.failure(error))
            }
        }
    }

    suspend fun setSortingField(sortingField: com.monistoWallet.modules.market.SortingField) {
        this.sortingField = sortingField

        update(false)
    }

    suspend fun setTimeDuration(timeDuration: com.monistoWallet.modules.market.TimeDuration) {
        this.timeDuration = timeDuration

        update(false)
    }

}
