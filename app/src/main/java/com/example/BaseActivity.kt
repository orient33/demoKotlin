package com.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    fun log(msg: String) {
        Log.i("dff", "$this : $msg")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")
    }

    override fun onStart() {
        log("onStart1")
        super.onStart()
        log("onStart2")
    }

    override fun onResume() {
        log("onResume1")
        super.onResume()
        log("onResume2")
    }

    override fun onPause() {
        log("onPause1")
        super.onPause()
        log("onPause2")
    }

    override fun onStop() {
        log("onStop1")
        super.onStop()
        log("onStop2")
    }

    override fun onDestroy() {
        log("onDestroy1")
        super.onDestroy()
        log("onDestroy2")
    }
}