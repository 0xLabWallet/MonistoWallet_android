package com.monistoWallet.additional_wallet0x.settings.logout.data

import android.util.Log
import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.root.Constants
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel
import com.monistoWallet.additional_wallet0x.root.model.GetCodeSuccessModel
import com.monistoWallet.additional_wallet0x.settings.logout.domain.api.LogoutRepository
import com.monistoWallet.additional_wallet0x.settings.logout.domain.model.LogoutResponse
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class LogoutRepositoryImpl(val gson: Gson) : LogoutRepository {
    override fun logout(accessToken: String, onResponse: (LogoutResponse) -> Unit) {
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            ""
        )
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/account/logout")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(LogoutResponse.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                Log.d("Wallet0xTag", "LogoutRepositoryImpl.logout: $myResponse")
                if (!response.isSuccessful) {
                    onResponse.invoke(LogoutResponse.Error("Error ${response.code}"))
                    return
                }

                val model = JSONObject(myResponse).getString("message")
                onResponse.invoke(LogoutResponse.Success(model))
            }
        })
    }
}