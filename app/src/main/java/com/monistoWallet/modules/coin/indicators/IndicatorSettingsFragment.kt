package com.monistoWallet.modules.coin.indicators

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.modules.chart.ChartIndicatorSetting
import com.monistoWallet.core.helpers.HudHelper

class IndicatorSettingsFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val indicatorSetting = arguments?.getString("indicatorId")?.let {
            com.monistoWallet.core.App.chartIndicatorManager.getChartIndicatorSetting(it)
        }

        if (indicatorSetting == null) {
            HudHelper.showErrorMessage(LocalView.current, R.string.Error_ParameterNotSet)
            navController.popBackStack()
        } else {
            when (indicatorSetting.type) {
                ChartIndicatorSetting.IndicatorType.MA -> {
                    EmaSettingsScreen(
                        navController = navController,
                        indicatorSetting = indicatorSetting
                    )
                }

                ChartIndicatorSetting.IndicatorType.RSI -> {
                    RsiSettingsScreen(
                        navController = navController,
                        indicatorSetting = indicatorSetting
                    )
                }

                ChartIndicatorSetting.IndicatorType.MACD -> {
                    MacdSettingsScreen(
                        navController = navController,
                        indicatorSetting = indicatorSetting
                    )
                }
            }
        }
    }

    companion object {
        fun params(indicatorId: String) = bundleOf("indicatorId" to indicatorId)
    }
}
