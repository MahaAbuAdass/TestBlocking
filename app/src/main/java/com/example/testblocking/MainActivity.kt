package com.example.testblocking


import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonToggleDnd: Button = findViewById(R.id.button_toggle_dnd)
        buttonToggleDnd.setOnClickListener {
            toggleDoNotDisturbMode()
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
                // Disable notifications for all channels
                for (channel in notificationManager.notificationChannels) {
                    channel.setImportance(NotificationManager.IMPORTANCE_NONE)
                    notificationManager.createNotificationChannel(channel)
                }
            } else {
                // Enable notifications for all channels
                for (channel in notificationManager.notificationChannels) {
                    channel.setImportance(NotificationManager.IMPORTANCE_DEFAULT)
                    notificationManager.createNotificationChannel(channel)
                }
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
}
