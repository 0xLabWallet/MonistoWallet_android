package com.monistoWallet.additional_wallet0x.account.transactions.di

import com.monistoWallet.additional_wallet0x.account.transactions.data.CardTransactionsRepositoryImpl
import com.monistoWallet.additional_wallet0x.account.transactions.domain.api.CardTransactionsInteractor
import com.monistoWallet.additional_wallet0x.account.transactions.domain.api.CardTransactionsRepository
import com.monistoWallet.additional_wallet0x.account.transactions.domain.impl.CardTransactionsInteractorImpl
import com.monistoWallet.additional_wallet0x.account.transactions.ui.view_model.CardTransactionsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val cardTransactionsModule = module {
    viewModel {
        CardTransactionsViewModel(get(), get())
    }

    single<CardTransactionsInteractor> {
        CardTransactionsInteractorImpl(get())
    }
    single<CardTransactionsRepository> {
        CardTransactionsRepositoryImpl(get())
    }
}