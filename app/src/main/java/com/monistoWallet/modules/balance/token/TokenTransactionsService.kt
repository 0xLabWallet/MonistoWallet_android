package com.monistoWallet.modules.balance.token

import com.monistoWallet.core.Clearable
import com.monistoWallet.core.managers.SpamManager
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.CurrencyValue
import com.monistoWallet.entities.LastBlockInfo
import com.monistoWallet.entities.Wallet
import com.monistoWallet.entities.nft.NftAssetBriefMetadata
import com.monistoWallet.entities.nft.NftUid
import com.monistoWallet.entities.transactionrecords.TransactionRecord
import com.monistoWallet.entities.transactionrecords.nftUids
import com.monistoWallet.modules.contacts.ContactsRepository
import com.monistoWallet.modules.transactions.FilterTransactionType
import com.monistoWallet.modules.transactions.HistoricalRateKey
import com.monistoWallet.modules.transactions.ITransactionRecordRepository
import com.monistoWallet.modules.transactions.NftMetadataService
import com.monistoWallet.modules.transactions.TransactionItem
import com.monistoWallet.modules.transactions.TransactionSource
import com.monistoWallet.modules.transactions.TransactionSyncStateRepository
import com.monistoWallet.modules.transactions.TransactionWallet
import com.monistoWallet.modules.transactions.TransactionsRateRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

class TokenTransactionsService(
    private val wallet: Wallet,
    private val transactionRecordRepository: ITransactionRecordRepository,
    private val rateRepository: TransactionsRateRepository,
    private val transactionSyncStateRepository: TransactionSyncStateRepository,
    private val contactsRepository: ContactsRepository,
    private val nftMetadataService: NftMetadataService,
    private val spamManager: SpamManager,
) : Clearable {
    private val disposables = CompositeDisposable()
    private val transactionItems = CopyOnWriteArrayList<TransactionItem>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val itemsSubject = BehaviorSubject.create<List<TransactionItem>>()
    val itemsObservable: Observable<List<TransactionItem>> get() = itemsSubject

    fun start() {
        transactionRecordRepository.itemsObservable
            .subscribeIO {
                handleUpdatedRecords(it)
            }
            .let {
                disposables.add(it)
            }

        rateRepository.dataExpiredObservable
            .subscribeIO {
                handleUpdatedHistoricalRates()
            }
            .let {
                disposables.add(it)
            }

        rateRepository.historicalRateObservable
            .subscribeIO {
                handleUpdatedHistoricalRate(it.first, it.second)
            }
            .let {
                disposables.add(it)
            }

        transactionSyncStateRepository.lastBlockInfoObservable
            .subscribeIO { (source, lastBlockInfo) ->
                handleLastBlockInfo(source, lastBlockInfo)
            }
            .let {
                disposables.add(it)
            }

        coroutineScope.launch {
            nftMetadataService.assetsBriefMetadataFlow.collect {
                handle(it)
            }
        }

        coroutineScope.launch {
            contactsRepository.contactsFlow.drop(1).collect {
                handleContactsUpdate()
            }
        }

        val transactionWallet = TransactionWallet(wallet.token, wallet.transactionSource, wallet.badge)

        transactionSyncStateRepository.setTransactionWallets(listOf(transactionWallet))
        transactionRecordRepository.setWallets(
            listOf(transactionWallet),
            transactionWallet,
            FilterTransactionType.All,
            null
        )
    }

    @Synchronized
    private fun handleContactsUpdate() {
        val tmpList = mutableListOf<TransactionItem>()
        transactionItems.forEach {
            tmpList.add(it.copy())
        }

        transactionItems.clear()
        transactionItems.addAll(tmpList)

        itemsSubject.onNext(transactionItems)
    }

    @Synchronized
    private fun handle(assetBriefMetadataMap: Map<NftUid, NftAssetBriefMetadata>) {
        var updated = false
        transactionItems.forEachIndexed { index, item ->
            val tmpMetadata = item.nftMetadata.toMutableMap()
            item.record.nftUids.forEach { nftUid ->
                assetBriefMetadataMap[nftUid]?.let {
                    tmpMetadata[nftUid] = it
                }
            }
            transactionItems[index] = item.copy(nftMetadata = tmpMetadata)
            updated = true
        }

        if (updated) {
            itemsSubject.onNext(transactionItems)
        }
    }

    @Synchronized
    private fun handleLastBlockInfo(source: TransactionSource, lastBlockInfo: LastBlockInfo) {
        var updated = false
        transactionItems.forEachIndexed { index, item ->
            if (item.record.source == source && item.record.changedBy(item.lastBlockInfo, lastBlockInfo)) {
                transactionItems[index] = item.copy(lastBlockInfo = lastBlockInfo)
                updated = true
            }
        }

        if (updated) {
            itemsSubject.onNext(transactionItems)
        }
    }

    @Synchronized
    private fun handleUpdatedHistoricalRate(key: HistoricalRateKey, rate: CurrencyValue) {
        var updated = false
        for (i in 0 until transactionItems.size) {
            val item = transactionItems[i]

            item.record.mainValue?.let { mainValue ->
                mainValue.decimalValue?.let { decimalValue ->
                    if (mainValue.coin?.uid == key.coinUid && item.record.timestamp == key.timestamp) {
                        val currencyValue = CurrencyValue(rate.currency, decimalValue * rate.value)

                        transactionItems[i] = item.copy(currencyValue = currencyValue)
                        updated = true
                    }
                }
            }
        }

        if (updated) {
            itemsSubject.onNext(transactionItems)
        }
    }

    @Synchronized
    private fun handleUpdatedHistoricalRates() {
        for (i in 0 until transactionItems.size) {
            val item = transactionItems[i]
            val currencyValue = getCurrencyValue(item.record)

            transactionItems[i] = item.copy(currencyValue = currencyValue)
        }

        itemsSubject.onNext(transactionItems)
    }

    @Synchronized
    private fun handleUpdatedRecords(transactionRecords: List<TransactionRecord>) {
        val tmpList = mutableListOf<TransactionItem>()

        val nftUids = transactionRecords.nftUids
        val nftMetadata = nftMetadataService.assetsBriefMetadata(nftUids)

        val missingNftUids = nftUids.subtract(nftMetadata.keys)
        if (missingNftUids.isNotEmpty()) {
            coroutineScope.launch {
                nftMetadataService.fetch(missingNftUids)
            }
        }

        val newRecords = mutableListOf<TransactionRecord>()
        transactionRecords.forEach { record ->
            var transactionItem = transactionItems.find { it.record == record }
            if (transactionItem == null) {
                newRecords.add(record)
            }

            if (record.spam && spamManager.hideSuspiciousTx) return@forEach

            transactionItem = if (transactionItem == null) {
                val lastBlockInfo = transactionSyncStateRepository.getLastBlockInfo(record.source)
                val currencyValue = getCurrencyValue(record)

                TransactionItem(record, currencyValue, lastBlockInfo, nftMetadata)
            } else {
                transactionItem.copy(record = record)
            }

            tmpList.add(transactionItem)
        }

        if (newRecords.isNotEmpty() && newRecords.all { it.spam }) {
            loadNext()
        } else {
            transactionItems.clear()
            transactionItems.addAll(tmpList)
            itemsSubject.onNext(transactionItems)
        }
    }

    private fun getCurrencyValue(record: TransactionRecord): CurrencyValue? {
        val decimalValue = record.mainValue?.decimalValue ?: return null
        val coinUid = record.mainValue?.coin?.uid ?: return null

        return rateRepository.getHistoricalRate(HistoricalRateKey(coinUid, record.timestamp))
            ?.let { rate -> CurrencyValue(rate.currency, decimalValue * rate.value) }
    }

    private val executorService = Executors.newCachedThreadPool()

    fun refreshList() {
        itemsSubject.onNext(transactionItems)
    }

    fun loadNext() {
        executorService.submit {
            transactionRecordRepository.loadNext()
        }
    }

    fun fetchRateIfNeeded(recordUid: String) {
        executorService.submit {
            transactionItems.find { it.record.uid == recordUid }?.let { transactionItem ->
                if (transactionItem.currencyValue == null) {
                    transactionItem.record.mainValue?.coin?.uid?.let { coinUid ->
                        rateRepository.fetchHistoricalRate(HistoricalRateKey(coinUid, transactionItem.record.timestamp))
                    }
                }
            }
        }
    }

    fun getTransactionItem(recordUid: String): TransactionItem? {
        return transactionItems.find { it.record.uid == recordUid }
    }


    override fun clear() {
        disposables.clear()
        transactionRecordRepository.clear()
        rateRepository.clear()
        transactionSyncStateRepository.clear()
        coroutineScope.cancel()
        executorService.shutdown()
    }
}
