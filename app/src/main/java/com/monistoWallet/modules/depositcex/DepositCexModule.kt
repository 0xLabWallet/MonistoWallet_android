package com.monistoWallet.modules.depositcex

import com.monistoWallet.core.providers.CexAsset
import com.monistoWallet.modules.market.ImageSource

object DepositCexModule {

    data class CexCoinViewItem(
        val title: String,
        val subtitle: String,
        val coinIconUrl: String?,
        val coinIconPlaceholder: Int,
        val cexAsset: CexAsset,
        val depositEnabled: Boolean,
        val withdrawEnabled: Boolean,
    )

    data class NetworkViewItem(
        val title: String,
        val imageSource: com.monistoWallet.modules.market.ImageSource,
    )

}
