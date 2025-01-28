package com.monistoWallet.modules.coin.overview.ui

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.iconPlaceholder
import com.monistoWallet.core.imageUrl
import com.monistoWallet.core.slideFromBottom
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.chart.ChartViewModel
import com.monistoWallet.modules.coin.CoinLink
import com.monistoWallet.modules.coin.overview.CoinOverviewModule
import com.monistoWallet.modules.coin.overview.CoinOverviewViewModel
import com.monistoWallet.modules.coin.overview.HudMessageType
import com.monistoWallet.modules.coin.ui.CoinScreenTitle
import com.monistoWallet.modules.enablecoin.restoresettings.RestoreSettingsViewModel
import com.monistoWallet.modules.enablecoin.restoresettings.ZCashConfig
import com.monistoWallet.modules.managewallets.ManageWalletsModule
import com.monistoWallet.modules.managewallets.ManageWalletsViewModel
import com.monistoWallet.modules.markdown.MarkdownFragment
import com.monistoWallet.modules.zcashconfigure.ZcashConfigure
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.HSSwipeRefresh
import com.monistoWallet.ui.compose.components.ButtonSecondaryCircle
import com.monistoWallet.ui.compose.components.ButtonSecondaryDefault
import com.monistoWallet.ui.compose.components.CellFooter
import com.monistoWallet.ui.compose.components.CellUniversalLawrenceSection
import com.monistoWallet.ui.compose.components.HSpacer
import com.monistoWallet.ui.compose.components.ListErrorView
import com.monistoWallet.ui.compose.components.RowUniversal
import com.monistoWallet.ui.compose.components.subhead2_grey
import com.monistoWallet.ui.helpers.LinkHelper
import com.monistoWallet.ui.helpers.TextHelper
import com.monistoWallet.core.getNavigationResult
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.core.parcelable
import com.wallet0x.marketkit.models.FullCoin
import com.wallet0x.marketkit.models.LinkType

@Composable
fun CoinOverviewScreen(
    apiTag: String,
    fullCoin: FullCoin,
    navController: NavController
) {
    val vmFactory by lazy { CoinOverviewModule.Factory(fullCoin, apiTag) }
    val viewModel = viewModel<CoinOverviewViewModel>(factory = vmFactory)
    val chartViewModel = viewModel<ChartViewModel>(factory = vmFactory)

    val refreshing by viewModel.isRefreshingLiveData.observeAsState(false)
    val overview by viewModel.overviewLiveData.observeAsState()
    val viewState by viewModel.viewStateLiveData.observeAsState()
    val chartIndicatorsState = viewModel.chartIndicatorsState

    val view = LocalView.current
    val context = LocalContext.current

    viewModel.showHudMessage?.let {
        when (it.type) {
            HudMessageType.Error -> HudHelper.showErrorMessage(
                contenView = view,
                resId = it.text,
                icon = it.iconRes,
                iconTint = R.color.white
            )

            HudMessageType.Success -> HudHelper.showSuccessMessage(
                contenView = view,
                resId = it.text,
                icon = it.iconRes,
                iconTint = R.color.white
            )
        }

        viewModel.onHudMessageShown()
    }

    val vmFactory1 = remember { ManageWalletsModule.Factory() }
    val manageWalletsViewModel = viewModel<ManageWalletsViewModel>(factory = vmFactory1)
    val restoreSettingsViewModel = viewModel<RestoreSettingsViewModel>(factory = vmFactory1)

    if (restoreSettingsViewModel.openZcashConfigure != null) {
        restoreSettingsViewModel.zcashConfigureOpened()

        navController.getNavigationResult(ZcashConfigure.resultBundleKey) { bundle ->
            val requestResult = bundle.getInt(ZcashConfigure.requestResultKey)

            if (requestResult == ZcashConfigure.RESULT_OK) {
                val zcashConfig = bundle.parcelable<ZCashConfig>(ZcashConfigure.zcashConfigKey)
                zcashConfig?.let { config ->
                    restoreSettingsViewModel.onEnter(config)
                }
            } else {
                restoreSettingsViewModel.onCancelEnterBirthdayHeight()
            }
        }

        navController.slideFromBottom(R.id.zcashConfigure)
    }


    HSSwipeRefresh(
        refreshing = refreshing,
        onRefresh = {
            viewModel.refresh()
            chartViewModel.refresh()
        },
        content = {
            Crossfade(viewState, label = "") { viewState ->
                when (viewState) {
                    ViewState.Loading -> {
                        Loading()
                    }
                    ViewState.Success -> {
                        overview?.let { overview ->
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                CoinScreenTitle(
                                    fullCoin.coin.name,
                                    overview.marketCapRank,
                                    fullCoin.coin.imageUrl,
                                    fullCoin.iconPlaceholder
                                )

                                Chart(chartViewModel = chartViewModel)

                                Spacer(modifier = Modifier.height(12.dp))

                                CellUniversalLawrenceSection {
                                    RowUniversal(
                                        modifier = Modifier
                                            .height(52.dp)
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                    ) {
                                        subhead2_grey(text = stringResource(R.string.CoinPage_Indicators))
                                        Spacer(modifier = Modifier.weight(1f))

                                        if (chartIndicatorsState.hasActiveSubscription) {
                                            if (chartIndicatorsState.enabled) {
                                                ButtonSecondaryDefault(
                                                    title = stringResource(id = R.string.Button_Hide),
                                                    onClick = {
                                                        viewModel.disableChartIndicators()
                                                    }
                                                )
                                            } else {
                                                ButtonSecondaryDefault(
                                                    title = stringResource(id = R.string.Button_Show),
                                                    onClick = {
                                                        viewModel.enableChartIndicators()
                                                    }
                                                )
                                            }
                                            HSpacer(width = 8.dp)
                                            ButtonSecondaryCircle(
                                                icon = R.drawable.ic_setting_20
                                            ) {
                                                navController.slideFromRight(R.id.indicatorsFragment)
                                            }
                                        }
                                    }
                                }

                                if (overview.marketData.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    MarketData(overview.marketData)
                                }

                                if (overview.roi.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Roi(overview.roi)
                                }

                                viewModel.tokenVariants?.let { tokenVariants ->
                                    Spacer(modifier = Modifier.height(24.dp))
                                    TokenVariants(
                                        tokenVariants = tokenVariants,
                                        onClickAddToWallet = {
                                            manageWalletsViewModel.enable(it)
                                        },
                                        onClickRemoveWallet = {
                                            manageWalletsViewModel.disable(it)
                                        },
                                        onClickCopy = {
                                            TextHelper.copyText(it)
                                            HudHelper.showSuccessMessage(view, R.string.Hud_Text_Copied)
                                        },
                                        onClickExplorer = {
                                            LinkHelper.openLinkInAppBrowser(context, it)
                                        },
                                    )
                                }

                                if (overview.about.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    About(overview.about)
                                }

                                if (overview.links.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Links(overview.links) { onClick(it, context, navController) }
                                }

                                Spacer(modifier = Modifier.height(32.dp))
                                CellFooter(text = stringResource(id = R.string.Market_PoweredByApi))
                            }
                        }

                    }
                    is ViewState.Error -> {
                        ListErrorView(stringResource(id = R.string.BalanceSyncError_Title)) {
                            viewModel.retry()
                            chartViewModel.refresh()
                        }
                    }
                    null -> {}
                }
            }
        },
    )
}

private fun onClick(coinLink: CoinLink, context: Context, navController: NavController) {
    val absoluteUrl = getAbsoluteUrl(coinLink)

    when (coinLink.linkType) {
        LinkType.Guide -> {
            val arguments = bundleOf(
                MarkdownFragment.markdownUrlKey to absoluteUrl,
                MarkdownFragment.handleRelativeUrlKey to true
            )
            navController.slideFromRight(
                R.id.markdownFragment,
                arguments
            )
        }
        else -> LinkHelper.openLinkInAppBrowser(context, absoluteUrl)
    }
}

private fun getAbsoluteUrl(coinLink: CoinLink) = when (coinLink.linkType) {
    LinkType.Twitter -> "https://twitter.com/${coinLink.url}"
    LinkType.Telegram -> "https://t.me/${coinLink.url}"
    else -> coinLink.url
}

@Preview
@Composable
fun LoadingPreview() {
    ComposeAppTheme {
        Loading()
    }
}

@Composable
fun Error(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        subhead2_grey(text = message)
    }
}

@Composable
fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = ComposeAppTheme.colors.grey,
            strokeWidth = 2.dp
        )
    }
}
