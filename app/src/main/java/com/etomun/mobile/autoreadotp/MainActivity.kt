package com.etomun.mobile.autoreadotp

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.etomun.mobile.autoreadotp.SmsBroadcastReceiver.SmsBroadcastReceiverListener
import com.etomun.mobile.autoreadotp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.phone.SmsRetriever
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {
    private val REQ_USER_CONSENT = 999
    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startSmsUserConsent()
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == RESULT_OK && data != null) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                getOtpFromMessage(message.orEmpty())
            }
        }
    }

    private fun startSmsUserConsent() {
        val client = SmsRetriever.getClient(this)
        //We can add sender phone number or leave it blank (null)
        client.startSmsUserConsent(null).addOnSuccessListener {
            Toast.makeText(applicationContext, "On Success", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "On OnFailure", Toast.LENGTH_LONG).show()
        }
    }

    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver.smsBroadcastReceiverListener = object : SmsBroadcastReceiverListener {
            override fun onSuccess(intent: Intent?) {
                startActivityForResult(intent, REQ_USER_CONSENT)
            }

            override fun onFailure() {}
        }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    private fun getOtpFromMessage(message: String) {
        // This will match any 6 digit number in the message
        val pattern = Pattern.compile("(|^)\\d{6}")
        val matcher = pattern.matcher(message)
        if (matcher.find()) {
            binding.otp.setText(matcher.group(0))
        }
    }
}