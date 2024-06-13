package com.example.otpselfretrieverdemo

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
    // var otp: String? = null
    var intentFilter: IntentFilter? =null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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

       //  otp = binding.otpView.text.toString()
        binding.otpView.setText("1234")


        Toast.makeText(this,"Otp is ${binding.otpView.text}",Toast.LENGTH_SHORT).show()


        initSmartSelfRetrieval()

        // Register Broadcast Receiver
        registerBroadCastReceiver()

    }

    private fun initSmartSelfRetrieval() {
        val client = SmsRetriever.getClient(this@MainActivity)
        client.startSmsRetriever()
            .addOnSuccessListener {
                Log.e("Successfully started retriever, expect broadcast intent","")
            }
            .addOnFailureListener {
                Log.e("Failed to start retriever, inspect Exception for more details","")

            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun registerBroadCastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener = object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
            override fun onSuccess(otp: String?) {
                Log.e("MainActivityOTP", "successReceived OTP: $otp")
                startActivityForResult(intent!!, REQ_USER_CONSENT)
              //  val message = data.getStringExtra(SmsRetriever.EXTRA_CONSENT_INTENT)
               // getOtpFromMessage()

                runOnUiThread {
                    binding.otpView.setText(otp)
                    Log.e("OTPView", otp.toString())
                }
            }

            override fun onFailure() {
                Log.e("MainActivityOTP", "failed retrieval")
//                Log.e("", "Try again. Kindly, TimeOut \n" +
//                        "Caused by: ${smsBroadcastReceiver!!.smsBroadcastReceiverListener!!.onFailure()}")
            }
        }

        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver,intentFilter, RECEIVER_NOT_EXPORTED)
    }


    // @Deprecated(replaceWith = )


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT && resultCode == RESULT_OK && data != null) {
            val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            message?.let { getOtpFromMessage(it) }
        }
    }

    private fun getOtpFromMessage(message: String?) {
        val otpPattern = Pattern.compile("\\d{4}")
        val matcher = otpPattern.matcher(message?: "2222")
        if (matcher.find()) {
            val otp = matcher.group(0)
            runOnUiThread {
                binding.otpView.setText(otp)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()
        registerBroadCastReceiver()

       registerReceiver(smsBroadcastReceiver,intentFilter)
        Log.e("MA===", "Init Broadcast receiver")
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
       // Log.e("MA===","${unregisterReceiver(smsBroadcastReceiver)}")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsBroadcastReceiver)
    }

}