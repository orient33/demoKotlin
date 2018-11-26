package com.example

import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * @author dundongfang on 2018/9/25.
 */

fun log(msg: String, tag: String = "df") {
    Log.d(tag, msg)
}

fun toast(context: Context, msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, msg, duration).show()
}