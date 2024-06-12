package com.example.otpselfretrieverdemo

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.chaos.view.PinView
import com.example.otpselfretrieverdemo.databinding.ActivityMainBinding
import com.example.otpselfretrieverdemo.smsretriever.SmsBroadcastReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val REQ_USER_CONSENT = 200
    var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    var otp: String? = null
    var intentFilter: IntentFilter? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        otp = binding.otpView.text.toString()

        Toast.makeText(this,"Otp is $otp",Toast.LENGTH_SHORT).show()

       // Allow consent from the user
        initSmartUserConsent()

        // Register Broadcast Receiver
        registerBroadCastReceiver()

    }

    private fun initSmartUserConsent() {
        val client = SmsRetriever.getClient(this)
        client.startSmsUserConsent(null)
    }

    private fun registerBroadCastReceiver() {


        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener = object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
            override fun onSuccess(intent: Intent?) {
                Log.e("MainActivityOTP", "successReceived OTP: ${otp!!.toString()}")
                startActivityForResult(intent!!, REQ_USER_CONSENT)
              //  val message = data.getStringExtra(SmsRetriever.EXTRA_CONSENT_INTENT)
               // getOtpFromMessage()
            }

            override fun onFailure() {
                Log.e("MainActivityOTP", "failed retrieval")
//                Log.e("", "Try again. Kindly, TimeOut \n" +
//                        "Caused by: ${smsBroadcastReceiver!!.smsBroadcastReceiverListener!!.onFailure()}")
            }
        }

       intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver,intentFilter)
    }


    // @Deprecated(replaceWith = )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK && data != null) {
            val message = data.getStringExtra(SmsRetriever.EXTRA_CONSENT_INTENT)
            getOtpFromMessage(message)
        }

//        if (requestCode == REQ_USER_CONSENT) {
//
//
//
//        }
    }

    private fun getOtpFromMessage(message: String?) {

        Log.e("OTP is::", message.toString())

//        val otpFormatter = Pattern.compile("\\d{4}")   // ("\\b\\d{4}\\b")

        val otpFormatter = Pattern.compile("\\d{4}")  // "7432" "4581" "2538"

        val matcher = message?.let { otpFormatter.matcher(it) }

        if (matcher != null) {
            if (matcher.find()) {
                runOnUiThread {
                    binding.otpView.setText(matcher.group(0))
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        registerBroadCastReceiver()
       // registerReceiver(smsBroadcastReceiver,intentFilter)
        Log.e("MA===", "Init Broadcast receiver")
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
       // Log.e("MA===","${unregisterReceiver(smsBroadcastReceiver)}")
    }

}