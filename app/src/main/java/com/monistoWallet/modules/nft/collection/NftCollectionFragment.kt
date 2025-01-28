package com.monistoWallet.modules.nft.collection

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.modules.nft.collection.assets.NftCollectionAssetsScreen
import com.monistoWallet.modules.nft.collection.events.NftCollectionEventsScreen
import com.monistoWallet.modules.nft.collection.overview.NftCollectionOverviewScreen
import com.monistoWallet.modules.nft.collection.overview.NftCollectionOverviewViewModel
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.MenuItem
import com.monistoWallet.ui.compose.components.TabItem
import com.monistoWallet.ui.compose.components.Tabs
import com.monistoWallet.ui.helpers.LinkHelper
import com.monistoWallet.ui.helpers.TextHelper
import com.monistoWallet.core.helpers.HudHelper
import com.wallet0x.marketkit.models.BlockchainType
import kotlinx.coroutines.launch

class NftCollectionFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val nftCollectionUid = requireArguments().getString(collectionUidKey, "")
        val blockchainTypeString = requireArguments().getString(blockchainTypeUidKey, "")
        val blockchainType = BlockchainType.fromUid(blockchainTypeString)

        val viewModel by navGraphViewModels<NftCollectionOverviewViewModel>(R.id.nftCollectionFragment) {
            NftCollectionModule.Factory(blockchainType, nftCollectionUid)
        }

        NftCollectionScreen(
            navController,
            viewModel
        )
    }

    companion object {
        private const val collectionUidKey = "collectionUid"
        private const val blockchainTypeUidKey = "blockchainTypeUid"

        fun prepareParams(collectionUid: String, blockchainTypeUid: String) =
            bundleOf(collectionUidKey to collectionUid, blockchainTypeUidKey to blockchainTypeUid)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NftCollectionScreen(navController: NavController, viewModel: NftCollectionOverviewViewModel) {
    val tabs = viewModel.tabs
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current
    val context = LocalContext.current

    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            menuItems = listOf(
                MenuItem(
                    title = TranslatableString.ResString(R.string.Button_Close),
                    icon = R.drawable.ic_close,
                    onClick = {
                        navController.popBackStack()
                    }
                )
            )
        )

        val selectedTab = tabs[pagerState.currentPage]
        val tabItems = tabs.map {
            TabItem(stringResource(id = it.titleResId), it == selectedTab, it)
        }
        Tabs(tabItems, onClick = {
            coroutineScope.launch {
                pagerState.scrollToPage(it.ordinal)
            }
        })

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            when (tabs[page]) {
                NftCollectionModule.Tab.Overview -> {
                    NftCollectionOverviewScreen(
                        viewModel,
                        onCopyText = {
                            TextHelper.copyText(it)
                            HudHelper.showSuccessMessage(view, R.string.Hud_Text_Copied)
                        },
                        onOpenUrl = {
                            LinkHelper.openLinkInAppBrowser(context, it)
                        }
                    )
                }

                NftCollectionModule.Tab.Items -> {
                    NftCollectionAssetsScreen(navController, viewModel.blockchainType, viewModel.collectionUid)
                }

                NftCollectionModule.Tab.Activity -> {
                    NftCollectionEventsScreen(navController, viewModel.blockchainType, viewModel.collectionUid, viewModel.contracts)
                }
            }
        }
    }
}
