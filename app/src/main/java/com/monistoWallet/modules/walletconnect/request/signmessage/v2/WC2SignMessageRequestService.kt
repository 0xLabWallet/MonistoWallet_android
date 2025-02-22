package com.monistoWallet.modules.walletconnect.request.signmessage.v2

import com.monistoWallet.core.shorten
import com.monistoWallet.core.toHexString
import com.monistoWallet.modules.walletconnect.request.WCRequestChain
import com.monistoWallet.modules.walletconnect.request.signmessage.SignMessage
import com.monistoWallet.modules.walletconnect.request.signmessage.WCSignMessageRequestModule
import com.monistoWallet.modules.walletconnect.version2.WC2SessionManager
import com.monistoWallet.modules.walletconnect.version2.WC2SignMessageRequest
import com.wallet0x.ethereumkit.core.hexStringToByteArray

class WC2SignMessageRequestService(
    private val requestData: WC2SessionManager.RequestData,
    private val sessionManager: WC2SessionManager,
) : WCSignMessageRequestModule.RequestAction {

    override val isLegacySignRequest = false

    override val dAppName: String?
        get() = pendingRequest.dAppName

    val evmKitWrapper by lazy {
        requestData.evmKitWrapper
    }

    override val chain: WCRequestChain by lazy {
        val evmKit = evmKitWrapper.evmKit
        val chain = evmKit.chain
        WCRequestChain(chain.name, evmKit.receiveAddress.eip55.shorten())
    }

    private val pendingRequest by lazy {
        requestData.pendingRequest as WC2SignMessageRequest
    }

    override val message: SignMessage by lazy {
        pendingRequest.message
    }

    private fun signMessage(request: WC2SignMessageRequest): ByteArray? {
        return when (request.message) {
            is SignMessage.Message,
            is SignMessage.PersonalMessage -> {
                evmKitWrapper.signer?.signByteArray(message = request.rawData.hexStringToByteArray())
            }
            is SignMessage.TypedMessage -> {
                evmKitWrapper.signer?.signTypedData(rawJsonMessage = request.rawData)
            }
        }
    }

    override fun sign() {
        signMessage(pendingRequest)?.let{
            sessionManager.service.respondPendingRequest(
                pendingRequest.id,
                pendingRequest.topic,
                it.toHexString()
            )
        }
    }

    override fun reject() {
        sessionManager.service.rejectRequest(pendingRequest.topic, pendingRequest.id)
    }
}
