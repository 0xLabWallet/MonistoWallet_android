package com.monistoWallet.additional_wallet0x.account.pay_for_card.di

import com.monistoWallet.additional_wallet0x.account.pay_for_card.data.CancelPaymentRepositoryImpl
import com.monistoWallet.additional_wallet0x.account.pay_for_card.domain.api.CancelPaymentInteractor
import com.monistoWallet.additional_wallet0x.account.pay_for_card.domain.api.CancelPaymentRepository
import com.monistoWallet.additional_wallet0x.account.pay_for_card.domain.impl.CancelPaymentInteractorImpl
import com.monistoWallet.additional_wallet0x.account.pay_for_card.ui.view_model.PayViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val payModule = module {
    viewModel {
        PayViewModel()
    }

    single<CancelPaymentRepository> {
        CancelPaymentRepositoryImpl(get())
    }
    single<CancelPaymentInteractor> {
        CancelPaymentInteractorImpl(get())
    }
}