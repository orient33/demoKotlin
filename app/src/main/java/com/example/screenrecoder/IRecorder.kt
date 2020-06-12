package com.example.screenrecoder

interface IRecorder {
    fun name(): String
    fun startVideo(): Boolean
    fun stop(): Boolean
    fun startImage()
    interface IMessageInfo {
        fun onMessage(msg: String)
    }
}