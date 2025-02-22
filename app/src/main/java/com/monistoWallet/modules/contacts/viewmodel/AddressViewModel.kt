package com.monistoWallet.modules.contacts.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monistoWallet.R
import com.monistoWallet.core.managers.EvmBlockchainManager
import com.monistoWallet.core.managers.MarketKitWrapper
import com.monistoWallet.core.order
import com.monistoWallet.entities.Address
import com.monistoWallet.entities.DataState
import com.monistoWallet.modules.address.AddressParserChain
import com.monistoWallet.modules.address.AddressValidationException
import com.monistoWallet.modules.address.IAddressHandler
import com.monistoWallet.modules.contacts.ContactsRepository
import com.monistoWallet.modules.contacts.model.ContactAddress
import com.monistoWallet.ui.compose.TranslatableString
import com.wallet0x.marketkit.models.Blockchain
import com.wallet0x.marketkit.models.BlockchainType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressViewModel(
    private val contactUid: String?,
    private val contactsRepository: ContactsRepository,
    private val addressHandlerFactory: com.monistoWallet.modules.address.AddressHandlerFactory,
    evmBlockchainManager: EvmBlockchainManager,
    marketKit: MarketKitWrapper,
    contactAddress: ContactAddress?,
    definedAddresses: List<ContactAddress>?
) : ViewModel() {

    private val title = if (contactAddress == null)
        TranslatableString.ResString(R.string.Contacts_AddAddress)
    else
        TranslatableString.PlainString(contactAddress.blockchain.name)
    private var address = contactAddress?.address ?: ""
    private val editingAddress = contactAddress
    private var addressState: DataState<Address>? = contactAddress?.address?.let { DataState.Success(Address(it)) }
    private val availableBlockchains: List<Blockchain>

    init {
        availableBlockchains = if (contactAddress == null) {
            val allBlockchainTypes = EvmBlockchainManager.blockchainTypes + listOf(
                BlockchainType.Bitcoin,
                BlockchainType.BitcoinCash,
                BlockchainType.Dash,
                BlockchainType.Litecoin,
                BlockchainType.Zcash,
                BlockchainType.Solana,
                BlockchainType.BinanceChain,
                BlockchainType.ECash,
                BlockchainType.Tron,
                BlockchainType.Ton,
            )
            val definedBlockchainTypes = definedAddresses?.map { it.blockchain.type } ?: listOf()
            val availableBlockchainUids = allBlockchainTypes.filter { !definedBlockchainTypes.contains(it) }.map { it.uid }

            marketKit.blockchains(availableBlockchainUids).sortedBy { it.type.order }
        } else {
            listOf()
        }
    }

    private var blockchain = contactAddress?.blockchain ?: availableBlockchains.first()
    private var addressParser: AddressParserChain = addressHandlerFactory.parserChain(blockchain.type, true)

    var uiState by mutableStateOf(uiState())
        private set

    fun onEnterAddress(address: String) {
        this.address = address

        emitUiState()

        validateAddress(address)
    }

    fun onEnterBlockchain(blockchain: Blockchain) {
        this.blockchain = blockchain
        this.addressParser = addressHandlerFactory.parserChain(blockchain.type, true)

        emitUiState()

        validateAddress(address)
    }

    private var validationJob: Job? = null

    private fun validateAddress(address: String) {
        validationJob?.cancel()

        if (address.isEmpty()) {
            addressState = null
            emitUiState()
            return
        }

        validationJob = viewModelScope.launch {
            addressState = DataState.Loading
            emitUiState()

            addressState = try {
                val parsedAddress = parseAddress(addressParser, address.trim())
                ensureActive()
                contactsRepository.validateAddress(contactUid, ContactAddress(blockchain, parsedAddress.hex))
                DataState.Success(parsedAddress)
            } catch (error: Throwable) {
                ensureActive()
                DataState.Error(error)
            }
            emitUiState()
        }
    }

    private fun uiState() = UiState(
        headerTitle = title,
        editingAddress = editingAddress,
        addressState = addressState,
        address = address,
        blockchain = blockchain,
        canChangeBlockchain = editingAddress == null,
        showDelete = editingAddress != null,
        availableBlockchains = availableBlockchains,
        doneEnabled = addressState is DataState.Success
    )

    private fun emitUiState() {
        uiState = uiState()
    }

    private suspend fun parseAddress(addressParser: AddressParserChain, value: String): Address = withContext(Dispatchers.IO) {
        try {
            val resolvedAddress = addressParser.getAddressFromDomain(value)?.hex ?: value
            parse(resolvedAddress, addressParser.supportedAddressHandlers(resolvedAddress))
        } catch (error: Throwable) {
            throw AddressValidationException.Invalid(error, blockchain.name)
        }
    }

    private fun parse(value: String, supportedHandlers: List<IAddressHandler>): Address {
        if (supportedHandlers.isEmpty()) {
            throw AddressValidationException.Unsupported(blockchain.name)
        }

        try {
            return supportedHandlers.first().parseAddress(value)
        } catch (t: Throwable) {
            throw AddressValidationException.Invalid(t, blockchain.name)
        }
    }

    data class UiState(
        val headerTitle: TranslatableString,
        val editingAddress: ContactAddress?,
        val addressState: DataState<Address>?,
        val address: String,
        val blockchain: Blockchain,
        val canChangeBlockchain: Boolean,
        val showDelete: Boolean,
        val availableBlockchains: List<Blockchain>,
        val doneEnabled: Boolean
    )
}
