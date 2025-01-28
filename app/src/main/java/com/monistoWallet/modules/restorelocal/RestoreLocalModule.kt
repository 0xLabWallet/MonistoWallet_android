package com.monistoWallet.modules.restorelocal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monistoWallet.core.App
import com.monistoWallet.entities.AccountType
import com.monistoWallet.entities.DataState
import com.monistoWallet.modules.backuplocal.fullbackup.BackupViewItemFactory
import com.monistoWallet.modules.backuplocal.fullbackup.SelectBackupItemsViewModel.OtherBackupViewItem
import com.monistoWallet.modules.backuplocal.fullbackup.SelectBackupItemsViewModel.WalletBackupViewItem

object RestoreLocalModule {

    class Factory(private val backupJsonString: String?, private val fileName: String?) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RestoreLocalViewModel(backupJsonString, App.accountFactory, App.backupProvider, BackupViewItemFactory(), fileName) as T
        }
    }

    data class UiState(
        val passphraseState: DataState.Error?,
        val showButtonSpinner: Boolean,
        val parseError: Exception?,
        val showSelectCoins: AccountType?,
        val manualBackup: Boolean,
        val restored: Boolean,
        var walletBackupViewItems: List<WalletBackupViewItem>,
        var otherBackupViewItems: List<OtherBackupViewItem>,
        val showBackupItems: Boolean
    )
}