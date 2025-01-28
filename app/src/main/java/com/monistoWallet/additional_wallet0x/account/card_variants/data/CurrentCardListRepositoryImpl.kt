package com.monistoWallet.additional_wallet0x.account.card_variants.data

import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.account.card_variants.data.model.CardVariantsModel
import com.monistoWallet.additional_wallet0x.account.card_variants.domain.api.CurrentCardListRepository
import com.monistoWallet.additional_wallet0x.account.card_variants.domain.model.CardsListVariantsResponse
import com.monistoWallet.additional_wallet0x.root.Constants
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class CurrentCardListRepositoryImpl(val gson: Gson) : CurrentCardListRepository {
    override fun getAllCards(accessToken: String, onResponse: (CardsListVariantsResponse) -> Unit) {

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            ""
        )
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/payments/current_data")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(CardsListVariantsResponse.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                if (!response.isSuccessful) {
                    onResponse.invoke(CardsListVariantsResponse.Error("Error ${response.code}"))
                    return
                }

                val model = gson.fromJson(myResponse, CardVariantsModel::class.java)
                onResponse.invoke(CardsListVariantsResponse.Success(model))
            }
        })
    }
}