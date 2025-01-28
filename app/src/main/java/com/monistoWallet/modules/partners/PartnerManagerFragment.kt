package com.monistoWallet.modules.partners


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.monistoWallet.R
import com.monistoWallet.core.BaseComposeFragment
import com.monistoWallet.core.SnackbarDuration
import com.monistoWallet.core.helpers.HudHelper
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.ui.compose.ColoredTextStyle
import com.monistoWallet.ui.compose.ComposeAppTheme
import com.monistoWallet.ui.compose.components.AppBar
import com.monistoWallet.ui.compose.components.ButtonPrimaryYellow
import com.monistoWallet.ui.compose.components.ButtonSecondaryDefault
import com.monistoWallet.ui.compose.components.HSpacer
import com.monistoWallet.ui.compose.components.HsBackButton
import com.monistoWallet.ui.compose.components.TextImportantWarning
import com.monistoWallet.ui.compose.components.TextPreprocessor
import com.monistoWallet.ui.compose.components.TextPreprocessorImpl
import org.koin.androidx.compose.koinViewModel
import java.lang.reflect.Field


class PartnerManagerFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {

    }

}

@Composable
fun HUDProgress(placeHolder: String, show: Boolean) {
    var animate by remember { mutableStateOf(false) }

    if (show) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.DarkGray.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(25.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.6f)),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(vertical = 15.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colors.secondary,
                    progress = animateFloatAsState(targetValue = if (animate) 1f else 0f).value
                )
                Spacer(modifier = Modifier.height(25.dp))
                Text(
                    text = placeHolder,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onError,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 15.dp)
                )
            }
        }

        LaunchedEffect(key1 = true) {
            animate = true
        }
    }
}

@Composable
fun LevelView(
    status: String,
    completedTasks: Int,
    maxTasks: Int,
    progress: Float,
    modifier: Modifier = Modifier,
    ) {
    Column(
        modifier = modifier
            .background(
                color = Color.DarkGray,
                shape = RoundedCornerShape(10.dp)
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (status.isEmpty()) "Bronze" else status.capitalize(),
            style = MaterialTheme.typography.h6,
            color = Color.White,
            modifier = Modifier.padding(top = 15.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)

        ) {
            Text(
                text = stringResource(id = R.string.Purchased_Cards),
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.width(8.dp))

            LinearProgressIndicator(
                progress = progress,
                color = ComposeAppTheme.colors.yellowD,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "$completedTasks/$maxTasks",
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun IconFromViewModel(iconName: String) {
    val iconResourceId = getDrawableResourceId(iconName)

    Image(
        painter = painterResource(id = iconResourceId),
        contentDescription = null,
        modifier = Modifier
            .size(width = 32.dp, height = 35.dp)
            .offset(y = 40.dp)
    )
}

private fun getDrawableResourceId(name: String): Int {
    val field: Field = R.drawable::class.java.getDeclaredField(name)
    return field.getInt(null)
}

@Composable
private fun DropdownMenuContent(
    expanded: Boolean,
    options: List<DropdownMenuOption>,
    onOptionSelected: (DropdownMenuOption) -> Unit,
    modifier: Modifier
) {
    if (expanded) {
        Column(
            modifier = modifier
                .background(Color.DarkGray)
                .width(100.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(option = option, onClick = { onOptionSelected(option) })
            }
        }
    }
}

@Composable
private fun DropdownMenuItem(
    option: DropdownMenuOption,
    onClick: () -> Unit
) {
    Text(
        text = option.option,
        style = MaterialTheme.typography.body1,
        color = Color.Gray,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .clickable(onClick = onClick)
    )
}

data class DropdownMenuOption(val option: String)


fun String.trimMiddle(maxLength: Int): String {
    return if (this.length > maxLength) {
        val halfLength = (maxLength - 3) / 2 // Вычисляем половину длины строки, вычитая 3 для трех многоточий
        "${this.substring(0, halfLength)}...${this.substring(this.length - halfLength)}"
    } else {
        this
    }
}