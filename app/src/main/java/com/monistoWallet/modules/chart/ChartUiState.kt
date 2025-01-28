package com.monistoWallet.modules.chart

import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.coin.ChartInfoData
import com.monistoWallet.ui.compose.components.TabItem
import com.monistoWallet.ChartViewType
import com.wallet0x.marketkit.models.HsTimePeriod

data class ChartUiState(
    val tabItems: List<TabItem<HsTimePeriod?>>,
    val chartHeaderView: ChartModule.ChartHeaderView?,
    val chartInfoData: ChartInfoData?,
    val loading: Boolean,
    val viewState: ViewState,
    val hasVolumes: Boolean,
    val chartViewType: ChartViewType,
)
