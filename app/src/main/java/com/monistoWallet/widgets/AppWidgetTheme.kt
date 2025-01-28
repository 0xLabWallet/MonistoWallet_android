package com.monistoWallet.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.glance.color.ColorProvider
import androidx.glance.text.FontWeight
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.monistoWallet.ui.compose.darkPalette
import com.monistoWallet.ui.compose.lightPalette

object AppWidgetTheme {
    val colors: ColorProviders
        @Composable
        @ReadOnlyComposable
        get() = LocalColorProviders.current

    val textStyles: TextStyles = TextStyles()
}

class TextStyles {
    @Composable
    fun c3(textAlign: TextAlign = TextAlign.Start) =
        TextStyle(
            color = AppWidgetTheme.colors.jacob,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = textAlign
        )

    @Composable
    fun d1(textAlign: TextAlign = TextAlign.Start) =
        TextStyle(
            color = AppWidgetTheme.colors.grey,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            textAlign = textAlign
        )

    @Composable
    fun d3(textAlign: TextAlign = TextAlign.Start) =
        TextStyle(
            color = AppWidgetTheme.colors.jacob,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            textAlign = textAlign
        )

    @Composable
    fun micro(textAlign: TextAlign = TextAlign.Start) =
        TextStyle(
            color = AppWidgetTheme.colors.grey,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            textAlign = textAlign
        )
}

@Composable
fun AppWidgetTheme(colors: ColorProviders = AppWidgetTheme.colors, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalColorProviders provides colors) {
        content()
    }
}

internal val LocalColorProviders = staticCompositionLocalOf {
    ColorProviders(
        jacob = ColorProvider(lightPalette.jacob, darkPalette.jacob),
        remus = ColorProvider(lightPalette.remus, darkPalette.remus),
        lucian = ColorProvider(lightPalette.lucian, darkPalette.lucian),
        tyler = ColorProvider(lightPalette.tyler, darkPalette.tyler),
        bran = ColorProvider(lightPalette.bran, darkPalette.bran),
        leah = ColorProvider(lightPalette.leah, darkPalette.leah),
        claude = ColorProvider(lightPalette.claude, darkPalette.claude),
        lawrence = ColorProvider(lightPalette.lawrence, darkPalette.lawrence),
        jeremy = ColorProvider(lightPalette.jeremy, darkPalette.jeremy),
        laguna = ColorProvider(lightPalette.laguna, darkPalette.laguna),
        raina = ColorProvider(lightPalette.raina, darkPalette.raina),
    )
}

data class ColorProviders(
    val jacob: ColorProvider,
    val remus: ColorProvider,
    val lucian: ColorProvider,
    val tyler: ColorProvider,
    val bran: ColorProvider,
    val leah: ColorProvider,
    val claude: ColorProvider,
    val lawrence: ColorProvider,
    val jeremy: ColorProvider,
    val laguna: ColorProvider,
    val raina: ColorProvider,

    //base colors
    val grey: ColorProvider = ColorProvider(com.monistoWallet.ui.compose.Grey)
)

//base colors
val transparent = Color.Transparent
val dark = com.monistoWallet.ui.compose.Dark
val light = com.monistoWallet.ui.compose.Light
val white = Color.White
val black50 = com.monistoWallet.ui.compose.Black50
val issykBlue = Color(0xFF3372FF)
val lightGrey = com.monistoWallet.ui.compose.LightGrey
val steelLight = com.monistoWallet.ui.compose.SteelLight
val steelDark = com.monistoWallet.ui.compose.SteelDark
val steel10 = com.monistoWallet.ui.compose.Steel10
val steel20 = com.monistoWallet.ui.compose.Steel20
val grey = com.monistoWallet.ui.compose.Grey
val grey50 = com.monistoWallet.ui.compose.Grey50
val yellow50 = com.monistoWallet.ui.compose.Yellow50
val yellow20 = com.monistoWallet.ui.compose.Yellow20

val yellowD = com.monistoWallet.ui.compose.YellowD
val yellowL = com.monistoWallet.ui.compose.YellowL
val greenD = com.monistoWallet.ui.compose.GreenD
val greenL = com.monistoWallet.ui.compose.GreenL
val green50 = com.monistoWallet.ui.compose.Green50
val redD = com.monistoWallet.ui.compose.RedD
val redL = com.monistoWallet.ui.compose.RedL
val elenaD = Color(0xFF6E7899)
val red50 = com.monistoWallet.ui.compose.Red50
val red20 = com.monistoWallet.ui.compose.Red20

