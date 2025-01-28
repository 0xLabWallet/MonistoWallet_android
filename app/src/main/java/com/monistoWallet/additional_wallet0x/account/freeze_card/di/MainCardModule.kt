package com.monistoWallet.additional_wallet0x.account.freeze_card.di

import com.monistoWallet.additional_wallet0x.account.freeze_card.data.CardFreezeManagerRepositoryImpl
import com.monistoWallet.additional_wallet0x.account.freeze_card.domain.api.CardFreezeManagerInteractor
import com.monistoWallet.additional_wallet0x.account.freeze_card.domain.api.CardFreezeManagerRepository
import com.monistoWallet.additional_wallet0x.account.freeze_card.domain.impl.CardFreezeManagerInteractorImpl
import com.monistoWallet.additional_wallet0x.account.freeze_card.ui.view_model.CardMainViewModel
import com.monistoWallet.additional_wallet0x.account.top_up.data.TopUpCardRepositoryImpl
import com.monistoWallet.additional_wallet0x.account.top_up.domain.api.TopUpCardInteractor
import com.monistoWallet.additional_wallet0x.account.top_up.domain.api.TopUpCardRepository
import com.monistoWallet.additional_wallet0x.account.top_up.domain.impl.TopUpCardInteractorImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainCardModule = module {
    viewModel {
        CardMainViewModel(get(), get(), get())
    }
    single<CardFreezeManagerInteractor> {
        CardFreezeManagerInteractorImpl(get())
    }
    single<CardFreezeManagerRepository> {
        CardFreezeManagerRepositoryImpl(get())
    }
    single<TopUpCardInteractor> {
        TopUpCardInteractorImpl(get())
    }
    single<TopUpCardRepository> {
        TopUpCardRepositoryImpl(get())
    }
}