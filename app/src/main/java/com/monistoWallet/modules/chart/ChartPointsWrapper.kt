package com.monistoWallet.modules.chart

import com.monistoWallet.models.ChartIndicator
import com.monistoWallet.models.ChartPoint


data class ChartPointsWrapper(
    val items: List<ChartPoint>,
    val isMovementChart: Boolean = true,
    val indicators: Map<String, ChartIndicator> = mapOf(),
)
