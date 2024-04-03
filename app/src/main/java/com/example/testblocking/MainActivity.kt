package com.example.testblocking

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_PERMISSION_CODE = 101
    }

    private lateinit var buttonCallForwardOn: Button
    private lateinit var buttonCallForwardOff: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonToggleDnd: Button = findViewById(R.id.button_toggle_dnd)
        buttonToggleDnd.setOnClickListener {
            toggleDoNotDisturbMode()
        }




        //forward  + phoneCallListener class
        buttonCallForwardOn = findViewById(R.id.buttonCallForwardOn)
        buttonCallForwardOn.setOnClickListener {
            callForward("*21*0797735165#") // 0123456789 is the number you want to forward the calls.
        }

        buttonCallForwardOff = findViewById(R.id.buttonCallForwardOff)
        buttonCallForwardOff.setOnClickListener {
            callForward("#21#")
        }


        // block

        val blockCallButton: Button = findViewById(R.id.blockCallButton)
        blockCallButton.setOnClickListener {

            blockIncomingCalls()

        }



    }

    private fun toggleDoNotDisturbMode() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!notificationManager.isNotificationPolicyAccessGranted) {
                // Ask the user to grant access to "Do Not Disturb" mode.
                openDndSettings()
                showToast("Please grant access to change Do Not Disturb settings")
                return
            }
        }

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
            setNotificationPolicy(notificationManager, true)
            showToast("Do Not Disturb mode turned ON")
        } else {
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            setNotificationPolicy(notificationManager, false)
            showToast("Do Not Disturb mode turned OFF")
        }
    }

    private fun setNotificationPolicy(notificationManager: NotificationManager, enableDndMode: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (enableDndMode) {
                // Suppress all notifications
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
            } else {
                // Restore interruption filter to allow notifications
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
        }
    }

    private fun openDndSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



    private fun callForward(callForwardString: String) {
        val phoneListener = PhoneCallListener(this)
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)

        val intentCallForward = Intent(Intent.ACTION_CALL)
        val mmiCode = Uri.fromParts("tel", callForwardString, "#")
        intentCallForward.data = mmiCode
        startActivity(intentCallForward)
    }






    // block
    private fun blockIncomingCalls() {
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        // Check if the TelecomManager API is available on the device
        if (telecomManager.defaultDialerPackage != packageName) {
            // Your app is not set as the default phone app
            // Request the user to set it as default if necessary
        } else {
            // Block incoming calls
            val phoneAccountHandle = PhoneAccountHandle(
                ComponentName(applicationContext, MainActivity::class.java),
                "your_unique_id_here"
            )
            telecomManager.addNewIncomingCall(phoneAccountHandle, null)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            // Check if permissions are granted
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                blockIncomingCalls()
            } else {
                // Handle permission denied
            }
        }
    }



}
