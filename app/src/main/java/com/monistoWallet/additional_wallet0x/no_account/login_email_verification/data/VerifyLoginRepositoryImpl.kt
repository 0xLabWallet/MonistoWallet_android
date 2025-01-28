package com.monistoWallet.additional_wallet0x.no_account.login_email_verification.data

import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.api.VerifyLoginRepository
import com.monistoWallet.additional_wallet0x.no_account.login_email_verification.domain.model.VerifyLoginAccount
import com.monistoWallet.additional_wallet0x.no_account.register_verification.domain.model.VerifyRegisterAccount
import com.monistoWallet.additional_wallet0x.root.Constants
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

class VerifyLoginRepositoryImpl(val gson: Gson) : VerifyLoginRepository {
    override fun verify(
        email: String,
        code: String,
        onResponse: (VerifyLoginAccount) -> Unit
    ) {
        val url = "${Constants.AUTHENTIFICATION_URL}/auth/verify_login"

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
                onResponse.invoke(VerifyLoginAccount.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onResponse.invoke(VerifyLoginAccount.Error("Error ${response.code}"))
                    return
                }

                val model = gson.fromJson(response.body.string(), VerificationSuccessModel::class.java)
                onResponse.invoke(VerifyLoginAccount.Success(model))
            }
        })
    }
}