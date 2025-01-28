package com.monistoWallet.additional_wallet0x.no_account.register_verification.data

import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.root.model.VerificationSuccessModel
import com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.api.VerifyRegisterRepository
import com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.model.VerifyRegisterAccount
import com.monistoWallet.additional_wallet0x.root.Constants
import com.monistoWallet.additional_wallet0x.root.model.GetCodeErrorModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class VerifyRegisterRepositoryImpl(val gson: Gson) : VerifyRegisterRepository {
    override fun verifyRegister(
        email: String,
        code: String,
        onResponse: (VerifyRegisterAccount) -> Unit
    ) {
        val url = "${Constants.AUTHENTIFICATION_URL}/auth/verify_register"

        val client = OkHttpClient()

        val json = JSONObject()
        json.put("email", email)
        json.put("code", code)

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
                onResponse.invoke(VerifyRegisterAccount.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body.string()
                if (!response.isSuccessful) {
                    val error = gson.fromJson(body, GetCodeErrorModel::class.java)
                    if (response.code == 400) {
                        onResponse.invoke(VerifyRegisterAccount.Error("Email has been registered"))
                    } else {
                        onResponse.invoke(VerifyRegisterAccount.Error("Error ${response.code}\n${error.detail}"))
                    }
                    return
                }

                val model = gson.fromJson(response.body.string(), VerificationSuccessModel::class.java)
                onResponse.invoke(VerifyRegisterAccount.Success(model))
            }
        })
    }
}