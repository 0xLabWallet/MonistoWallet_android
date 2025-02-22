package com.monistoWallet.modules.coin.reports

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.coin.overview.ui.Loading
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.HSSwipeRefresh
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.CellNews
import com.monistoWallet.ui.compose.components.HsBackButton
import com.monistoWallet.ui.compose.components.ListErrorView
import com.monistoWallet.ui.helpers.LinkHelper

class CoinReportsFragment : BaseComposeFragment() {

    private val viewModel by viewModels<CoinReportsViewModel> {
        CoinReportsModule.Factory(requireArguments().getString(COIN_UID_KEY)!!)
    }

    @Composable
    override fun GetContent(navController: NavController) {
        CoinReportsScreen(
            viewModel = viewModel,
            onClickNavigation = {
                navController.popBackStack()
            },
            onClickReportUrl = {
                LinkHelper.openLinkInAppBrowser(requireContext(), it)
            }
        )
    }

    companion object {
        private const val COIN_UID_KEY = "coin_uid_key"

        fun prepareParams(coinUid: String) = bundleOf(COIN_UID_KEY to coinUid)
    }
}

@Composable
private fun CoinReportsScreen(
    viewModel: CoinReportsViewModel,
    onClickNavigation: () -> Unit,
    onClickReportUrl: (url: String) -> Unit
) {
    val viewState by viewModel.viewStateLiveData.observeAsState()
    val isRefreshing by viewModel.isRefreshingLiveData.observeAsState(false)
    val reportViewItems by viewModel.reportViewItemsLiveData.observeAsState()

    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = stringResource(R.string.CoinPage_Reports),
            navigationIcon = {
                HsBackButton(onClick = onClickNavigation)
            }
        )
        HSSwipeRefresh(
            refreshing = isRefreshing,
            onRefresh = viewModel::refresh
        ) {
            Crossfade(viewState) { viewState ->
                when (viewState) {
                    ViewState.Loading -> {
                        Loading()
                    }

                    is ViewState.Error -> {
                        ListErrorView(stringResource(R.string.SyncError), viewModel::onErrorClick)
                    }

                    ViewState.Success -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            reportViewItems?.let {
                                items(it) { report ->
                                    Spacer(modifier = Modifier.height(12.dp))
                                    CellNews(
                                        source = report.author,
                                        title = report.title,
                                        body = report.body,
                                        date = report.date,
                                    ) {
                                        onClickReportUrl(report.url)
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }

                    null -> {}
                }
            }
        }
    }
}
