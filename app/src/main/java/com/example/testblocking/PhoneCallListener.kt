package com.example.testblocking

import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

class PhoneCallListener(private val context: Context) : PhoneStateListener() {
    private var isPhoneCalling = false

    override fun onCallStateChanged(state: Int, incomingNumber: String) {
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                // Phone ringing
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                // Active
                isPhoneCalling = true
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                // Run when class initial and phone call ended, need to detect flag
                // from CALL_STATE_OFFHOOK
                if (isPhoneCalling) {
                    // Restart app
                    val i = context.packageManager
                        .getLaunchIntentForPackage(context.packageName)
                    i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(i)
                    isPhoneCalling = false
                }
            }
        }
    }
}
