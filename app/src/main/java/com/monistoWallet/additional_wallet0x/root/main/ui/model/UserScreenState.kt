package com.monistoWallet.additional_wallet0x.root.main.ui.model

import com.monistoWallet.additional_wallet0x.root.tokens.model.SseResponseModel

interface UserScreenState {
    object Null : UserScreenState
    class CardsNotFound(val sseModel: SseResponseModel) : UserScreenState
    class CardsFound(val sseModel: SseResponseModel) : UserScreenState
}