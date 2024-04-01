package com.example.testblocking

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.Calendar


class IncomingCallReceiver : BroadcastReceiver() {

    private var callStartTime: Long = 0
    private var handler: Handler? = null
    private var mediaPlayer: MediaPlayer? = null
    private val ALERT_SOUND_DELAY = 10000 // 10 seconds

    @SuppressLint("SuspiciousIndentation")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                // Call ringing
            } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                // Call answered, record the start time and start the alert timer
                callStartTime = System.currentTimeMillis()
                startAlertTimer(context)
            } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                // Call ended, stop the timer
                stopAlertTimer()
            }
        }



//            if (state == TelephonyManager.EXTRA_STATE_RINGING && incomingNumber != null) {
//                Log.v("block incoming number", "incoming number: $incomingNumber")
//                endOrBlockCall(context)
//                //   endCall(context)
//                //  cancelCall(context)
//
//                //  showNotification(context)
//                //  if (incomingNumber == "0797734726")
//                if (state == TelephonyManager.EXTRA_STATE_RINGING && isWithinSpecificTimes()) {
//
//                    showNotificationWithSound(context, incomingNumber ?: "Unknown number")
//
//                }
            }



    private fun startAlertTimer(context: Context) {
        handler = Handler()
        handler?.postDelayed({
            // Alert sound after specific duration from the call start
            playAlertSound(context)
        }, ALERT_SOUND_DELAY.toLong())
    }

    private fun stopAlertTimer() {
        handler?.removeCallbacksAndMessages(null)
    }

    private fun playAlertSound(context: Context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.sound2)
        mediaPlayer?.start()
    }





    private fun isWithinSpecificTimes(): Boolean {
        // Define your specific times here
        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10) // Start time: 10 AM
            set(Calendar.MINUTE, 0)
        }
        val endTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18) // End time: 6 PM
            set(Calendar.MINUTE, 0)
        }

        val currentTime = Calendar.getInstance()

        // Check if the current time is within the specific times
        return currentTime in startTime..endTime
    }





    private fun showNotificationWithSound(context: Context, incomingNumber: String) {
        // Play sound
        val mediaPlayer = MediaPlayer.create(context, R.raw.sound2)
        mediaPlayer.start()

        // Build and show notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "IncomingCallNotificationChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Incoming Call Notification Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Incoming Call")
            .setContentText("You have an incoming call from: $incomingNumber")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notification = notificationBuilder.build()
        notificationManager.notify(1, notification)

        // Release the media player after the sound finishes
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
        }
    }









    private fun cancelCall(context: Context) {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val clazz = Class.forName(telephonyManager.javaClass.name)
            val method = clazz.getDeclaredMethod("getITelephony")
            method.isAccessible = true
            val telephonyService = method.invoke(telephonyManager)
            val telephonyServiceClass = Class.forName(telephonyService.javaClass.name)
            val endCallMethod = telephonyServiceClass.getDeclaredMethod("endCall")
            endCallMethod.invoke(telephonyService)
        } catch (e: Exception) {
            showToast(context, "Error canceling call: ${e.message}")
        }
    }
    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }






    private fun endCall(context: Context) {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val telephonyClass = Class.forName(telephonyManager.javaClass.name)
            val method = telephonyClass.getDeclaredMethod("getITelephony")
            method.isAccessible = true
            val telephonyService = method.invoke(telephonyManager) as ITelephony
            telephonyService.endCall()

            // Notify the user about the blocked call
            showNotification(context)
        } catch (e: Exception) {
            Log.e("PhoneCallReceiver", "Error ending call: $e")
        }
    }

    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
            .setSmallIcon(android.R.drawable.ic_notification_clear_all)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1, notificationBuilder.build())
    }
}
