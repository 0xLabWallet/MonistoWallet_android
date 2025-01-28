package com.monistoWallet.core.storage.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.monistoWallet.core.App

object Migration_56_57 : Migration(56, 57) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE AccountRecord ADD `level` INTEGER NOT NULL DEFAULT 0")
        database.execSQL("CREATE TABLE IF NOT EXISTS `Pin` (`level` INTEGER NOT NULL, `passcode` TEXT, PRIMARY KEY(`level`))")

        com.monistoWallet.core.App.pinSettingsStorage.pin?.let {
            database.execSQL("INSERT INTO `Pin` VALUES(0, ?)", arrayOf(it))
        }
    }
}
