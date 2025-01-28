package com.monistoWallet.core.managers

import android.content.Context
import com.monistoWallet.core.AppLogger
import com.monistoWallet.core.ILocalStorage
import com.monistoWallet.core.ITorManager
import com.monistoWallet.core.tor.ConnectionStatus
import com.monistoWallet.core.tor.Tor
import com.monistoWallet.core.tor.torcore.TorConstants
import com.monistoWallet.core.tor.torcore.TorOperator
import com.monistoWallet.core.tor.torutils.TorConnectionManager
import com.monistoWallet.modules.settings.security.tor.TorStatus
import io.reactivex.Single
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.Executors

class TorManager(context: Context, val localStorage: ILocalStorage) : ITorManager, TorOperator.Listener {

    private val logger = AppLogger("tor status")
    private val _torStatusFlow = MutableStateFlow(TorStatus.Closed)
    override val torStatusFlow = _torStatusFlow

    private val executorService = Executors.newCachedThreadPool()
    private val torOperator: TorOperator by lazy {
        TorOperator(Tor.Settings(context), this)
    }

    init {
        if (localStorage.torEnabled) {
            start()
        }
    }

    override fun start() {
        enableProxy()
        executorService.execute {
            torOperator.start()
        }
    }

    override fun stop(): Single<Boolean> {
        disableProxy()
        return torOperator.stop()
    }

    override fun setTorAsEnabled() {
        localStorage.torEnabled = true
        logger.info("Tor enabled")
    }

    override fun setTorAsDisabled() {
        localStorage.torEnabled = false
        logger.info("Tor disabled")
    }

    override val isTorEnabled: Boolean
        get() = localStorage.torEnabled

    override fun statusUpdate(torInfo: Tor.Info) {
        _torStatusFlow.update { getStatus(torInfo) }
    }

    private fun getStatus(torInfo: Tor.Info): TorStatus {
        return when (torInfo.connection.status) {
            ConnectionStatus.CONNECTED -> TorStatus.Connected
            ConnectionStatus.CONNECTING -> TorStatus.Connecting
            ConnectionStatus.CLOSED -> TorStatus.Closed
            ConnectionStatus.FAILED -> TorStatus.Failed
        }
    }

    private fun enableProxy() {
        TorConnectionManager.setSystemProxy(
            true,
            TorConstants.IP_LOCALHOST,
            TorConstants.HTTP_PROXY_PORT_DEFAULT,
            TorConstants.SOCKS_PROXY_PORT_DEFAULT
        )
    }

    private fun disableProxy() {
        TorConnectionManager.disableSystemProxy()
    }

}
