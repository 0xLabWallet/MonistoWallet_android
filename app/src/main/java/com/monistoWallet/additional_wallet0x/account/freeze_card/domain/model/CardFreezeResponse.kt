package com.monistoWallet.additional_wallet0x.account.freeze_card.domain.model

interface CardFreezeResponse {
    class Error(val message: String) : CardFreezeResponse
    class Success(val message: String) : CardFreezeResponse
}