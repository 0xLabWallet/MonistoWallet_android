package com.monistoWallet.additional_wallet0x.account.recover_password.data

import android.util.Log
import com.google.gson.Gson
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.api.RecoverRepository
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.model.RecoverPassword
import com.monistoWallet.additional_wallet0x.account.recover_password.domain.model.VerifyRecoverPassword
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

class RecoverRepositoryImpl(val gson: Gson) : RecoverRepository{
    override fun forgotPassword(email: String, onResponse: (RecoverPassword) -> Unit) {
        val body = """{
  "email": "$email"
}"""

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            body
        )
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/account/forgot_password")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(RecoverPassword.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                Log.d("Wallet0xTAG", "RecoverRepositoryImpl.forgotPassword $myResponse")
                if (!response.isSuccessful) {
                    if (response.code == 400) {
                        onResponse.invoke(RecoverPassword.Error("User not found"))
                    } else {
                        val jsonObject = JSONObject(myResponse).get("detail").toString()
                        onResponse.invoke(RecoverPassword.Error(jsonObject))
                    }
                    return
                }

                val jsonObject = JSONObject(myResponse).get("message").toString()
                onResponse.invoke(RecoverPassword.Success(jsonObject))
            }
        })
    }

    override fun verifyRecoverPassword(
        email: String,
        newPassword: String,
        code: String,
        onResponse: (VerifyRecoverPassword) -> Unit
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
            .url("${Constants.AUTHENTIFICATION_URL}/account/verify_forgot_password")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResponse.invoke(VerifyRecoverPassword.Error(e.message.toString()))
            }

            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body.string()
                Log.d("Wallet0xTAG", "RecoverRepositoryImpl.verifyRecoverPassword $myResponse")
                if (!response.isSuccessful) {
                    if (response.code == 400) {
                        onResponse.invoke(VerifyRecoverPassword.Error("User not found"))
                    } else {
                        onResponse.invoke(VerifyRecoverPassword.Error("Error ${response.code}"))
                    }
                    return
                }

                val data = gson.fromJson(myResponse, VerificationSuccessModel::class.java)
                onResponse.invoke(VerifyRecoverPassword.Success(data))
            }
        })
    }
}