package com.monistoWallet.modules.contacts.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.monistoWallet.R
import com.monistoWallet.core.order
import com.monistoWallet.modules.contacts.ContactsModule.ContactValidationException
import com.monistoWallet.modules.contacts.ContactsRepository
import com.monistoWallet.modules.contacts.model.Contact
import com.monistoWallet.modules.contacts.model.ContactAddress
import com.monistoWallet.ui.compose.TranslatableString
import com.wallet0x.marketkit.models.Blockchain
import java.util.UUID

class ContactViewModel(
    private val repository: ContactsRepository,
    existingContact: Contact?,
    newAddress: ContactAddress?
) : ViewModel() {

    val contact = existingContact ?: Contact(UUID.randomUUID().toString(), "", listOf())
    private val title = if (existingContact == null)
        TranslatableString.ResString(R.string.Contacts_NewContact)
    else
        TranslatableString.PlainString(existingContact.name)

    private var contactName = contact.name
    private var addresses: MutableMap<Blockchain, ContactAddress> = contact.addresses.associateBy { it.blockchain }.toMutableMap()
    private var addressViewItems: List<AddressViewItem> = addressViewItems()
    private val isNewContact = existingContact == null
    private var closeAfterSave = false
    private var error: ContactValidationException? = null

    var uiState by mutableStateOf(uiState())
        private set

    init {
        newAddress?.let {
            setAddress(it)
        }
    }

    fun onNameChange(name: String) {
        contactName = name

        error = try {
            repository.validateContactName(contactUid = contact.uid, name = name)
            null
        } catch (ex: ContactValidationException.DuplicateContactName) {
            ex
        }

        emitUiState()
    }

    fun onSave() {
        val editedContact = contact.copy(
            name = contactName,
            addresses = uiState.addressViewItems.map { it.contactAddress }
        )
        repository.save(editedContact)

        closeAfterSave = true

        emitUiState()
    }

    fun onDelete() {
        repository.delete(contact.uid)

        closeAfterSave = true

        emitUiState()
    }

    fun setAddress(address: ContactAddress) {
        addresses[address.blockchain] = address
        addressViewItems = addressViewItems()

        emitUiState()
    }

    fun deleteAddress(address: ContactAddress) {
        addresses.remove(address.blockchain)
        addressViewItems = addressViewItems()

        emitUiState()
    }

    private fun hasChanges(): Boolean {
        val savedAddresses = contact.addresses.toSet()
        val newAddresses = addresses.values.toSet()
        val addressesChanged = savedAddresses.size != newAddresses.size || (savedAddresses.toMutableSet() + newAddresses) != savedAddresses

        return contactName != contact.name || addressesChanged
    }

    private fun isSaveEnabled(): Boolean {
        return addresses.isNotEmpty() && error == null && contactName.isNotBlank() && hasChanges()
    }

    private fun emitUiState() {
        uiState = uiState()
    }

    private fun addressViewItems(): List<AddressViewItem> {
        val sortedAddresses = addresses.values.sortedBy { it.blockchain.type.order }
        val savedAddresses = contact.addresses.associateBy { it.blockchain }
        return sortedAddresses.map { AddressViewItem(it, edited = it != savedAddresses[it.blockchain]) }.sortedByDescending {
            it.edited
        }
    }

    private fun uiState() = UiState(
        headerTitle = title,
        addressViewItems = addressViewItems,
        saveEnabled = isSaveEnabled(),
        confirmBack = hasChanges(),
        showDelete = !isNewContact,
        focusOnContactName = isNewContact,
        closeWithSuccess = closeAfterSave,
        error = error
    )

    data class UiState(
        val headerTitle: TranslatableString,
        val addressViewItems: List<AddressViewItem>,
        val saveEnabled: Boolean,
        val confirmBack: Boolean,
        val showDelete: Boolean,
        val focusOnContactName: Boolean,
        val closeWithSuccess: Boolean,
        val error: ContactValidationException?
    )

    data class AddressViewItem(
        val contactAddress: ContactAddress,
        val edited: Boolean
    ) {
        val blockchain: Blockchain
            get() = contactAddress.blockchain
    }

}
