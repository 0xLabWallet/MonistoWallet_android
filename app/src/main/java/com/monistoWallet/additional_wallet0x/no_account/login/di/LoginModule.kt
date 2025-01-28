package com.monistoWallet.additional_wallet0x.no_account.login.di

import com.monistoWallet.additional_wallet0x.no_account.login.data.GetCodeToLoginSuccessModelRepositoryImpl
import com.monistoWallet.additional_wallet0x.no_account.login.domain.api.GetCodeToLoginSuccessModeInteractor
import com.monistoWallet.additional_wallet0x.no_account.login.domain.api.GetCodeToLoginSuccessModelRepository
import com.monistoWallet.additional_wallet0x.no_account.login.domain.model.GetCodeToLoginSuccessModeInteractorImpl
import com.monistoWallet.additional_wallet0x.no_account.login.ui.view_model.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    viewModel {
        LoginViewModel(get())
    }


    single<GetCodeToLoginSuccessModeInteractor> {
        GetCodeToLoginSuccessModeInteractorImpl(get())
    }

    single<GetCodeToLoginSuccessModelRepository> {
        GetCodeToLoginSuccessModelRepositoryImpl(get())
    }
}