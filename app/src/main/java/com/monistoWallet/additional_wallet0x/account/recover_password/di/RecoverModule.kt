package com.monistoWallet.additional_wallet0x.account.recover_password.di

import com.monistoWallet.additional_wallet0x.account.recover_password.data.RecoverRepositoryImpl
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.api.RecoverInteractor
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.api.RecoverRepository
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.impl.RecoverInteractorImpl
import com.monistoWallet.additional_wallet0x.account.recover_password.ui.view_model.RecoverViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val recoverModule = module {
    viewModel {
        RecoverViewModel(get())
    }

    single<RecoverInteractor> {
        RecoverInteractorImpl(get())
    }
    single<RecoverRepository> {
        RecoverRepositoryImpl(get())
    }
}