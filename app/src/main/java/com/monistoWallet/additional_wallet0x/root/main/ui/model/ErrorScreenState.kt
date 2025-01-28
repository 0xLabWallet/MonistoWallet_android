package com.monistoWallet.additional_wallet0x.root.main.ui.model

interface ErrorScreenState {
    object Null : ErrorScreenState
    class Error(val text: String) : ErrorScreenState
}