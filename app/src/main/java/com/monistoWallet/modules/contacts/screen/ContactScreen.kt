package com.monistoWallet.modules.contacts.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.monistoWallet.R
import com.monistoWallet.core.imageUrl
import com.monistoWallet.entities.DataState
import com.monistoWallet.modules.contacts.model.ContactAddress
import com.monistoWallet.modules.contacts.viewmodel.ContactViewModel
import com.monistoWallet.modules.contacts.viewmodel.ContactViewModel.AddressViewItem
import com.monistoWallet.modules.swap.settings.Caution
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.components.*
import com.monistoWallet.ui.extensions.BottomSheetHeader
import com.monistoWallet.core.SnackbarDuration
import com.monistoWallet.core.helpers.HudHelper
import kotlinx.coroutines.launch

enum class ContactScreenBottomSheetType {
    DeleteConfirmation, DiscardChangesConfirmation
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContactScreen(
    viewModel: ContactViewModel,
    onNavigateToBack: () -> Unit,
    onNavigateToAddress: (ContactAddress?) -> Unit
) {
    val uiState = viewModel.uiState
    val view = LocalView.current

    var bottomSheetType: ContactScreenBottomSheetType? by remember { mutableStateOf(null) }
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.closeWithSuccess) {
        if (uiState.closeWithSuccess) {
            focusManager.clearFocus(true)

            HudHelper.showSuccessMessage(view, R.string.Hud_Text_Done, SnackbarDuration.SHORT)

            onNavigateToBack()
        }
    }

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetBackgroundColor = ComposeAppTheme.colors.transparent,
        sheetContent = {
            when (bottomSheetType) {
                null -> {
                    Spacer(modifier = Modifier.height(1.dp))
                }

                ContactScreenBottomSheetType.DeleteConfirmation -> {
                    ConfirmationBottomSheet(
                        title = stringResource(R.string.Contacts_DeleteContact),
                        text = stringResource(R.string.Contacts_DeleteContact_Warning),
                        iconPainter = painterResource(R.drawable.ic_delete_20),
                        iconTint = ColorFilter.tint(ComposeAppTheme.colors.lucian),
                        confirmText = stringResource(R.string.Button_Delete),
                        cautionType = Caution.Type.Error,
                        cancelText = stringResource(R.string.Button_Cancel),
                        onConfirm = viewModel::onDelete,
                        onClose = { coroutineScope.launch { modalBottomSheetState.hide() } }
                    )
                }

                ContactScreenBottomSheetType.DiscardChangesConfirmation -> {
                    ConfirmationBottomSheet(
                        title = stringResource(R.string.Alert_TitleWarning),
                        text = stringResource(R.string.Contacts_DiscardChanges_Warning),
                        iconPainter = painterResource(R.drawable.icon_warning_2_20),
                        iconTint = ColorFilter.tint(ComposeAppTheme.colors.jacob),
                        confirmText = stringResource(R.string.Contacts_DiscardChanges),
                        cautionType = Caution.Type.Error,
                        cancelText = stringResource(R.string.Contacts_KeepEditing),
                        onConfirm = onNavigateToBack,
                        onClose = { coroutineScope.launch { modalBottomSheetState.hide() } }
                    )
                }
            }
        },
    ) {
        val confirmNavigateToBack: () -> Unit = {
            if (uiState.confirmBack) {
                bottomSheetType = ContactScreenBottomSheetType.DiscardChangesConfirmation
                coroutineScope.launch {
                    focusManager.clearFocus(true)
                    modalBottomSheetState.show()
                }
            } else {
                focusManager.clearFocus(true)
                onNavigateToBack()
            }
        }

        BackHandler {
            if (modalBottomSheetState.isVisible) {
                coroutineScope.launch { modalBottomSheetState.hide() }
            } else {
                confirmNavigateToBack()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = ComposeAppTheme.colors.tyler)
        ) {
            AppBar(
                title = uiState.headerTitle.getString(),
                navigationIcon = {
                    HsBackButton {
                        confirmNavigateToBack()
                    }
                },
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Save),
                        enabled = uiState.saveEnabled,
                        onClick = viewModel::onSave
                    )
                )
            )

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Spacer(Modifier.height(12.dp))
                FormsInput(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .padding(horizontal = 16.dp)
                        .onGloballyPositioned {
                            if (uiState.focusOnContactName) {
                                focusRequester.requestFocus()
                            }
                        },
                    initial = viewModel.contact.name,
                    pasteEnabled = false,
                    state = uiState.error?.let { DataState.Error(it) },
                    hint = stringResource(R.string.Contacts_NameHint),
                    onValueChange = viewModel::onNameChange
                )

                Addresses(
                    addressViewItems = uiState.addressViewItems,
                    onClickAddress = onNavigateToAddress
                )

                ActionButtons(
                    onAddAddress = { onNavigateToAddress(null) },
                    showDelete = uiState.showDelete,
                    onDeleteContact = {
                        bottomSheetType = ContactScreenBottomSheetType.DeleteConfirmation
                        coroutineScope.launch {
                            modalBottomSheetState.show()
                        }
                    }
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ConfirmationBottomSheet(
    title: String,
    text: String,
    iconPainter: Painter,
    iconTint: ColorFilter,
    confirmText: String,
    cautionType: Caution.Type,
    cancelText: String,
    onConfirm: () -> Unit,
    onClose: () -> Unit
) {
    BottomSheetHeader(
        iconPainter = iconPainter,
        iconTint = iconTint,
        title = title,
        onCloseClick = onClose
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TextImportantWarning(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = text
        )

        Spacer(modifier = Modifier.height(32.dp))

        when (cautionType) {
            Caution.Type.Error -> {
                ButtonPrimaryRed(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    title = confirmText,
                    onClick = {
                        onConfirm()
                    }
                )
            }

            Caution.Type.Warning -> {
                ButtonPrimaryYellow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    title = confirmText,
                    onClick = {
                        onConfirm()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        ButtonPrimaryTransparent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            title = cancelText,
            onClick = onClose
        )
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ActionButtons(
    onAddAddress: () -> Unit,
    showDelete: Boolean,
    onDeleteContact: () -> Unit
) {
    Spacer(Modifier.height(32.dp))
    CellUniversalLawrenceSection(
        buildList {
            add {
                AddAddressButton(onAddAddress)
            }
            if (showDelete) {
                add {
                    DeleteContactButton(onDeleteContact)
                }
            }
        }
    )
}

@Composable
private fun DeleteContactButton(onClick: () -> Unit) {
    RowUniversal(
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.padding(horizontal = 16.dp),
            painter = painterResource(R.drawable.ic_delete_20),
            contentDescription = null,
            tint = ComposeAppTheme.colors.lucian
        )
        body_lucian(
            text = stringResource(R.string.Contacts_DeleteContact),
        )
    }
}

@Composable
private fun AddAddressButton(onClick: () -> Unit) {
    RowUniversal(
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.padding(horizontal = 16.dp),
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = null,
            tint = ComposeAppTheme.colors.jacob
        )
        body_jacob(text = stringResource(R.string.Contacts_AddAddress))
    }
}

@Composable
private fun Addresses(
    addressViewItems: List<AddressViewItem>,
    onClickAddress: (ContactAddress) -> Unit
) {
    if (addressViewItems.isNotEmpty()) {
        Spacer(Modifier.height(32.dp))
        CellUniversalLawrenceSection(addressViewItems) { addressViewItem ->
            ContactAddress(addressViewItem) {
                onClickAddress(addressViewItem.contactAddress)
            }
        }
    }
}

@Composable
private fun ContactAddress(
    addressViewItem: AddressViewItem,
    onClickEdit: () -> Unit
) {
    RowUniversal(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = onClickEdit
    ) {
        Image(
            modifier = Modifier
                .padding(end = 16.dp)
                .size(32.dp),
            painter = rememberAsyncImagePainter(
                model = addressViewItem.blockchain.type.imageUrl,
                error = painterResource(R.drawable.ic_platform_placeholder_32)
            ),
            contentDescription = null,
        )
        Column(modifier = Modifier.weight(1f)) {
            body_leah(text = addressViewItem.blockchain.name)
            subhead2_grey(text = addressViewItem.contactAddress.address)
        }
        Spacer(Modifier.width(9.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_edit_20),
            contentDescription = null,
            tint = if (addressViewItem.edited) ComposeAppTheme.colors.jacob else ComposeAppTheme.colors.grey
        )
    }
}
