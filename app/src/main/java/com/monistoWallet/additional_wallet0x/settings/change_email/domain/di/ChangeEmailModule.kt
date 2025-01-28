package com.monistoWallet.additional_wallet0x.settings.change_email.domain.di

import com.monistoWallet.additional_wallet0x.settings.change_email.data.ChangeEmailRepositoryImpl
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.api.ChangeEmailInteractor
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.api.ChangeEmailRepository
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.impl.ChangeEmailInteractorImpl
import com.monistoWallet.additional_wallet0x.settings.change_email.ui.view_model.ChangeEmailViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val changeEmailModule = module {
    viewModel {
        ChangeEmailViewModel(get(), get())
    }

    single<ChangeEmailInteractor> {
        ChangeEmailInteractorImpl(get())
    }
    single<ChangeEmailRepository> {
        ChangeEmailRepositoryImpl(get())
    }
}