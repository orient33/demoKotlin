package com.example.screenrecoder

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.os.Environment
import com.example.formatTime
import com.example.formatTimeNow
import com.example.log
import java.io.File
import java.io.FileOutputStream

/**
 * 使用MediaRecorder实现 录制屏幕.(包含声音Audio)
 * 参考android CTS  : AOSP/cts/tests/tests/media/src/android/media/cts/EncoderXX.java
 * https://developer.android.google.cn/reference/android/media/MediaCodec
 */
@TargetApi(21)
class RecorderMediaRecorder(
    private val context: Context,
    private val mediaProjection: MediaProjection,
    private val listener: IRecorder.IMessageInfo
) : ImageReader.OnImageAvailableListener, IRecorder {
    private val mMediaRecorder = MediaRecorder()
    private var mFilePath = ""
    private var mRecording = false
    private val mMpCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            log("MediaProjection.Callback.onStop()!!")
        }
    }

    //可配置 分辨率,画质(比特率),帧数,
    override fun startVideo(): Boolean {
        val dm = context.resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        try {
            configMediaRecorder(width, height)
        } catch (e: Exception) {
            listener.onMessage("prepare. fail $e")
            return false
        }
        mediaProjection.createVirtualDisplay(
            "video.", width, height, dm.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            mMediaRecorder.surface, null, null
        )
        mediaProjection.registerCallback(mMpCallback, null)
        log("create virtual display.")
        mMediaRecorder.start()
        log("start media recorder!")
        mRecording = true
        return true
    }

    private fun configMediaRecorder(w: Int, h: Int) {
        //注意Android Q上的sdcard权限.
        val dir = context.getExternalFilesDir(null)//Environment.DIRECTORY_MOVIES)
        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val path = dir!!.path + File.separator + "video-${formatTimeNow()}.mp4"
        mFilePath = path
        log("config. video save path $path")
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)

        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)

        mMediaRecorder.setVideoSize(w, h)
        mMediaRecorder.setVideoFrameRate(30)

//        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P))
        mMediaRecorder.setOutputFile(path)
        mMediaRecorder.setVideoEncodingBitRate(6 * 1000 * 1000)//这个决定清晰度!!
//            int rotation = getWindowManager().getDefaultDisplay().getRotation();
//            int orientation = ORIENTATIONS.get(rotation + 90);
//            mMediaRecorder.setOrientationHint(orientation);
        try {
            mMediaRecorder.prepare()
        } catch (e: Exception) {
            if (h * 1f / w > 16f / 9) { //这样会丢失底部的区域..
                val fixH = (16f / 9 * w).toInt()
                listener.onMessage("$w x $h prepare fail. use $w x $fixH")
                mMediaRecorder.reset()
                configMediaRecorder(w, fixH)
            }
        }
    }

    //截屏!
    override fun startImage() {
        val dm = context.resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        val dpi = dm.densityDpi
        val imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 2)
        mediaProjection.createVirtualDisplay(
            "screenRecorder", width, height, dpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            imageReader.surface, null, null
        )
        imageReader.setOnImageAvailableListener(this, null)
    }

    override fun onImageAvailable(reader: ImageReader) {
        val image = reader.acquireLatestImage()
        if (image != null) {
            val p = image.planes
            if (p != null && p.isNotEmpty()) {
                val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
                bitmap.copyPixelsFromBuffer(p[0].buffer)
                val dir = context.filesDir
                val file = File(dir, "image-" + formatTime(System.currentTimeMillis()) + ".jpg")
                val os = FileOutputStream(file)
                val r = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                bitmap.recycle()
                log("save bitmap $r :" + file.path)
                listener.onMessage("save Image: ${file.path}")
                mediaProjection.stop()
            }
        }
    }

    override fun stop(): Boolean {
        if (!mRecording) return false
        mRecording = false
        listener.onMessage("File: $mFilePath")
        mediaProjection.unregisterCallback(mMpCallback)
        mediaProjection.stop()
        try {
            mMediaRecorder.stop()
            log("stop MediaProjection. stop media recorder!")
        } catch (e: Exception) {
            log("stop MediaRecorder Fail!$e")
        }
        return true
    }

    override fun name(): String {
        return "MediaRecorder"
    }
}