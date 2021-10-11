package com.example.kotlindemo

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.util.ArrayList

class IntentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intent)
        val textView: TextView = findViewById(R.id.textView)
        val text = "action : ${intent.action}\n" +
                "categories: ${intent.categories}\n" +
                "data: ${intent.data}\n" +
                "extra: ${intent.extras}"
        textView.text = text
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkApplink()
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkApplink() {
        val manager = getSystemService(DomainVerificationManager::class.java)
        val userState: DomainVerificationUserState? = try {
            manager.getDomainVerificationUserState(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            return
        }
        val hostToStateMap = userState!!.hostToStateMap
        val verifiedDomains: MutableList<String> = ArrayList()
        val selectedDomains: MutableList<String> = ArrayList()
        val unapprovedDomains: MutableList<String> = ArrayList()
        for (key in hostToStateMap.keys) {
            val stateValue = hostToStateMap[key]
            if (stateValue == DomainVerificationUserState.DOMAIN_STATE_VERIFIED) {
                // Domain has passed Android App Links verification.
                verifiedDomains.add(key)
            } else if (stateValue == DomainVerificationUserState.DOMAIN_STATE_SELECTED) {
                // Domain hasn't passed Android App Links verification, but the user has
                // associated it with an app.
                selectedDomains.add(key)
            } else {
                // All other domains.
                unapprovedDomains.add(key)
            }
        }
        Log.i("df", "isLinkHandAllowed: " + userState.isLinkHandlingAllowed)
        Log.i("df", "verify:$verifiedDomains")
        Log.i("df", "select:$selectedDomains")
        Log.i("df", "unapproved:$unapprovedDomains")
        if (unapprovedDomains.size > 0) { // 设置crash了
            val intent = Intent(
                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }
    }
}