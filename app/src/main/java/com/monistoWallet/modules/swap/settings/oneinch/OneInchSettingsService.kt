package com.monistoWallet.modules.swap.settings.oneinch

import android.util.Range
import com.monistoWallet.entities.Address
import com.monistoWallet.entities.DataState
import com.monistoWallet.modules.swap.settings.IRecipientAddressService
import com.monistoWallet.modules.swap.settings.ISwapSlippageService
import com.monistoWallet.modules.swap.settings.SwapSettingsModule.InvalidSlippageType
import com.monistoWallet.modules.swap.settings.SwapSettingsModule.SwapSettingsError
import com.monistoWallet.modules.swap.settings.oneinch.OneInchSwapSettingsModule.OneInchSwapSettings
import com.monistoWallet.modules.swap.settings.oneinch.OneInchSwapSettingsModule.State
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.math.BigDecimal

class OneInchSettingsService(
    address: Address?,
    slippage: BigDecimal?,
) : IRecipientAddressService, ISwapSlippageService {

    val stateObservable = BehaviorSubject.createDefault<State>(State.Invalid)
    var errors: List<Throwable> = listOf()

    private var recipient: Address? = address
    private val swapSettings = OneInchSwapSettings(recipient = address, slippage = slippage ?: OneInchSwapSettingsModule.defaultSlippage)

    var state: State = State.Valid(swapSettings)
        private set(value) {
            field = value
            stateObservable.onNext(value)
        }
    private var recipientError: Throwable? = null
        set(value) {
            field = value

            val state = if (value == null) {
                DataState.Success(Unit)
            } else {
                DataState.Error(value)
            }
            recipientAddressState.onNext(state)
        }

    override val initialAddress: Address?
        get() {
            val state = state
            if (state is State.Valid) {
                return state.swapSettings.recipient
            }

            return null
        }

    override val recipientAddressState = BehaviorSubject.create<DataState<Unit>>()

    override fun setRecipientAddress(address: Address?) {
        recipient = address
        sync()
    }

    override fun updateRecipientError(error: Throwable?) {
        recipientError = error
        sync()
    }

    override fun setRecipientAmount(amount: BigDecimal) {
    }

    private val limitSlippageBounds = Range(BigDecimal("0.01"), BigDecimal("50"))
    private val usualHighestSlippage = BigDecimal(5)
    private var slippage: BigDecimal = swapSettings.slippage

    override val initialSlippage: BigDecimal?
        get() = state.let {
            if (it is State.Valid && it.swapSettings.slippage.compareTo(defaultSlippage) != 0) {
                it.swapSettings.slippage.stripTrailingZeros()
            } else {
                null
            }
        }
    override val defaultSlippage = OneInchSwapSettingsModule.defaultSlippage

    override val recommendedSlippages = listOf(BigDecimal("0.1"), BigDecimal("3"))

    override val slippageError: Throwable?
        get() = getSlippageError(errors)

    override val unusualSlippage get() = usualHighestSlippage < slippage

    override val slippageChangeObservable = PublishSubject.create<Unit>()

    override fun setSlippage(value: BigDecimal) {
        slippage = value
        sync()
        slippageChangeObservable.onNext(Unit)
    }

    private fun getSlippageError(errors: List<Throwable>): Throwable? {
        return errors.firstOrNull {
            it is SwapSettingsError.InvalidSlippage
        }
    }

    init {
        sync()
    }

    private fun sync() {
        val swapSettings = OneInchSwapSettings()

        val errs = mutableListOf<Exception>()

        swapSettings.recipient = recipient
        recipientError?.let {
            errs.add(SwapSettingsError.InvalidAddress)
        }

        when {
            slippage.compareTo(BigDecimal.ZERO) == 0 -> {
                errs.add(SwapSettingsError.ZeroSlippage)
            }
            slippage > limitSlippageBounds.upper -> {
                errs.add(
                    SwapSettingsError.InvalidSlippage(
                        InvalidSlippageType.Higher(limitSlippageBounds.upper)
                    )
                )
            }
            slippage < limitSlippageBounds.lower -> {
                errs.add(
                    SwapSettingsError.InvalidSlippage(
                        InvalidSlippageType.Lower(limitSlippageBounds.lower)
                    )
                )
            }
            else -> {
                swapSettings.slippage = slippage
            }
        }

        errors = errs

        state = if (errs.isEmpty()) {
            State.Valid(swapSettings)
        } else {
            State.Invalid
        }
    }

}
