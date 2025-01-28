package com.monistoWallet.additional_wallet0x.account.card_variants.di

import com.monistoWallet.additional_wallet0x.account.card_variants.data.CurrentCardListRepositoryImpl
import com.monistoWallet.additional_wallet0x.account.card_variants.domain.api.CurrentCardListInteractor
import com.monistoWallet.additional_wallet0x.account.card_variants.domain.api.CurrentCardListRepository
import com.monistoWallet.additional_wallet0x.account.card_variants.domain.impl.CurrentCardListInteractorImpl
import com.monistoWallet.additional_wallet0x.account.card_variants.ui.view_model.BuyCardViewModel
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.data.RequestPayForCardRepositoryImpl
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.api.RequestPayForCardInteractor
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.api.RequestPayForCardRepository
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.impl.RequestPayForCardInteractorImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val allCardsToOrderModule = module {
    viewModel {
        BuyCardViewModel(get(), get())
    }

    single<CurrentCardListInteractor> {
        CurrentCardListInteractorImpl(get())
    }
    single<CurrentCardListRepository> {
        CurrentCardListRepositoryImpl(get())
    }
    single<RequestPayForCardInteractor> {
        RequestPayForCardInteractorImpl(get())
    }
    single<RequestPayForCardRepository> {
        RequestPayForCardRepositoryImpl(get())
    }
}