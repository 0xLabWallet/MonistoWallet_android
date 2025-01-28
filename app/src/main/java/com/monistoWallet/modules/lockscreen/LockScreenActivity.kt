package com.monistoWallet.modules.lockscreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import com.monistoWallet.core.BaseActivity
import com.monistoWallet.modules.pin.ui.PinUnlock
import com.monistoWallet.ui.compose.ComposeAppTheme

class LockScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeAppTheme {
                PinUnlock(
                    onSuccess = {
                        setResult(RESULT_OK)
                        finish()
                    }
                )
            }
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, LockScreenActivity::class.java)
            context.startActivity(intent)
        }
    }
}
