package com.monistoWallet.modules.settings.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.R
import com.monistoWallet.core.App
import com.monistoWallet.modules.theme.ThemeService
import com.monistoWallet.ui.compose.TranslatableString
import com.monistoWallet.ui.compose.WithTranslatableTitle

object AppearanceModule {

    class Factory() : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val launchScreenService = LaunchScreenService(com.monistoWallet.core.App.localStorage)
            val appIconService = AppIconService(com.monistoWallet.core.App.localStorage)
            val themeService = ThemeService(com.monistoWallet.core.App.localStorage)
            return AppearanceViewModel(
                launchScreenService,
                appIconService,
                themeService,
                com.monistoWallet.core.App.baseTokenManager,
                com.monistoWallet.core.App.balanceViewTypeManager,
                com.monistoWallet.core.App.localStorage
            ) as T
        }
    }

}

enum class AppIcon(val icon: Int, val titleText: String) : WithTranslatableTitle {
    Main(R.drawable.layer_4, "Main"),
    Dark(R.drawable.layer_4, "Dark"),
    Mono(R.drawable.layer_4, "Mono"),
    Leo(R.drawable.launcher_leo_preview, "Leo"),
    Mustang(R.drawable.launcher_mustang_preview, "Mustang"),
    Yak(R.drawable.launcher_yak_preview, "Yak"),
    Punk(R.drawable.launcher_punk_preview, "Punk"),
    Ape(R.drawable.launcher_ape_preview, "#1874"),
    Ball8(R.drawable.launcher_8ball_preview, "8ball");

    override val title: TranslatableString
        get() = TranslatableString.PlainString(titleText)

    val launcherName: String
        get() = "${com.monistoWallet.core.App.instance.packageName}.${this.name}LauncherAlias"


    companion object {
        private val map = values().associateBy(AppIcon::name)
        private val titleMap = values().associateBy(AppIcon::titleText)

        fun fromString(type: String?): AppIcon? = map[type]
        fun fromTitle(title: String?): AppIcon? = titleMap[title]
    }
}