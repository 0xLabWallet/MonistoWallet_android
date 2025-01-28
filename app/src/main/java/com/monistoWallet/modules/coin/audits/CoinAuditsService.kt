package com.monistoWallet.modules.coin.audits

import com.monistoWallet.core.logoUrl
import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.DataState
import com.monistoWallet.modules.coin.audits.CoinAuditsModule.AuditorItem
import com.monistoWallet.modules.market.sortedByDescendingNullLast
import com.wallet0x.marketkit.models.Auditor
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class CoinAuditsService(
    private val addresses: List<String>,
    private val marketKit: MarketKitWrapper
) {
    private var disposable: Disposable? = null

    private val stateSubject = BehaviorSubject.create<DataState<List<AuditorItem>>>()
    val stateObservable: Observable<DataState<List<AuditorItem>>>
        get() = stateSubject

    private fun fetch() {
        disposable?.dispose()

        marketKit.auditReportsSingle(addresses)
            .subscribeIO({ auditors ->
                stateSubject.onNext(DataState.Success(auditorItems(auditors)))
            }, { error ->
                stateSubject.onNext(DataState.Error(error))
            }).let { disposable = it }
    }

    private fun auditorItems(auditors: List<Auditor>): List<AuditorItem> {
        val auditorItems = auditors.map { auditor ->
            val sortedReports = auditor.reports.sortedByDescendingNullLast { it.date }
            AuditorItem(auditor.name, auditor.logoUrl, sortedReports, sortedReports.firstOrNull()?.date)
        }
        return auditorItems.sortedByDescendingNullLast { it.latestDate }
    }

    fun start() {
        fetch()
    }

    fun refresh() {
        fetch()
    }

    fun stop() {
        disposable?.dispose()
    }
}
