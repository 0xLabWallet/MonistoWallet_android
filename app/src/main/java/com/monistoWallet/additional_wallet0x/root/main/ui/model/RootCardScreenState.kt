package com.monistoWallet.additional_wallet0x.root.main.ui.model

import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel

interface RootCardScreenState {
    object NoAccount : RootCardScreenState
    object Loading : RootCardScreenState
    class Error(val message: String) : RootCardScreenState
    object Null : RootCardScreenState

    class Account(val data: VerificationSuccessModel) : RootCardScreenState
}