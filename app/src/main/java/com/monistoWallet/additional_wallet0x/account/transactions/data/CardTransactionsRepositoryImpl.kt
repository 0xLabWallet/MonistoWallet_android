package com.monistoWallet.additional_wallet0x.account.transactions.data

import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.account.transactions.data.model.TransactionsDataModel
import com.monistoWallet.additional_wallet0x.account.transactions.domain.api.CardTransactionsRepository
import com.monistoWallet.additional_wallet0x.account.transactions.domain.model.CardListTransactionsResponse
import com.monistoWallet.additional_wallet0x.root.Constants
import com.monistoWallet.additional_wallet0x.root.tokens.model.Card
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class CardTransactionsRepositoryImpl(val gson: Gson) : CardTransactionsRepository {
    override fun getTransactionsList(
        accessToken: String,
        card: Card,
        onResponse: (CardListTransactionsResponse) -> Unit
    ) {
        val body = """{"card_id": "${card.id}", "page": 1, "limit": 50}"""

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            body
        )
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/cards/transactions")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(CardListTransactionsResponse.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                if (!response.isSuccessful) {
                    onResponse.invoke(CardListTransactionsResponse.Error("Error ${response.code}"))
                    return
                }

                val model = gson.fromJson(myResponse, TransactionsDataModel::class.java)
                onResponse.invoke(CardListTransactionsResponse.Success(model))
            }
        })
    }
}