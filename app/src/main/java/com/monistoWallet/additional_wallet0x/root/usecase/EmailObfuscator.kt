package com.monistoWallet.additional_wallet0x.root.usecase


fun obfuscateEmail(email: String): String {
    val parts = email.split("@")
    if (parts.size != 2) return email

    val username = parts[0]
    val domain = parts[1]

    if (username.length < 3) return email

    val visiblePart = username.take(2)
    val hiddenPart = "*".repeat(username.length - 2)

    return "$visiblePart$hiddenPart@$domain"
}