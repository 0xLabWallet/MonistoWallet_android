package com.monistoWallet.additional_wallet0x.account.top_up.data

import android.util.Log
import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.data.model.RequestPayForCardResponseModel
import com.monistoWallet.additional_wallet0x.account.setup_pay_for_card.domain.model.RequestPayApplyResponse
import com.monistoWallet.additional_wallet0x.account.top_up.domain.api.TopUpCardRepository
import com.monistoWallet.additional_wallet0x.root.Constants
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class TopUpCardRepositoryImpl(val gson: Gson) : TopUpCardRepository {
    override fun topUpCard(
        accessToken: String,
        cardProviderId: String,
        network: String,
        token: String,
        onResult: (RequestPayApplyResponse) -> Unit
    ) {
        val body = """{"card_provider_id": "$cardProviderId", "chain": "$network", "token_symbol": "$token"}"""

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            body
        )
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/payments/request_recharge")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResult.invoke(RequestPayApplyResponse.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                Log.d("Wallet0xTag", "TopUpCardRepositoryImpl.topUpCard: $myResponse")
                if (!response.isSuccessful) {
                    onResult.invoke(RequestPayApplyResponse.Error("Error ${response.code}"))
                    return
                }

                val model = gson.fromJson(myResponse, RequestPayForCardResponseModel::class.java)
                onResult.invoke(RequestPayApplyResponse.Success(model))
            }
        })
    }
}