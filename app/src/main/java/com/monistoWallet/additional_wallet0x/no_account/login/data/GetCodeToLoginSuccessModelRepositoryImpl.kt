package com.monistoWallet.additional_wallet0x.no_account.login.data

import android.util.Log
import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.no_account.login.domain.api.GetCodeToLoginSuccessModelRepository
import com.monistoWallet.additional_wallet0x.root.Constants
import com.monistoWallet.additional_wallet0x.root.model.GetCodeErrorModel
import com.monistoWallet.additional_wallet0x.root.model.GetCodeResponseModel
import com.monistoWallet.additional_wallet0x.root.model.GetCodeSuccessModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class GetCodeToLoginSuccessModelRepositoryImpl(val gson: Gson) :
    GetCodeToLoginSuccessModelRepository {
    override fun getLoginCode(
        email: String,
        password: String,
        onResponse: (GetCodeResponseModel) -> Unit
    ) {
        val url = "${Constants.AUTHENTIFICATION_URL}/auth/login"

        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("username", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(GetCodeResponseModel.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    if (response.code == 400) {
                        val data = gson.fromJson(response.body.string(), GetCodeErrorModel::class.java)
                        onResponse.invoke(GetCodeResponseModel.Error("Email or password is incorrect"))
                    } else {
                        onResponse.invoke(GetCodeResponseModel.Error("Error ${response.code}"))
                    }
                    return
                }

                val model = gson.fromJson(response.body.string(), GetCodeSuccessModel::class.java)
                onResponse.invoke(GetCodeResponseModel.Success(model))
            }
        })
    }
}