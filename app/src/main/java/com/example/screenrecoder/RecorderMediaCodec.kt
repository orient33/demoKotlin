package com.example.screenrecoder

import android.content.Context
import android.hardware.display.DisplayManager
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.media.projection.MediaProjection
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.example.formatTimeNow
import com.example.isMainThread
import com.example.log
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * 使用MediaCodec编码器实现 录制屏幕.(不包含声音Audio)
 * 参考android CTS  : AOSP/cts/tests/tests/media/src/android/media/cts/EncoderXX.java
 * https://developer.android.google.cn/reference/android/media/MediaCodec
 */
const val TAG = "RecorderMediaCodec"

class RecorderMediaCodec(
    private val context: Context,
    private val mediaProjection: MediaProjection,
    private val listener: IRecorder.IMessageInfo
) : IRecorder, MediaCodec.Callback() {
    private var mFormat: MediaFormat? = null
    private var mEncoder: MediaCodec? = null
    private lateinit var mHandler: Handler
    private var mDoing = false
    private val mMediaProjectionCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            log("RecorderMediaCodec. MediaProjection.Callback.onStop()!!")
            stopEncoder()
        }
    }
    override fun startVideo(config: RecordConfig): Boolean {
        if (mDoing) return false
        val dm = context.resources.displayMetrics
        val r = findEncoderParam(dm, config) ?: return false
        val width = ENCODER_PARAM_TABLE[r.first][1]
        val height = ENCODER_PARAM_TABLE[r.first][0]
        val encoder = MediaCodec.createByCodecName(r.second)
        val ht = HandlerThread("encoder.callback")
        ht.start()
        mHandler = Handler(ht.looper)
        encoder.setCallback(this, mHandler)
        try {
            encoder.configure(
                mFormat!!,
                null,
                null,
                MediaCodec.CONFIGURE_FLAG_ENCODE
            )
            val outputSurface = encoder.createInputSurface()
            mediaProjection.registerCallback(mMediaProjectionCallback, null)
            mediaProjection.createVirtualDisplay(
                "encoder.2", width, height, dm.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                outputSurface, null, null
            )
        } catch (e: Exception) {
            Log.w(TAG, "$r, Encoder.configure fail", e)
            return false
        }
        encoder.start()
        mEncoder = encoder
        mDoing = true
        return true
    }

    override fun stop(): Boolean {
        if (!mDoing) return false
        mDoing = false
        mHandler.post {
            appendBuffer(null)
        }
        stopEncoder()
        mediaProjection.unregisterCallback(mMediaProjectionCallback)
        mediaProjection.stop()
        return true
    }
    private fun stopEncoder(){
        listener.onMessage("File: $filePath")
        mEncoder?.stop()
        mEncoder?.release()
    }

    override fun startImage() {
        listener.onMessage("MediaCodec not support! choose MediaRecorder(actually use ImageReader!).")
    }

    override fun name(): String {
        return "MediaCodec"
    }

    private fun findEncoderParam(dm: DisplayMetrics, c: RecordConfig): Pair<Int, String>? {
        ENCODER_PARAM_TABLE[0] = intArrayOf(
            if (c.size.second > 0) c.size.second else dm.heightPixels,
            if (c.size.first > 0) c.size.first else dm.widthPixels,
            c.bitRate * 1000000,
            c.rate,
        )
        for (i in ENCODER_PARAM_TABLE.indices) {
            // Check if we can support it?
            val mcl = MediaCodecList(MediaCodecList.REGULAR_CODECS)
            mFormat = MediaFormat.createVideoFormat(
                MediaFormat.MIMETYPE_VIDEO_AVC, ENCODER_PARAM_TABLE[i][1], ENCODER_PARAM_TABLE[i][0]
            )
            mFormat!!.setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface //注意此参数!
            )
            mFormat!!.setInteger(MediaFormat.KEY_CAPTURE_RATE, ENCODER_PARAM_TABLE[i][3])
            mFormat!!.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 0)
            mFormat!!.setInteger(MediaFormat.KEY_BIT_RATE, ENCODER_PARAM_TABLE[i][2])
            mFormat!!.setInteger(MediaFormat.KEY_FRAME_RATE, ENCODER_PARAM_TABLE[i][3])
            val name = mcl.findEncoderForFormat(mFormat)
            if (name != null) {
                listener.onMessage("use support: $mFormat")
                return Pair(i, name)
            } else {
                Log.d(TAG, "$i ,not support. encoder parameters : $mFormat")
            }
        }
        return null
    }

    var filePath = ""
    var fileChannel: FileChannel? = null

    @WorkerThread
    fun appendBuffer(data: ByteBuffer?) {
        if (isMainThread()) throw IllegalThreadStateException("write File must in worker thread!")
        if (data == null) {
            fileChannel?.close()
            return
        }
        if (fileChannel == null) {
            val destFile =
                File(context.getExternalFilesDir(null), "MediaCodec-${formatTimeNow()}.mp4")
            fileChannel = FileOutputStream(destFile).channel
            filePath = destFile.path
        }
        try {
            fileChannel!!.write(data)
        } catch (e: Exception) {
            log("write fail. $e")
        }
    }

    //implement MediaCodec.Callback.
    override fun onOutputBufferAvailable(
        codec: MediaCodec,
        index: Int,
        info: MediaCodec.BufferInfo
    ) {
        val buffer = codec.getOutputBuffer(index)
        val format = codec.getOutputFormat(index)
//        Log.d(TAG, "onOutputBufferAvailable. $info\n $buffer")
        if (buffer != null) {
            appendBuffer(buffer)
        }
        try {
            codec.releaseOutputBuffer(index, false)
        } catch (e:Exception) {
            Log.w(TAG, "onOutputBufferAvailable. $e")
        }
    }

    override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
        Log.d(TAG, "onInputBufferAvailable. $codec")
    }

    override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
        Log.d(TAG, "onOutputFormatChanged $format")
        for (key in format.keys) {
            if (MediaFormat.TYPE_INTEGER == mFormat!!.getValueTypeForKey(key)) {
                val a = mFormat!!.getInteger(key)
                val b = format.getInteger(key)
                if (a != b) {
                    Log.w(TAG, "format not same. $key : $a != $b")
                    listener.onMessage("format, $key : $a != $b not same with request!")
                }
            }
        }

    }

    override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
        Log.e(TAG, "onError. $e")
        listener.onMessage("onError, $e")
    }
}