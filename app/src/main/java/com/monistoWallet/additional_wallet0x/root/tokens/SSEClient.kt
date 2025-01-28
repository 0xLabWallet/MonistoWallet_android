package com.monistoWallet.additional_wallet0x.root.tokens

import android.util.Log
import com.monistoWallet.additional_wallet0x.root.Constants
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.concurrent.TimeUnit

class SSEClient {

    interface SSEHandler {
        fun onError401()
        fun onSSEConnectionOpened()
        fun onSSEConnectionClosed()
        fun onSSEEventReceived(event: String, message: String)
        fun onSSEError(t: Throwable)
    }

    private var client = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var call: Call? = null
    private var accessToken = ""

    fun initSse(sseHandler: SSEHandler) {
        Log.d("Wallet0xTag", "SSEClient.initSse 0")
        val request = Request.Builder()
            .url("${Constants.AUTHENTIFICATION_URL}/sse/")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "text/event-stream")
            .build()

        call = client.newCall(request)
        var event = ""
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Wallet0xTag", "SSEClient.initSse $e")
                if (!call.isCanceled()) {
                    sseHandler.onSSEError(e)
                    reconnect(sseHandler)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    if (response.code == 401) {
                        sseHandler.onError401()
                    } else {
                        sseHandler.onSSEError(IOException("Unexpected response: $response"))
                    }
                    response.close()
                    return
                }

                sseHandler.onSSEConnectionOpened()
                val source = response.body.source()

                try {
                    while (!call.isCanceled()) {
                        val line = source.readUtf8Line() ?: break
                        if (line.startsWith("event: ")) {
                            event = line.removePrefix("event: ").trim()
                        }
                        if (line.startsWith("data: ")) {
                            val data = line.removePrefix("data: ").trim()
                            sseHandler.onSSEEventReceived(event, data)
                        }
                    }
                } catch (e: IOException) {
                    if (!call.isCanceled()) {
                        sseHandler.onSSEError(e)
                        reconnect(sseHandler)
                    }
                } finally {
                    response.close()
                    sseHandler.onSSEConnectionClosed()
                }
            }
        })
    }

    fun stopSse() {
        call?.cancel()
    }

    fun updateAccessToken(newAccessToken: String) {
        if (accessToken != newAccessToken) {
            accessToken = newAccessToken
            restartConnection()
        }
    }

    private fun restartConnection() {
        stopSse()
        call = null
    }

    private fun reconnect(sseHandler: SSEHandler) {
        Thread.sleep(2000)
        initSse(sseHandler)
    }
}