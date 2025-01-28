package com.monistoWallet.additional_wallet0x.settings.change_email.data

import android.util.Log
import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.api.ChangeEmailRepository
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.model.ChangeEmailResponse
import com.monistoWallet.additional_wallet0x.settings.change_email.domain.model.VerifyChangeEmailResponse
import com.monistoWallet.additional_wallet0x.root.Constants
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel
import com.monistoWallet.additional_wallet0x.root.model.GetCodeSuccessModel
import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ChangeEmailRepositoryImpl(val gson: Gson) : ChangeEmailRepository {
    override fun changeEmail(
        accessToken: String,
        newEmail: String,
        onResponse: (ChangeEmailResponse) -> Unit
    ) {
        val body = """{
  "email": "$newEmail"
}"""

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            body
        )
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/account/change_email")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(ChangeEmailResponse.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                if (!response.isSuccessful) {
                    onResponse.invoke(ChangeEmailResponse.Error("Error ${response.code}"))
                    return
                }

                val model = gson.fromJson(myResponse, GetCodeSuccessModel::class.java)
                onResponse.invoke(ChangeEmailResponse.Success(model))
            }
        })
    }

    override fun verifyChangeEmail(
        accessToken: String,
        newEmail: String,
        code: String,
        onResponse: (VerifyChangeEmailResponse) -> Unit
    ) {
        val body = """{
  "code": "$code",
  "new_email": "$newEmail"
}"""

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            body
        )
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/account/verify_change_email")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(VerifyChangeEmailResponse.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                Log.d("Wallet0xTag", "ChangeEmailRepositoryImpl.verifyChangeEmail: $myResponse")
                if (!response.isSuccessful) {
                    onResponse.invoke(VerifyChangeEmailResponse.Error("Error ${response.code}"))
                    return
                }
                val result = gson.fromJson(myResponse, VerificationSuccessModel::class.java)
                onResponse.invoke(VerifyChangeEmailResponse.Success(result))
            }
        })
    }
}