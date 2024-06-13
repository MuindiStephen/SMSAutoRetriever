package com.example.otpselfretrieverdemo.smsretriever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SmsBroadcastReceiver : BroadcastReceiver() {

    var smsBroadcastReceiverListener: SmsBroadcastReceiverListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras
            val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (smsRetrieverStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                    message?.let {
                        val otpPattern = Pattern.compile("\\d{4}")
                        val matcher = otpPattern.matcher(it)
                        if (matcher.find()) {
                            val otp = matcher.group(0)
                            smsBroadcastReceiverListener?.onSuccess(otp)
                        } else {
                            Log.e("SmsBroadcastReceiver", "OTP pattern not found in the message")
                        }
                    } ?: run {
                        Log.e("SmsBroadcastReceiver", "No SMS message received")
                    }
                }
                CommonStatusCodes.TIMEOUT -> {
                    Log.e("SmsBroadcastReceiver", "OTP retrieval timed out")
                    smsBroadcastReceiverListener?.onFailure()
                }
                else -> {
                    Log.e("SmsBroadcastReceiver", "Unexpected status code: ${smsRetrieverStatus.statusCode}")
                }
            }
        }
    }

    interface SmsBroadcastReceiverListener {
        fun onSuccess(otp: String?)
        fun onFailure()
    }


    // NB::
    // Ensure the SMS contains the app’s hash,
    // which is necessary for the SMS Retriever API to identify that the message is intended for your app.
}
