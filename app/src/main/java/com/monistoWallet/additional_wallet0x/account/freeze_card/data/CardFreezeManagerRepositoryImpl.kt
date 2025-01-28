package com.monistoWallet.additional_wallet0x.account.freeze_card.data

import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.account.freeze_card.domain.api.CardFreezeManagerRepository
import com.monistoWallet.additional_wallet0x.account.freeze_card.domain.model.CardFreezeResponse
import com.monistoWallet.additional_wallet0x.root.Constants
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class CardFreezeManagerRepositoryImpl(val gson: Gson) : CardFreezeManagerRepository {
    override fun freezeCard(
        accessToken: String,
        card: Card,
        onResponse: (CardFreezeResponse) -> Unit
    ) {

        val body = """{"card_id": "${card.id}"}"""

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            body
        )
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/cards/freeze")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(CardFreezeResponse.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                if (!response.isSuccessful) {
                    onResponse.invoke(CardFreezeResponse.Error("Error ${response.code}"))
                    return
                }

                val jsonObject = JSONObject(myResponse).get("message").toString()
                onResponse.invoke(CardFreezeResponse.Success(jsonObject))
            }
        })
    }

    override fun unfreezeCard(
        accessToken: String,
        card: Card,
        onResponse: (CardFreezeResponse) -> Unit
    ) {
        val body = """{"card_id": "${card.id}"}"""

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            body
        )
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/cards/unfreeze")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(CardFreezeResponse.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                if (!response.isSuccessful) {
                    onResponse.invoke(CardFreezeResponse.Error("Error ${response.code}"))
                    return
                }

                val jsonObject = JSONObject(myResponse).get("message").toString()
                onResponse.invoke(CardFreezeResponse.Success(jsonObject))
            }
        })
    }
}