package com.etomun.mobile.autoreadotp

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.etomun.mobile.autoreadotp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.phone.SmsRetriever


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var intentFilter: IntentFilter? = null
    private var smsReceiver: SMSReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appSignatureHelper = AppSignatureHashHelper(this)
        val sig = appSignatureHelper.appSignatures[0]
        Log.e("SMS", "Hash --> $sig")

        initSmsListener()
        initBroadCast()
    }

    private fun initSmsListener() {
        val client = SmsRetriever.getClient(this)
        client.startSmsRetriever()
    }

    private fun initBroadCast() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsReceiver = SMSReceiver()
        smsReceiver?.setOTPListener(object : SMSReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String?) {
                showToast("OTP Received: $otp")
                Log.e("SMS", "OTP received: $otp")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(smsReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(smsReceiver)
    }

    private fun showToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}