package com.monistoWallet.additional_wallet0x.no_account.register.data

import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.root.model.GetCodeErrorModel
import com.monistoWallet.additional_wallet0x.root.model.GetCodeSuccessModel
import com.monistoWallet.additional_wallet0x.no_account.register.domain.api.GetCodeToRegisterRepository
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel
import com.monistoWallet.additional_wallet0x.root.Constants
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class GetCodeToRegisterRepositoryImpl(private val gson: Gson) : GetCodeToRegisterRepository {
    override fun getCodeToRegister(
        email: String,
        password: String,
        onResponse: (GetCodeResponseModel) -> Unit
    ) {
        val url = "${Constants.AUTHENTIFICATION_URL}/auth/register"

        val client = OkHttpClient()

        val json = JSONObject()
        json.put("email", email)
        json.put("password", password)

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(GetCodeResponseModel.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    if (response.code == 500 || response.code == 400) {
                        val model = gson.fromJson(response.body.string(), GetCodeErrorModel::class.java)
                        onResponse.invoke(GetCodeResponseModel.Error(model.detail))
                    } else if (response.code == 422) {
                        onResponse.invoke(GetCodeResponseModel.Error("Incorrect email or password"))

                    }
                    return
                }

                val model = gson.fromJson(response.body.string(), GetCodeSuccessModel::class.java)
                onResponse.invoke(GetCodeResponseModel.Success(model))
            }
        })
    }
}