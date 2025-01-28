package com.monistoWallet.additional_wallet0x.settings.logout.di

import com.monistoWallet.additional_wallet0x.settings.logout.data.LogoutRepositoryImpl
import com.monistoWallet.additional_wallet0x.settings.logout.domain.api.LogoutInteractor
import com.monistoWallet.additional_wallet0x.settings.logout.domain.api.LogoutRepository
import com.monistoWallet.additional_wallet0x.settings.logout.domain.impl.LogoutInteractorImpl
import com.monistoWallet.additional_wallet0x.settings.logout.ui.view_model.LogoutViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val logoutModule = module {
    viewModel {
        LogoutViewModel(get(), get())
    }

    single<LogoutInteractor> {
        LogoutInteractorImpl(get())
    }
    single<LogoutRepository> {
        LogoutRepositoryImpl(get())
    }
}