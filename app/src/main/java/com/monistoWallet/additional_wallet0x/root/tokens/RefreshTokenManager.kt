package com.monistoWallet.additional_wallet0x.root.tokens

import android.util.Log
import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.root.Constants
import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class RefreshTokenManager(val gson: Gson) {
    fun updateAccessToken(
        refreshToken: String,
        onResponse: (RefreshTokenResponse) -> Unit
    ) {
        val url = "${Constants.AUTHENTIFICATION_URL}/auth/refresh_token"

        val client = OkHttpClient()

        val json = """{"refresh_token": "$refreshToken"}"""
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(RefreshTokenResponse.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onResponse.invoke(RefreshTokenResponse.Error("Error ${response.code}"))
                    return
                }

                val model = gson.fromJson(response.body.string(), VerificationSuccessModel::class.java)
                onResponse.invoke(RefreshTokenResponse.Success(model))
            }
        })
    }
    companion object {
        val REFRESH_TOKEN_ERROR_CODE = "401"
        private val gson = Gson()
        fun updateAccessToken(
            refreshToken: String,
            onResponse: (RefreshTokenResponse) -> Unit
        ) {
            val url = "${Constants.AUTHENTIFICATION_URL}/auth/refresh_token"

            val client = OkHttpClient()

            val json = """{"refresh_token": "$refreshToken"}"""
            val requestBody = json.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    onResponse.invoke(RefreshTokenResponse.Error(e.message.toString()))
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        onResponse.invoke(RefreshTokenResponse.Error("Error ${response.code}"))
                        return
                    }

                    val model = gson.fromJson(response.body.string(), VerificationSuccessModel::class.java)
                    onResponse.invoke(RefreshTokenResponse.Success(model))
                }
            })
        }
    }

}