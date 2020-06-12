package com.example.screenrecoder

interface IRecorderCallback {
    /**
     * @param recording 当前是否正在录屏
     * @param mediaCodec 是否在使用MediaCodec录屏
     */
    fun onStateChange(recording: Boolean, mediaCodec: Boolean)

    /**
     * @param msg 信息..用于显示/debug
     */
    fun onMessageInfo(msg: String)
}