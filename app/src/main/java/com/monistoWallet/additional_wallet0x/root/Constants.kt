package com.monistoWallet.additional_wallet0x.root

import android.content.Context
import android.content.Intent
import android.net.Uri

object Constants {
    val MAX_PAYMENT_TO_ORDER_CARD = 5000
    val TERMS_URL = "https://bilderpay.io/termsofuse"

    val AUTHENTIFICATION_URL = "https://api.bilderpay.io"
}

fun openChromeWithUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        // Указание на использование Chrome, если он установлен на устройстве
        setPackage("com.android.chrome")
    }
    context.startActivity(intent)
}