package com.monistoWallet.additional_wallet0x.settings.change_password.di

import com.monistoWallet.additional_wallet0x.settings.change_password.data.ChangePasswordRepositoryImpl
import com.monistoWallet.additional_wallet0x.settings.change_password.domain.api.ChangePasswordInteractor
import com.monistoWallet.additional_wallet0x.settings.change_password.domain.api.ChangePasswordRepository
import com.monistoWallet.additional_wallet0x.settings.change_password.domain.impl.ChangePasswordInteractorImpl
import com.monistoWallet.additional_wallet0x.settings.change_password.ui.view_model.ChangePasswordViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val changePasswordModule = module {
    viewModel {
        ChangePasswordViewModel(get(), get())
    }

    single<ChangePasswordInteractor> {
        ChangePasswordInteractorImpl(get())
    }
    single<ChangePasswordRepository> {
        ChangePasswordRepositoryImpl(get())
    }
}