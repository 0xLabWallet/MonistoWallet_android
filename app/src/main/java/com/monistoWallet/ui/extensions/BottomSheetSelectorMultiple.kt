package com.monistoWallet.ui.extensions

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monistoWallet.R
import com.monistoWallet.modules.market.ImageSource
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.CoinImage
import com.monistoWallet.ui.compose.components.HsSwitch
import com.monistoWallet.ui.compose.components.RowUniversal
import com.monistoWallet.ui.compose.components.SectionUniversalItem
import com.monistoWallet.ui.compose.components.TextImportantWarning
import com.monistoWallet.ui.compose.components.body_leah
import com.monistoWallet.ui.compose.components.subhead2_grey
import com.monistoWallet.ui.helpers.TextHelper
import com.monistoWallet.core.helpers.HudHelper

@Composable
fun BottomSheetSelectorMultiple(
    config: BottomSheetSelectorMultipleDialog.Config,
    onItemsSelected: (List<Int>) -> Unit,
    onCloseClick: () -> Unit,
) {
    BottomSheetSelectorMultiple(
        title = config.title,
        icon = config.icon,
        items = config.viewItems,
        selectedIndexes = config.selectedIndexes,
        warningTitle = config.descriptionTitle,
        warning = config.description,
        notifyUnchanged = true,
        allowEmpty = config.allowEmpty,
        onItemsSelected = onItemsSelected,
        onCloseClick = onCloseClick,
    )
}

@Composable
fun BottomSheetSelectorMultiple(
    title: String,
    icon: com.monistoWallet.modules.market.ImageSource,
    items: List<BottomSheetSelectorViewItem>,
    selectedIndexes: List<Int>,
    warningTitle: String?,
    warning: String?,
    notifyUnchanged: Boolean,
    allowEmpty: Boolean,
    onItemsSelected: (List<Int>) -> Unit,
    onCloseClick: () -> Unit,
) {
    val selected = remember(selectedIndexes, items) { mutableStateListOf<Int>().apply { addAll(selectedIndexes) } }

    ComposeAppTheme {
        BottomSheetHeader(
            iconPainter = icon.painter(),
            title = title,
            onCloseClick = onCloseClick
        ) {
            val localView = LocalView.current
            warning?.let {
                TextImportantWarning(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    title = warningTitle,
                    text = it
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, ComposeAppTheme.colors.steel10, RoundedCornerShape(12.dp))
            ) {
                items.forEachIndexed { index, item ->
                    val onClick = if (item.copyableString != null) {
                        {
                            HudHelper.showSuccessMessage(localView, R.string.Hud_Text_Copied)
                            TextHelper.copyText(item.copyableString)
                        }
                    } else {
                        null
                    }

                    SectionUniversalItem(
                        borderTop = index != 0,
                    ) {
                        RowUniversal(
                            onClick = onClick,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalPadding = 0.dp
                        ) {
                            item.icon?.let { url ->
                                CoinImage(
                                    iconUrl = url,
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .size(32.dp)
                                )
                            }
                            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                                body_leah(text = item.title)
                                subhead2_grey(text = item.subtitle)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            HsSwitch(
                                modifier = Modifier.padding(start = 5.dp),
                                checked = selected.contains(index),
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selected.add(index)
                                    } else {
                                        selected.remove(index)
                                    }
                                },
                            )
                        }
                    }
                }
            }
            ButtonPrimaryYellow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 32.dp),
                title = stringResource(R.string.Button_Done),
                onClick = {
                    if (notifyUnchanged || !equals(selectedIndexes, selected)) {
                        onItemsSelected(selected)
                    }
                    onCloseClick.invoke()
                },
                enabled = allowEmpty || selected.isNotEmpty()
            )
        }
    }
}

private fun equals(list1: List<Int>, list2: List<Int>): Boolean {
    return (list1 - list2).isEmpty() && (list2 - list1).isEmpty()
}