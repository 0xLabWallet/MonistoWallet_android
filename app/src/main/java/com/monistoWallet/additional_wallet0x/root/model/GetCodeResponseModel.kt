package com.monistoWallet.additional_wallet0x.root.model

interface GetCodeResponseModel {
    class Success(val model: GetCodeSuccessModel) : GetCodeResponseModel
    class Error(val message: String) : GetCodeResponseModel
}