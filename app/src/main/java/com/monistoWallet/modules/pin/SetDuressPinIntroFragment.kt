package com.monistoWallet.modules.pin

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.modules.evmfee.ButtonsGroupWithShade
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.HSpacer
import com.monistoWallet.ui.compose.components.HeaderText
import com.monistoWallet.ui.compose.components.HsBackButton
import com.monistoWallet.ui.compose.components.InfoText
import com.monistoWallet.ui.compose.components.RowUniversal
import com.monistoWallet.ui.compose.components.VSpacer
import com.monistoWallet.ui.compose.components.body_leah
import com.monistoWallet.ui.compose.components.subhead2_grey

class SetDuressPinIntroFragment : BaseComposeFragment() {
    @Composable
    override fun GetContent(navController: NavController) {
        SetDuressPinIntroScreen(navController)
    }
}

@Composable
fun SetDuressPinIntroScreen(navController: NavController) {
    val viewModel = viewModel<SetDuressPinIntroViewModel>(factory = SetDuressPinIntroViewModel.Factory())

    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            AppBar(
                title = stringResource(R.string.DuressPin_Title),
                navigationIcon = {
                    HsBackButton(onClick = { navController.popBackStack() })
                },
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            InfoText(
                text = stringResource(R.string.DuressPin_Description),
                paddingBottom = 32.dp
            )
            HeaderText(text = stringResource(R.string.DuressPin_Notes))

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(1.dp, ComposeAppTheme.colors.steel10, RoundedCornerShape(12.dp))
            ) {
                if (viewModel.biometricAuthSupported) {
                    NotesCell(
                        icon = painterResource(id = R.drawable.icon_touch_id_24),
                        title = stringResource(id = R.string.DuressPin_Notes_Biometrics_Title),
                        description = stringResource(id = R.string.DuressPin_Notes_Biometrics_Description)
                    )
                }

                NotesCell(
                    icon = painterResource(id = R.drawable.ic_passcode),
                    title = stringResource(id = R.string.DuressPin_Notes_PasscodeDisabling_Title),
                    description = stringResource(id = R.string.DuressPin_Notes_PasscodeDisabling_Description),
                    borderTop = true
                )
                NotesCell(
                    icon = painterResource(id = R.drawable.ic_edit_24),
                    title = stringResource(id = R.string.DuressPin_Notes_PasscodeChange_Title),
                    description = stringResource(id = R.string.DuressPin_Notes_PasscodeChange_Description),
                    borderTop = true
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            ButtonsGroupWithShade {
                ButtonPrimaryYellow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    title = stringResource(R.string.Button_Continue),
                    onClick = {
                        if (viewModel.shouldShowSelectAccounts) {
                            navController.slideFromRight(R.id.setDuressPinSelectAccounts)
                        } else {
                            navController.slideFromRight(R.id.setDuressPinFragment)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun NotesCell(icon: Painter, title: String, description: String, borderTop: Boolean = false) {
    Box {
        if (borderTop) {
            Divider(
                thickness = 1.dp,
                color = ComposeAppTheme.colors.steel10,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        RowUniversal(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = icon,
                tint = ComposeAppTheme.colors.jacob,
                contentDescription = null,
            )
            HSpacer(width = 16.dp)
            Column {
                body_leah(text = title)
                VSpacer(height = 1.dp)
                subhead2_grey(text = description)
            }
        }
    }
}

