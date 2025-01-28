package com.monistoWallet.additional_wallet0x.root.main.ui.model

import com.monistoWallet.additional_wallet0x.root.sse_top_up_received.data.model.SSETopUpReceivedModel

interface TopUpScreenState {
    object Null : TopUpScreenState
    class Result(val response: SSETopUpReceivedModel) : TopUpScreenState
}