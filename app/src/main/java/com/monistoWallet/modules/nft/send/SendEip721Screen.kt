package com.monistoWallet.modules.nft.send

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.monistoWallet.R
import com.monistoWallet.core.slideFromRight
import com.monistoWallet.modules.address.AddressParserViewModel
import com.monistoWallet.modules.address.AddressViewModel
import com.monistoWallet.modules.address.HSAddressInput
import com.monistoWallet.modules.send.evm.confirmation.SendEvmConfirmationModule
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.MenuItem
import com.monistoWallet.ui.compose.components.headline1_leah

@Composable
fun SendEip721Screen(
    navController: NavController,
    viewModel: SendEip721ViewModel,
    addressViewModel: AddressViewModel,
    addressParserViewModel: AddressParserViewModel,
    parentNavId: Int,
) {

    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            AppBar(
                title = stringResource(R.string.SendNft_Title),
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = { navController.popBackStack() }
                    )
                )
            )
        }
    ) {
        Column(Modifier.padding(it)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                viewModel.uiState.imageUrl?.let { imageUrl ->
                    Spacer(Modifier.height(12.dp))
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = imageUrl,
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .heightIn(0.dp, 100.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.height(24.dp))
                headline1_leah(
                    text = viewModel.uiState.name
                )
                Spacer(Modifier.height(24.dp))
                HSAddressInput(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    viewModel = addressViewModel,
                    error = viewModel.uiState.addressError,
                    textPreprocessor = addressParserViewModel,
                    navController = navController,
                ) { address ->
                    viewModel.onEnterAddress(address)
                }
                Spacer(Modifier.height(24.dp))
                ButtonPrimaryYellow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                    title = stringResource(R.string.Button_Next),
                    onClick = {
                        viewModel.getSendData()?.let { sendData ->
                            navController.slideFromRight(
                                R.id.sendEvmConfirmationFragment,
                                SendEvmConfirmationModule.prepareParams(sendData, parentNavId)
                            )
                        }
                    },
                    enabled = viewModel.uiState.canBeSend
                )
            }
        }
    }
}
