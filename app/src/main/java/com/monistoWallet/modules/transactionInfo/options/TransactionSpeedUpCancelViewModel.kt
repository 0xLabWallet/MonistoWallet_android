package com.monistoWallet.modules.transactionInfo.options

import androidx.lifecycle.ViewModel
import com.monistoWallet.R
import com.monistoWallet.core.providers.Translator
import com.wallet0x.marketkit.models.Token

class TransactionSpeedUpCancelViewModel(
    baseToken: Token,
    optionType: TransactionInfoOptionsModule.Type,
    val isTransactionPending: Boolean
) : ViewModel() {

    val title: String = when (optionType) {
        TransactionInfoOptionsModule.Type.SpeedUp -> Translator.getString(R.string.TransactionInfoOptions_SpeedUp_Title)
        TransactionInfoOptionsModule.Type.Cancel -> Translator.getString(R.string.TransactionInfoOptions_Cancel_Title)
    }

    val description: String = when (optionType) {
        TransactionInfoOptionsModule.Type.SpeedUp -> Translator.getString(R.string.TransactionInfoOptions_SpeedUp_Description)
        TransactionInfoOptionsModule.Type.Cancel -> Translator.getString(R.string.TransactionInfoOptions_Cancel_Description, baseToken.coin.code)
    }

    val buttonTitle: String = when (optionType) {
        TransactionInfoOptionsModule.Type.SpeedUp -> Translator.getString(R.string.TransactionInfoOptions_SpeedUp_Button)
        TransactionInfoOptionsModule.Type.Cancel -> Translator.getString(R.string.TransactionInfoOptions_Cancel_Button)
    }

}
