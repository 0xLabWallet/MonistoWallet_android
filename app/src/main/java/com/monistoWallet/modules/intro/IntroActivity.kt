package com.monistoWallet.modules.intro

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monistoWallet.R
import com.monistoWallet.additional_wallet0x.root.ui.GreyRedText
import com.monistoWallet.core.BaseActivity
import com.monistoWallet.modules.main.MainModule
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow


class IntroActivity : BaseActivity() {
    val viewModel by viewModels<IntroViewModel> { IntroModule.Factory() }

    private val nightMode by lazy {
        val uiMode =
            com.monistoWallet.core.App.instance.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        uiMode == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IntroScreen(viewModel, true) { finish() }
        }
        setStatusBarTransparent()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, IntroActivity::class.java)
            context.startActivity(intent)
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun IntroScreen(viewModel: IntroViewModel, nightMode: Boolean, closeActivity: () -> Unit) {
    val context = LocalContext.current
    ComposeAppTheme {
        Box {
            Image(
                painter = painterResource(R.drawable.welcome_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            ButtonPrimaryYellow(
                title = stringResource(R.string.order_a_crypto_card_now),
                onClick = {
                    canShowCardScreen = true
                    viewModel.onStartClicked()
                    MainModule.start(context)
                    closeActivity.invoke()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
            )
            GreyRedText(
                Modifier
                    .padding(bottom = 80.dp)
                    .align(Alignment.BottomCenter)
                    .clickable {
                        viewModel.onStartClicked()
                        MainModule.start(context)
                        closeActivity.invoke()
                    },
                stringResource(R.string.Or_use),
                stringResource(R.string.wallet_without_a_crypto_card),
            )
        }
    }
}
var canShowCardScreen = false
