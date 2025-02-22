package com.monistoWallet.core.managers

import com.google.gson.annotations.SerializedName
import com.monistoWallet.R
import com.monistoWallet.core.IRestoreSettingsStorage
import com.monistoWallet.core.providers.Translator
import com.monistoWallet.entities.RestoreSettingRecord
import com.wallet0x.marketkit.models.BlockchainType
import com.wallet0x.marketkit.models.Token

class RestoreSettingsManager(
        private val storage: IRestoreSettingsStorage,
        private val zcashBirthdayProvider: ZcashBirthdayProvider
) {
    fun settings(account: com.monistoWallet.entities.Account, blockchainType: BlockchainType): RestoreSettings {
        val records = storage.restoreSettings(account.id, blockchainType.uid)

        val settings = RestoreSettings()
        records.forEach { record ->
            RestoreSettingType.fromString(record.key)?.let { type ->
                settings[type] = record.value
            }
        }

        return settings
    }

    fun accountSettingsInfo(account: com.monistoWallet.entities.Account): List<Triple<BlockchainType, RestoreSettingType, String>> {
        return storage.restoreSettings(account.id).mapNotNull { record ->
            RestoreSettingType.fromString(record.key)?.let { settingType ->
                val blockchainType = BlockchainType.fromUid(record.blockchainTypeUid)
                Triple(blockchainType, settingType, record.value)
            }
        }
    }

    fun save(settings: RestoreSettings, account: com.monistoWallet.entities.Account, blockchainType: BlockchainType) {
        val records = settings.values.map { (type, value) ->
            RestoreSettingRecord(account.id, blockchainType.uid, type.name, value)
        }

        storage.save(records)
    }

    fun getSettingValueForCreatedAccount(settingType: RestoreSettingType, blockchainType: BlockchainType): String? {
        return when (settingType) {
            RestoreSettingType.BirthdayHeight -> {
                when (blockchainType) {
                    BlockchainType.Zcash -> {
                        return zcashBirthdayProvider.getLatestCheckpointBlockHeight().toString()
                    }
                    else -> null
                }
            }
        }
    }

    fun getSettingsTitle(settingType: RestoreSettingType, token: Token): String {
        return when (settingType) {
            RestoreSettingType.BirthdayHeight -> Translator.getString(R.string.ManageAccount_BirthdayHeight, token.coin.code)
        }
    }

}

enum class RestoreSettingType {
    @SerializedName("birthday_height")
    BirthdayHeight;

    companion object {
        private val map = values().associateBy(RestoreSettingType::name)

        fun fromString(value: String?): RestoreSettingType? = map[value]
    }
}

class RestoreSettings {
    val values = mutableMapOf<RestoreSettingType, String>()

    var birthdayHeight: Long?
        get() = values[RestoreSettingType.BirthdayHeight]?.toLongOrNull()
        set(value) {
            values[RestoreSettingType.BirthdayHeight] = value?.toString() ?: ""
        }

    fun isNotEmpty() = values.isNotEmpty()

    operator fun get(key: RestoreSettingType): String? {
        return values[key]
    }

    operator fun set(key: RestoreSettingType, value: String) {
        values[key] = value
    }
}
