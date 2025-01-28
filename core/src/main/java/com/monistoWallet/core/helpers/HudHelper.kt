package com.monistoWallet.core.helpers

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import com.monistoWallet.core.CustomSnackbar
import com.monistoWallet.core.R
import com.monistoWallet.core.SnackbarDuration
import com.monistoWallet.core.SnackbarGravity

object HudHelper {

    fun showInProcessMessage(
        contenView: View,
        resId: Int,
        duration: SnackbarDuration = SnackbarDuration.SHORT,
        gravity: SnackbarGravity = SnackbarGravity.BOTTOM,
        showProgressBar: Boolean = true
    ): CustomSnackbar? {
        return showHudNotification(
            contentView = contenView,
            text = contenView.context.getString(resId),
            backgroundColor = R.color.grey,
            duration = duration,
            gravity = gravity,
            showProgressBar = showProgressBar
        )
    }

    fun showSuccessMessage(
        contenView: View,
        resId: Int,
        duration: SnackbarDuration = SnackbarDuration.SHORT,
        gravity: SnackbarGravity = SnackbarGravity.BOTTOM,
        @DrawableRes icon: Int? = null,
        iconTint: Int? = null,
    ): CustomSnackbar? {
        return showHudNotification(
            contentView = contenView,
            text = contenView.context.getString(resId),
            backgroundColor = R.color.green_d,
            duration = duration,
            gravity = gravity,
            icon = icon,
            iconTint = iconTint
        )
    }

    fun showSuccessMessage(
        contenView: View,
        text: String,
        duration: SnackbarDuration = SnackbarDuration.SHORT,
        gravity: SnackbarGravity = SnackbarGravity.BOTTOM
    ): CustomSnackbar? {
        return showHudNotification(
            contentView = contenView,
            text = text,
            backgroundColor = R.color.green_d,
            duration = duration,
            gravity = gravity,
        )
    }

    fun showErrorMessage(
        contenView: View,
        textRes: Int,
        gravity: SnackbarGravity = SnackbarGravity.BOTTOM
    ) {
        showErrorMessage(contenView, contenView.context.getString(textRes), gravity)
    }

    fun showErrorMessage(
        contenView: View,
        text: String,
        gravity: SnackbarGravity = SnackbarGravity.BOTTOM
    ): CustomSnackbar? {
        return showHudNotification(
            contentView = contenView,
            text = text,
            backgroundColor = R.color.red_d,
            duration = SnackbarDuration.LONG,
            gravity = gravity,
        )
    }
    fun show0xErrorMessage(
        contentView: View,
        headerText: String,
        mainText: String,
    ) {
        // Создаем кастомный макет для Toast
        val inflater = LayoutInflater.from(contentView.context)
        val layout = inflater.inflate(R.layout.error_snackbar, null)

        val headerTextView = layout.findViewById<TextView>(R.id.headerText)
        val mainTextView = layout.findViewById<TextView>(R.id.mainText)

        headerTextView.text = headerText
        mainTextView.text = mainText

        layout.alpha = 0f
        layout.animate().alpha(1f).setDuration(300).start()

        val toast = Toast(contentView.context)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout

        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()

        layout.postDelayed({
            layout.animate().alpha(0f).setDuration(300).withEndAction {
                toast.cancel()
            }.start()
        }, 2000)
    }

    fun showErrorMessage(
        contenView: View,
        resId: Int,
        duration: SnackbarDuration = SnackbarDuration.SHORT,
        gravity: SnackbarGravity = SnackbarGravity.BOTTOM,
        @DrawableRes icon: Int? = null,
        iconTint: Int? = null,
    ): CustomSnackbar? {
        return showHudNotification(
            contentView = contenView,
            text = contenView.context.getString(resId),
            backgroundColor = R.color.red_d,
            duration = duration,
            gravity = gravity,
            icon = icon,
            iconTint = iconTint
        )
    }

    fun showWarningMessage(
        contentView: View,
        resId: Int,
        duration: SnackbarDuration = SnackbarDuration.SHORT,
        gravity: SnackbarGravity = SnackbarGravity.BOTTOM
    ): CustomSnackbar? {
        return showHudNotification(
            contentView = contentView,
            text = contentView.context.getString(resId),
            backgroundColor = R.color.grey,
            duration = duration,
            gravity = gravity,
            icon = R.drawable.ic_attention_24,
            iconTint = R.color.jacob
        )
    }

    fun vibrate(context: Context) {
        val vibratorService = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        val vibrationEffect = VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)

        vibratorService?.vibrate(vibrationEffect)
    }

    @Suppress("DEPRECATION")
    fun Context.vibrator(vibrationWavePattern: LongArray) {
//        vibrationWavePattern = longArrayOf(0, 10, 200, 500, 700, 1000, 300, 200, 50, 10)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                // For Android 12 (S) and above
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrationEffect = VibrationEffect.createWaveform(vibrationWavePattern, -1)
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(vibrationEffect)
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
                // For Android 8.0 (Oreo) to Android 11 (R)
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                val vibrationEffect = VibrationEffect.createWaveform(vibrationWavePattern, -1)
                vibrator.vibrate(vibrationEffect)
            }

            else -> {
                // For Android versions below Oreo (API level 26)
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(50)
            }
        }
    }

    private fun showHudNotification(
        contentView: View,
        text: String,
        backgroundColor: Int,
        duration: SnackbarDuration,
        gravity: SnackbarGravity,
        showProgressBar: Boolean = false,
        @DrawableRes icon: Int? = null,
        iconTint: Int? = null,
    ): CustomSnackbar? {

        val snackbar = CustomSnackbar.make(
            contentView,
            text,
            backgroundColor,
            duration,
            gravity,
            showProgressBar,
            icon,
            iconTint
        )
        snackbar?.show()

        return snackbar
    }

    fun show0xSuccessMessage(view: View, message: String) {
        val inflater = LayoutInflater.from(view.context)
        val layout = inflater.inflate(R.layout.error_snackbar, null)

        val imageView = layout.findViewById<ImageView>(R.id.imageView)
        val headerText = layout.findViewById<TextView>(R.id.headerText)
        val mainTextView = layout.findViewById<TextView>(R.id.mainText)

        imageView.setImageResource(R.drawable.ic_done)
        headerText.text = ""
        mainTextView.text = message

        layout.alpha = 0f
        layout.animate().alpha(1f).setDuration(300).start()

        val toast = Toast(view.context)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout

        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()

        layout.postDelayed({
            layout.animate().alpha(0f).setDuration(300).withEndAction {
                toast.cancel()
            }.start()
        }, 2000)
    }
}
