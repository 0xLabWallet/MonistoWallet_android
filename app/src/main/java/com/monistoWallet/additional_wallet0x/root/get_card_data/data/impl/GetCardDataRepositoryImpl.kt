package com.monistoWallet.additional_wallet0x.root.get_card_data.data.impl

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.root.Constants.AUTHENTIFICATION_URL
import com.monistoWallet.additional_wallet0x.root.get_card_data.data.model.CardSecretData
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.api.GetCardDataRepository
import com.monistoWallet.additional_wallet0x.root.get_card_data.domain.model.CardSecretDataResponse
import com.monistoWallet.additional_wallet0x.root.model.GetCodeErrorModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class GetCardDataRepositoryImpl : GetCardDataRepository {
    private val gson: Gson = Gson()
    private val client = OkHttpClient()
    override fun loadCardData(
        accessToken: String,
        cardId: String,
        onResponse: (CardSecretDataResponse) -> Unit
    ) {
        val body = """{"card_id": "$cardId"}"""

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            body
        )

        val request = Request.Builder()
            .url("$AUTHENTIFICATION_URL/cards/get_info")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResponse.invoke(CardSecretDataResponse.Error(e.toString()))
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val str = response.body.string()
                if (response.isSuccessful) {
                    val data = gson.fromJson(str, CardSecretData::class.java)
                    onResponse.invoke(CardSecretDataResponse.Success(data.data))
                } else {
                    val message = gson.fromJson(str, GetCodeErrorModel::class.java)
                    onResponse.invoke(CardSecretDataResponse.Error(message.detail))
                }
            }
        })
    }
}