package com.monistoWallet.additional_wallet0x.no_account.register_verification.di

import com.monistoWallet.additional_wallet0x.no_account.register_verification.data.VerifyRegisterRepositoryImpl
import com.monistoWallet.additional_wallet0x.no_account.register_verification.ui.view_model.VerificationRegisterViewModel
import com.monistoWallet.additional_wallet0x.no_account.register.data.GetCodeToRegisterRepositoryImpl
import com.monistoWallet.additional_wallet0x.no_account.register.domain.api.GetCodeToRegisterInteractor
import com.monistoWallet.additional_wallet0x.no_account.register.domain.api.GetCodeToRegisterRepository
import com.monistoWallet.additional_wallet0x.no_account.register.domain.impl.GetCodeToRegisterInteractorImpl
import com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.api.VerifyRegisterInteractor
import com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.api.VerifyRegisterRepository
import com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.impl.VerifyRegisterInteractorImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val registerEmailVerificationModule = module {
    viewModel {
        VerificationRegisterViewModel(get(), get())
    }

    single<GetCodeToRegisterInteractor> {
        GetCodeToRegisterInteractorImpl(get())
    }
    single<GetCodeToRegisterRepository> {
        GetCodeToRegisterRepositoryImpl(get())
    }
    single<VerifyRegisterInteractor> {
        VerifyRegisterInteractorImpl(get())
    }
    single<VerifyRegisterRepository> {
        VerifyRegisterRepositoryImpl(get())
    }
}