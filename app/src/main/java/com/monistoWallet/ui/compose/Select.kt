package com.monistoWallet.ui.compose

data class Select<T>(
    val selected: T,
    val options: List<T>
)

data class SelectOptional<T>(
    val selected: T?,
    val options: List<T>
)