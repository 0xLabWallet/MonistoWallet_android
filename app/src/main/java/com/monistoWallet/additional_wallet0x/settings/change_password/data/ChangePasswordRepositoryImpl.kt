package com.monistoWallet.additional_wallet0x.settings.change_password.data

import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.settings.change_password.domain.api.ChangePasswordRepository
import com.monistoWallet.additional_wallet0x.settings.change_password.domain.model.VerifyChangePasswordResponse
import com.monistoWallet.additional_wallet0x.root.Constants
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel
import com.monistoWallet.additional_wallet0x.root.model.GetCodeSuccessModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ChangePasswordRepositoryImpl(val gson: Gson) : ChangePasswordRepository {
    override fun changePassword(
        accessToken: String,
        email: String,
        oldPassword: String,
        onResponse: (GetCodeResponseModel) -> Unit
    ) {
        val body = """{
  "email": "$email",
  "old_password": "$oldPassword"
}"""

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            body
        )
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/account/change_password")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(GetCodeResponseModel.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                if (!response.isSuccessful) {
                    onResponse.invoke(GetCodeResponseModel.Error("Error ${response.code}"))
                    return
                }

                val model = gson.fromJson(myResponse, GetCodeSuccessModel::class.java)
                onResponse.invoke(GetCodeResponseModel.Success(model))
            }
        })
    }

    override fun verifyChangePassword(
        accessToken: String,
        email: String,
        code: String,
        newPassword: String,
        onResponse: (VerifyChangePasswordResponse) -> Unit
    ) {
        val body = """{
  "email": "$email",
  "new_password": "$newPassword",
  "code": "$code"
}"""

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            body
        )
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/account/verify_change_password")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(VerifyChangePasswordResponse.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                if (!response.isSuccessful) {
                    onResponse.invoke(VerifyChangePasswordResponse.Error("Error ${response.code}"))
                    return
                }

                val data = JSONObject(myResponse).getString("message")
                onResponse.invoke(VerifyChangePasswordResponse.Success(data))
            }
        })
    }
}