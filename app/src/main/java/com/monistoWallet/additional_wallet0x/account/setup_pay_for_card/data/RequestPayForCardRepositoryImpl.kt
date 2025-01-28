package com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.data

import android.util.Log
import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.data.model.RequestPayForCardResponseModel
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.api.RequestPayForCardRepository
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.model.RequestPayApplyResponse
import com.monistoWallet.additional_wallet0x.root.Constants
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class RequestPayForCardRepositoryImpl(val gson: Gson): RequestPayForCardRepository {
    override fun requestPayApply(
        accessToken: String,
        cardLayoutId: String,
        chain: String,
        tokenSymbol: String,
        onResponse: (RequestPayApplyResponse) -> Unit
    ) {

        val body = """{"card_layout_id": "$cardLayoutId", "chain": "$chain", "token_symbol": "$tokenSymbol"}"""

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            body
        )
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/payments/request_apply")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(RequestPayApplyResponse.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                Log.d("Wallet0xTAG", "RequestPayForCardRepositoryImpl.requestPayApply $myResponse")
                if (!response.isSuccessful) {
                    onResponse.invoke(RequestPayApplyResponse.Error("Error ${response.code}"))
                    return
                }

                val model = gson.fromJson(myResponse, RequestPayForCardResponseModel::class.java)
                onResponse.invoke(RequestPayApplyResponse.Success(model))
            }
        })
    }
}