package com.monistoWallet.modules.pin

import android.os.Bundle
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.modules.pin.ui.PinSet
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.core.parcelable
import kotlinx.parcelize.Parcelize

class SetDuressPinFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val viewModel = viewModel<SetDuressPinViewModel>(factory = SetDuressPinViewModel.Factory(arguments?.parcelable("input")))
        val view = LocalView.current
        PinSet(
            title = stringResource(id = R.string.SetDuressPin_Title),
            description = stringResource(id = R.string.SetDuressPin_Description),
            dismissWithSuccess = {
                viewModel.onDuressPinSet()
                HudHelper.showSuccessMessage(view, R.string.Hud_Text_Created)
                navController.popBackStack(R.id.setDuressPinIntroFragment, true)
            },
            onBackPress = { navController.popBackStack() },
            forDuress = true
        )
    }

    companion object {
        fun params(accountIds: List<String>): Bundle {
            return bundleOf("input" to Input(accountIds))
        }
    }

    @Parcelize
    data class Input(val accountIds: List<String>) : Parcelable
}
