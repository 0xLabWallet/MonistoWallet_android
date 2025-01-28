package com.monistoWallet.modules.settings.guides

import com.monistoWallet.core.managers.ConnectivityManager
import com.monistoWallet.core.managers.GuidesManager
import com.monistoWallet.core.managers.LanguageManager
import com.monistoWallet.entities.DataState
import com.monistoWallet.entities.GuideCategory
import com.monistoWallet.entities.GuideCategoryMultiLang
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class GuidesRepository(
        private val guidesManager: GuidesManager,
        private val connectivityManager: ConnectivityManager,
        private val languageManager: LanguageManager
        ) {

    val guideCategories: Observable<DataState<List<GuideCategory>>>
        get() = guideCategoriesSubject

    private val guideCategoriesSubject = BehaviorSubject.create<DataState<List<GuideCategory>>>()
    private val disposables = CompositeDisposable()
    private val retryLimit = 3

    init {
        fetch()

        connectivityManager.networkAvailabilitySignal
                .subscribeOn(Schedulers.io())
                .subscribe {
                    if (connectivityManager.isConnected && guideCategoriesSubject.value is DataState.Error) {
                        fetch()
                    }
                }
                .let {
                    disposables.add(it)
                }
    }

    fun clear() {
        disposables.clear()
    }

    private fun fetch() {
        guideCategoriesSubject.onNext(DataState.Loading)

        guidesManager.getGuideCategories()
                //retry on error java.lang.AssertionError: No System TLS
                .retryWhen { errors ->
                    errors.zipWith(
                            Flowable.range(1, retryLimit + 1),
                            BiFunction<Throwable, Int, Int> { error: Throwable, retryCount: Int ->
                                if (retryCount < retryLimit && (error is AssertionError)) {
                                    retryCount
                                } else {
                                    throw error
                                }
                            }
                    ).flatMap {
                        Flowable.timer(1, TimeUnit.SECONDS)
                    }
                }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val categories = getCategoriesByLocalLanguage(it, languageManager.currentLocale.language, languageManager.fallbackLocale.language)
                    guideCategoriesSubject.onNext(DataState.Success(categories))
                }, {
                    guideCategoriesSubject.onNext(DataState.Error(it))
                })
                .let {
                    disposables.add(it)
                }
    }

    private fun getCategoriesByLocalLanguage(categoriesMultiLanguage: Array<GuideCategoryMultiLang>, language: String, fallbackLanguage: String) =
        categoriesMultiLanguage.map { categoriesMultiLang ->
            val categoryTitle = categoriesMultiLang.category[language] ?: categoriesMultiLang.category[fallbackLanguage] ?: ""
            val guides = categoriesMultiLang.guides.mapNotNull { it[language] ?: it[fallbackLanguage] }

            GuideCategory(categoriesMultiLang.id, categoryTitle, guides.sortedByDescending { it.updatedAt })
        }
}
