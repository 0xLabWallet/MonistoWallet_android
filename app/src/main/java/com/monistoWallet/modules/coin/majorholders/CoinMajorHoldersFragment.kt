package com.monistoWallet.modules.coin.majorholders

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.core.shorten
import com.monistoWallet.entities.ViewState
import com.monistoWallet.modules.coin.MajorHolderItem
import com.monistoWallet.modules.coin.overview.ui.Loading
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.ButtonSecondaryDefault
import com.monistoWallet.ui.compose.components.CellUniversalLawrenceSection
import com.monistoWallet.ui.compose.components.HSpacer
import com.monistoWallet.ui.compose.components.ListErrorView
import com.monistoWallet.ui.compose.components.MenuItem
import com.monistoWallet.ui.compose.components.RowUniversal
import com.monistoWallet.ui.compose.components.SectionItemBorderedRowUniversalClear
import com.monistoWallet.ui.compose.components.SnackbarError
import com.monistoWallet.ui.compose.components.StackedBarChart
import com.monistoWallet.ui.compose.components.VSpacer
import com.monistoWallet.ui.compose.components.body_leah
import com.monistoWallet.ui.compose.components.captionSB_grey
import com.monistoWallet.ui.compose.components.headline1_bran
import com.monistoWallet.ui.compose.components.subhead1_grey
import com.monistoWallet.ui.compose.components.subhead2_grey
import com.monistoWallet.ui.helpers.LinkHelper
import com.monistoWallet.ui.helpers.TextHelper
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.core.parcelable
import com.wallet0x.marketkit.models.Blockchain

class CoinMajorHoldersFragment : BaseComposeFragment() {

    private val coinUid by lazy {
        requireArguments().getString(COIN_UID_KEY)!!
    }

    private val blockchain by lazy {
        requireArguments().parcelable<Blockchain>(BLOCKCHAIN_KEY)!!
    }

    @Composable
    override fun GetContent(navController: NavController) {
        CoinMajorHoldersScreen(
            coinUid,
            blockchain,
            navController,
        )
    }

    companion object {
        private const val COIN_UID_KEY = "coin_uid_key"
        private const val BLOCKCHAIN_KEY = "blockchain_key"

        fun prepareParams(coinUid: String, blockchain: Blockchain) =
            bundleOf(
                COIN_UID_KEY to coinUid,
                BLOCKCHAIN_KEY to blockchain,
            )
    }
}

@Composable
private fun CoinMajorHoldersScreen(
    coinUid: String,
    blockchain: Blockchain,
    navController: NavController,
    viewModel: CoinMajorHoldersViewModel = viewModel(
        factory = CoinMajorHoldersModule.Factory(coinUid, blockchain)
    )
) {

    Surface(color = ComposeAppTheme.colors.tyler) {
        Column {
            AppBar(
                title = blockchain.name,
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = { navController.popBackStack() }
                    )
                )
            )

            Crossfade(viewModel.uiState.viewState) { viewState ->
                when (viewState) {
                    ViewState.Loading -> {
                        Loading()
                    }

                    is ViewState.Error -> {
                        ListErrorView(stringResource(R.string.SyncError), viewModel::onErrorClick)
                        viewModel.uiState.error?.let {
                            SnackbarError(it.getString())
                            viewModel.errorShown()
                        }
                    }

                    ViewState.Success -> {
                        CoinMajorHoldersContent(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun CoinMajorHoldersContent(
    viewModel: CoinMajorHoldersViewModel,
) {
    val uiState = viewModel.uiState

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {

        item {
            HoldersGeneralInfo(uiState.top10Share, uiState.totalHoldersCount)
        }

        item {
            StackedBarChart(
                slices = uiState.chartData,
                modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
            )
        }

        items(uiState.topHolders) {
            TopWalletCell(it)
        }

        item {
            uiState.seeAllUrl?.let {
                val context = LocalContext.current
                SeeAllButton { LinkHelper.openLinkInAppBrowser(context, it) }
            }
        }
    }
}

@Composable
private fun HoldersGeneralInfo(top10Share: String, totalHoldersCount: String) {
    VSpacer(12.dp)
    subhead2_grey(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(R.string.CoinPage_MajorHolders_HoldersNumber, totalHoldersCount)
    )
    VSpacer(12.dp)
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        headline1_bran(
            text = top10Share,
            modifier = Modifier.alignByBaseline()
        )
        HSpacer(8.dp)
        subhead1_grey(
            text = stringResource(R.string.CoinPage_MajorHolders_InTopWallets),
            modifier = Modifier.alignByBaseline()
        )
    }
}

@Composable
private fun SeeAllButton(onClick: () -> Unit) {
    VSpacer(32.dp)
    CellUniversalLawrenceSection(
        listOf {
            RowUniversal(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = onClick
            ) {
                body_leah(
                    text = stringResource(R.string.Market_SeeAll),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = ComposeAppTheme.colors.grey
                )
            }
        }
    )
    VSpacer(32.dp)
}

@Composable
private fun TopWalletCell(item: MajorHolderItem) {
    val localView = LocalView.current

    SectionItemBorderedRowUniversalClear(borderTop = true) {
        captionSB_grey(
            text = item.index.toString(),
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            body_leah(text = item.sharePercent)
            VSpacer(1.dp)
            subhead2_grey(text = item.balance)
        }

        ButtonSecondaryDefault(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .height(28.dp),
            title = item.address.shorten(),
            onClick = {
                TextHelper.copyText(item.address)
                HudHelper.showSuccessMessage(localView, R.string.Hud_Text_Copied)
            }
        )
    }
}
