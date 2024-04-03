package com.example.testblocking

import android.content.Context
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.CallScreeningService.CallResponse
import android.net.Uri

class MyCallScreeningService(context: Context) : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = extractPhoneNumber(callDetails.handle)
        val shouldBlockCall = isNumberBlocked(phoneNumber)

        val response = if (shouldBlockCall) {
            CallResponse.Builder().setDisallowCall(true).build()


        } else {
            CallResponse.Builder().setDisallowCall(false).build()
        }

        respondToCall(callDetails, response)
    }

    private fun extractPhoneNumber(handle: Uri): String {
        // The handle Uri typically contains the phone number
        // You need to extract the phone number from the Uri
        // This is a placeholder implementation, replace it with your actual logic
        return handle.schemeSpecificPart
    }

    private fun isNumberBlocked(phoneNumber: String): Boolean {
        // Implement your logic to check if the phoneNumber is in a block list
        // For example, you can check against a list of blocked numbers stored in SharedPreferences or a database
        // This is just a placeholder implementation, replace it with your actual logic
//        val blockedNumbers = listOf("1234567890", "0987654321")
//        return phoneNumber in blockedNumbers
        return true
    }
}


