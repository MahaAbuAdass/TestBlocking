package com.example.testblocking

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import java.lang.reflect.Method

class IncomingCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                // Here you can add your logic to block the incomingNumber
                // For example, you can end the call programmatically or perform other actions.
                Log.v("block incoming number" , "incoming number: $incomingNumber")
               endCall(context)
            }
        }
    }

    private fun endCall(context: Context): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                "BlockedCallNotificationChannel",
                "Blocked Call Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, "BlockedCallNotificationChannel")
            .setContentTitle("Call Blocked")
            .setContentText("An incoming call was blocked.")
            .setSmallIcon(androidx.core.R.drawable.notification_bg)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1, notificationBuilder.build())

        return try {
            val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val c = Class.forName(telephony.javaClass.name)
            val m: Method = c.getDeclaredMethod("getITelephony")
            m.isAccessible = true
            val telephonyService = m.invoke(telephony) as ITelephony
            telephonyService.endCall()
            true
        } catch (e: Exception) {
            Log.e("PhoneCallReceiver", "Error ending call: $e")
            false
        }
    }
}
