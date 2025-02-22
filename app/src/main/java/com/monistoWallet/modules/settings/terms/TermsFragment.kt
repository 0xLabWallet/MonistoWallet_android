package com.monistoWallet.modules.settings.terms

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.modules.evmfee.ButtonsGroupWithShade
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.CellUniversalLawrenceSection
import com.monistoWallet.ui.compose.components.HsCheckbox
import com.monistoWallet.ui.compose.components.MenuItem
import com.monistoWallet.ui.compose.components.RowUniversal
import com.monistoWallet.ui.compose.components.subhead2_leah
import com.monistoWallet.core.findNavController
import com.monistoWallet.core.setNavigationResult

class TermsFragment : BaseComposeFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.onBackPressedDispatcher?.addCallback(this) {
            findNavController().setNavigationResult(
                resultBundleKey,
                bundleOf(requestResultKey to RESULT_CANCELLED)
            )
            findNavController().popBackStack()
        }
    }

    @Composable
    override fun GetContent(navController: NavController) {
        TermsScreen(navController = navController)
    }

    companion object {
        const val RESULT_OK = 1
        const val RESULT_CANCELLED = 2
        const val resultBundleKey = "resultBundleKey"
        const val requestResultKey = "requestResultKey"
    }
}

@Composable
fun TermsScreen(
    navController: NavController,
    viewModel: TermsViewModel = viewModel(factory = TermsModule.Factory())
) {

    if (viewModel.closeWithTermsAgreed) {
        viewModel.closedWithTermsAgreed()

        navController.setNavigationResult(
            TermsFragment.resultBundleKey,
            bundleOf(TermsFragment.requestResultKey to TermsFragment.RESULT_OK)
        )
        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeAppTheme.colors.tyler)
    ) {
        AppBar(
            title = stringResource(R.string.Settings_Terms),
            menuItems = listOf(
                MenuItem(
                    title = TranslatableString.ResString(R.string.Button_Close),
                    icon = R.drawable.ic_close,
                    onClick = {
                        navController.setNavigationResult(
                            TermsFragment.resultBundleKey,
                            bundleOf(TermsFragment.requestResultKey to TermsFragment.RESULT_CANCELLED)
                        )
                        navController.popBackStack()
                    }
                )
            )
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            CellUniversalLawrenceSection(viewModel.termsViewItems) { item ->
                val onClick = if (!viewModel.readOnlyState) {
                    { viewModel.onTapTerm(item.termType, !item.checked) }
                } else {
                    null
                }

                RowUniversal(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = onClick
                ) {
                    HsCheckbox(
                        checked = item.checked,
                        enabled = !viewModel.readOnlyState,
                        onCheckedChange = { checked ->
                            viewModel.onTapTerm(item.termType, checked)
                        },
                    )
                    Spacer(Modifier.width(16.dp))
                    subhead2_leah(
                        text = stringResource(item.termType.description)
                    )
                }
            }

            Spacer(Modifier.height(60.dp))
        }

        if (viewModel.buttonVisible) {
            ButtonsGroupWithShade {
                ButtonPrimaryYellow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    title = stringResource(R.string.Button_IAgree),
                    onClick = { viewModel.onAgreeClick() },
                    enabled = viewModel.buttonEnabled
                )
            }
        }
    }

}

