package com.monistoWallet.entities.transactionrecords.tron

import com.monistoWallet.core.managers.SpamManager
import com.monistoWallet.entities.TransactionValue
import com.monistoWallet.entities.transactionrecords.evm.EvmTransactionRecord
import com.monistoWallet.entities.transactionrecords.evm.TransferEvent
import com.monistoWallet.modules.transactions.TransactionSource
import com.wallet0x.marketkit.models.Token
import com.wallet0x.tronkit.models.Transaction

class TronExternalContractCallTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    spamManager: SpamManager,
    val incomingEvents: List<TransferEvent>,
    val outgoingEvents: List<TransferEvent>
) : TronTransactionRecord(
    transaction = transaction,
    baseToken = baseToken,
    source = source,
    foreignTransaction = true,
    spam = spamManager.isSpam(incomingEvents, outgoingEvents)
) {

    override val mainValue: TransactionValue?
        get() {
            val (incomingValues, outgoingValues) = EvmTransactionRecord.combined(incomingEvents, outgoingEvents)

            return when {
                (incomingValues.isEmpty() && outgoingValues.size == 1) -> outgoingValues.first()
                (incomingValues.size == 1 && outgoingValues.isEmpty()) -> incomingValues.first()
                else -> null
            }
        }

}
