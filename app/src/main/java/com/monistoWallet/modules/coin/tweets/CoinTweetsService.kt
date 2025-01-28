package com.monistoWallet.modules.coin.tweets

import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.core.subscribeIO
import com.monistoWallet.entities.DataState
import com.wallet0x.marketkit.models.LinkType
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class CoinTweetsService(
    private val coinUid: String,
    private val twitterProvider: TweetsProvider,
    private val marketKit: MarketKitWrapper,
) {
    private val disposables = CompositeDisposable()

    private val stateSubject = BehaviorSubject.create<DataState<List<Tweet>>>()
    val stateObservable: Observable<DataState<List<Tweet>>>
        get() = stateSubject

    val username: String? get() = user?.username
    private var user: TwitterUser? = null

    fun start() {
        fetch()
    }

    fun refresh() {
        fetch()
    }

    fun stop() {
        disposables.clear()
    }

    private fun fetch() {
        val tmpUser = user

        val twitterUserSingle = if (tmpUser != null) {
            Single.just(tmpUser)
        } else {
            marketKit
                .marketInfoOverviewSingle(coinUid, "USD", "en", "market_tweets")
                .flatMap {
                    val username = it.links[LinkType.Twitter]

                    if (username.isNullOrBlank()) {
                        Single.error(TweetsProvider.UserNotFound())
                    } else {
                        twitterProvider.userRequestSingle(username)
                    }
                }
                .doOnSuccess {
                    user = it
                }
        }

        twitterUserSingle
            .flatMap {
                twitterProvider.tweetsSingle(it)
            }
            .subscribeIO(
                {
                    stateSubject.onNext(DataState.Success(it))
                },
                {
                    stateSubject.onNext(DataState.Error(it))
                })
            .let {
                disposables.add(it)
            }
    }
}


