package com.monistoWallet.ui.extensions

import com.monistoWallet.ChartData
import com.monistoWallet.modules.metricchart.MetricsType
import java.math.BigDecimal

data class MetricData(
    val value: String?,
    val diff: BigDecimal?,
    val chartData: ChartData?,
    val type: MetricsType
)
