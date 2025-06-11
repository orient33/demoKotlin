package com.example.screenrecoder

import android.util.Log

interface IRecorder {
    fun name(): String
    fun startVideo(config: RecordConfig): Boolean
    fun stop(): Boolean
    fun startImage()
    interface IMessageInfo {
        fun onMessage(msg: String)
        fun stopBySystem()
    }
}

data class RecordConfig(
    val size: Pair<Int, Int>, //宽高
    val rate: Int,      //帧率 24, 30, 48, 60
    val bitRate: Int,   //比特率
)

val default = RecordConfig(Pair(-1, -1), 30, 6)

fun generateConfig(a: Any, b: Any, c: Any): RecordConfig {
    val wh = a.toString().split("*")
    val size = if (wh.size == 2) {
        Pair(parseAny(wh[0], -1), parseAny(wh[1], -1))
    } else {
        default.size
    }
    val r = parseAny(b, default.rate)
    val br = parseAny(c, default.bitRate)
    return RecordConfig(size, r, br)
}

fun parseAny(a: Any, def: Int): Int {
    try {
        return Integer.valueOf(a.toString())
    } catch (e: Exception) {
        Log.d(TAG, "generateConfig: fail, $a")
    }
    return def
}