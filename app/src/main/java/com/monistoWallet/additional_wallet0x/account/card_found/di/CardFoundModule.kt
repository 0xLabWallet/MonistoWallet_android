package com.monistoWallet.additional_wallet0x.account.card_found.di

import com.monistoWallet.additional_wallet0x.account.card_found.ui.view_model.CardFoundViewModel
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.impl.CardDataManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val cardFoundModule = module {
    viewModel {
        CardFoundViewModel(get())
    }
}