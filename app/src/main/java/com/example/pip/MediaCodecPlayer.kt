package com.example.pip

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.SurfaceView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.nio.ByteBuffer
import java.util.*

//MediaCodec播放video示例 (暂时不含audio)
class MediaCodecPlayer(val view: SurfaceView) : DefaultLifecycleObserver, Handler.Callback {
    private val extractor = MediaExtractor()
    private val handler = Handler(Looper.getMainLooper(), this)
    private var mCodec: MediaCodec? = null
    private var format: MediaFormat? = null
    private var frameInterval = -1

    fun play(uri: String) {
        logg("setDataSource. $uri")
        extractor.setDataSource(uri)
//        var videoIndex = -1
        var videoFormat: MediaFormat? = null
//        var audioIndex = -1
//        var audioFormat: MediaFormat? = null
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            logg("find $i track format. $format")
            val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
            if (mime.startsWith("video/")) {
//                videoIndex = i
                videoFormat = format
                extractor.selectTrack(i)
//            } else if (mime.startsWith("audio/")) {
//                audioIndex = i
//                audioFormat = format
            }
        }
        if (videoFormat == null) {
            logg("has no video track. $uri")
            return
        }
        format = videoFormat
        val frameRate = videoFormat.getInteger(MediaFormat.KEY_FRAME_RATE)
        val count = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
        frameInterval = 1000 / frameRate
        val mediaCodec = //PUtil.createCodec(videoFormat)
            MediaCodec.createDecoderByType(videoFormat.getString(MediaFormat.KEY_MIME)!!)
        logg("createDecoder. $mediaCodec , ${mediaCodec.name} , frameRate= $frameRate")
        mediaCodec.setCallback(callback)
        mediaCodec.configure(videoFormat, view.holder.surface, null, 0)
        logg("configure done. rate=$frameRate,, count = $count")
        mCodec = mediaCodec

        start()
    }

    fun start() {
        val mediaCodec = mCodec
        if (mediaCodec != null) {
            mediaCodec.start()
            logg("start.......")
        }
    }

    private var lastRenderTimeMs = -1L
    private fun setSampleData() {
        val index: Int? = mAvailableInputBufferIndexes.peek()
        if (index == null || mAvailableInputBufferIndexes.isEmpty()) {
            logg("input buffer empty!")
        } else {
            val buffer: ByteBuffer? = mCodec!!.getInputBuffer(index)
            if (buffer == null) {
                logg("input buffer. null. index = $index")
                return
            }
            val size: Int = extractor.readSampleData(buffer, 0)
            val time: Long = extractor.sampleTime
            if (time < 0) {
                logg("input buffer $buffer. getSampleData ,time<0  $time, size = $size")
                //state to end..
                return
            }
            val diffTimeMs = System.currentTimeMillis() - lastRenderTimeMs
            if (lastRenderTimeMs > 0 && shouldNotRender(diffTimeMs)) {
                //如果这帧不到时间 那么延迟下 所以有上面的 peek,当渲染后再remove
                handler.removeMessages(1)
                handler.sendEmptyMessageDelayed(1, diffTimeMs)
                return
            }
            mAvailableInputBufferIndexes.remove()
            lastRenderTimeMs = System.currentTimeMillis()
            val flags: Int = extractor.sampleFlags
            mCodec!!.queueInputBuffer(index, 0, size, time, flags) //渲染一帧
            count++
            val nn = extractor.advance()
            logg("queueInputBuffer setSampleData. $index,$size Byte, ${time / 1000} ms, renderTimeMs $lastRenderTimeMs, frameCount $count, advance=$nn")
        }
    }

    var count = 0//count 渲染完成后 frame-count=224, debug用,没有实际意义

    //是否应该渲染 根据帧的间隔ms计算
    private fun shouldNotRender(diffTimeMs: Long): Boolean {
//        logg("diff render time $diffTimeMs , frameInterval $frameInterval")
        return diffTimeMs < frameInterval
//        return diffSampleTimeUs / 1000 < diffTimeMs // * 1000
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mAvailableInputBufferIndexes.clear()
        mCodec?.release()
    }

    private fun logg(msg: String) {
        Log.i("df", "MediaCodecPlayer.$msg")
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            1 -> setSampleData()
        }
        return true
    }

    private val mAvailableInputBufferIndexes: Queue<Int> = ArrayDeque()
//    private val mAvailableOutputBufferIndexes: Queue<Int> = ArrayDeque()
//    private val mAvailableOutputBufferInfo: Queue<MediaCodec.BufferInfo> = ArrayDeque()

    val callback = object : MediaCodec.Callback() {
        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
//            logg("onInputBuffer. $index")
            mAvailableInputBufferIndexes.add(index)
            setSampleData()
        }

        override fun onOutputBufferAvailable(
            codec: MediaCodec,
            index: Int,
            info: MediaCodec.BufferInfo
        ) {
//            mAvailableOutputBufferIndexes.add(index)
            codec.releaseOutputBuffer(index, true)
//            logg("onOutputBuffer. $index, $info")
        }

        override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
            logg("onError. $e")
        }

        override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
            logg("onOutputFormat. $format")
        }
    }
}