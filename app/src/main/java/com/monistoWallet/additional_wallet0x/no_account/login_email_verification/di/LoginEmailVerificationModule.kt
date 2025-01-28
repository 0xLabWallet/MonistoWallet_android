package com.monistoWallet.additional_wallet0x.no_account.login_email_verification.di

import com.monistoWallet.additional_wallet0x.no_account.login.data.GetCodeToLoginSuccessModelRepositoryImpl
import com.monistoWallet.additional_wallet0x.no_account.login.domain.api.GetCodeToLoginSuccessModeInteractor
import com.monistoWallet.additional_wallet0x.no_account.login.domain.api.GetCodeToLoginSuccessModelRepository
import com.monistoWallet.additional_wallet0x.no_account.login.domain.model.GetCodeToLoginSuccessModeInteractorImpl
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.data.VerifyLoginRepositoryImpl
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.api.VerifyLoginInteractor
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.impl.VerifyLoginInteractorImpl
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.ui.view_model.LoginVerificationViewModel
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.api.VerifyLoginRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val loginEmailVerificationModule = module {
    viewModel {
        LoginVerificationViewModel(get(), get())
    }

    single<GetCodeToLoginSuccessModeInteractor> {
        GetCodeToLoginSuccessModeInteractorImpl(get())
    }
    single<GetCodeToLoginSuccessModelRepository> {
        GetCodeToLoginSuccessModelRepositoryImpl(get())
    }

    single<VerifyLoginRepository> {
        VerifyLoginRepositoryImpl(get())
    }
    single<VerifyLoginInteractor> {
        VerifyLoginInteractorImpl(get())
    }
}