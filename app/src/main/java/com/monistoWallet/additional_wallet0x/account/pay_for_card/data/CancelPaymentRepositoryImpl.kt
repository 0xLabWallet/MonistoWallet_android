package com.monistoWallet.additional_wallet0x.account.pay_for_card.data

import android.util.Log
import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.account.pay_for_card.domain.api.CancelPaymentRepository
import com.monistoWallet.additional_wallet0x.account.pay_for_card.domain.model.CancelPaymentResponse
import com.monistoWallet.additional_wallet0x.root.Constants
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class CancelPaymentRepositoryImpl(val gson: Gson) : CancelPaymentRepository {
    override fun cancelPayment(accessToken: String, onResponse: (CancelPaymentResponse) -> Unit) {

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            ""
        )
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/payments/cancel")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(CancelPaymentResponse.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                Log.d("Wallet0xTag", "CancelPaymentRepositoryImpl.cancelPayment: $myResponse")
                if (!response.isSuccessful) {
                    onResponse.invoke(CancelPaymentResponse.Error("Error ${response.code}"))
                    return
                }

                onResponse.invoke(CancelPaymentResponse.Success(myResponse))
            }
        })
    }
}