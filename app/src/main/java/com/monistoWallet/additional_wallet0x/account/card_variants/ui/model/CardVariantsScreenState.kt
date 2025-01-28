package com.monistoWallet.additional_wallet0x.account.card_variants.ui.model

import com.monistoWallet.additional_wallet0x.account.card_variants.data.model.CardVariantsModel

interface CardVariantsScreenState {
    object Null : CardVariantsScreenState
    object Loading : CardVariantsScreenState
    class Error(val message: String) : CardVariantsScreenState
    class Result(val model: CardVariantsModel) : CardVariantsScreenState
}