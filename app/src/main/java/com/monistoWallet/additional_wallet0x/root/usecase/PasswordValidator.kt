package com.monistoWallet.additional_wallet0x.root.usecase

fun isValidPassword(password: String): Boolean {
    // Регулярное выражение для проверки пароля:
    // - Должен содержать хотя бы одну цифру
    // - Должен содержать хотя бы один специальный символ
    // - Должен содержать хотя бы одну строчную букву
    // - Должен содержать хотя бы одну заглавную букву
    // - Минимальная длина 8 символов
    val passwordPattern = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$")
    return passwordPattern.matches(password)
}