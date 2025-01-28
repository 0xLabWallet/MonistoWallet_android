package com.monistoWallet.additional_wallet0x.no_account.register.di

import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.no_account.register.data.GetCodeToRegisterRepositoryImpl
import com.monistoWallet.additional_wallet0x.no_account.register.domain.api.GetCodeToRegisterInteractor
import com.monistoWallet.additional_wallet0x.no_account.register.domain.api.GetCodeToRegisterRepository
import com.monistoWallet.additional_wallet0x.no_account.register.domain.impl.GetCodeToRegisterInteractorImpl
import com.monistoWallet.additional_wallet0x.no_account.register.ui.view_model.RegistrationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val registerModule = module {
    viewModel<RegistrationViewModel> {
        RegistrationViewModel(get())
    }

    single<GetCodeToRegisterInteractor> {
        GetCodeToRegisterInteractorImpl(get())
    }
    single<GetCodeToRegisterRepository> {
        GetCodeToRegisterRepositoryImpl(get())
    }
    single<Gson> {
        Gson()
    }
}