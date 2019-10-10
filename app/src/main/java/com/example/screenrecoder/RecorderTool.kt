package com.example.screenrecoder

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.os.Environment
import android.support.annotation.Keep
import com.example.formatTime
import com.example.formatTimeNow
import com.example.log
import java.io.File
import java.io.FileOutputStream

//这里只是demo. 最好使用service + notification提示录屏中,且可停止.
@TargetApi(21)
class RecorderTool(
    private val context: Context,
    private val mediaProjection: MediaProjection
) : ImageReader.OnImageAvailableListener {
    private val mMediaRecorder = MediaRecorder()
    var mVirtualDisplay: VirtualDisplay? = null
    var mRecording = false
    private val mMpCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            log("MediaProjection.Callback.onStop()!!")
        }
    }

    //可配置 分辨率,画质(比特率),帧数,
    fun startVideo() {
        val dm = context.resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        configMediaRecorder(width, height)
        mVirtualDisplay = mediaProjection.createVirtualDisplay(
            "video.", width, height, dm.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            mMediaRecorder.surface, null, null
        )
        mediaProjection.registerCallback(mMpCallback, null)
        log("create virtual display.")
        mMediaRecorder.start()
        log("start media recorder!")
        mRecording = true
    }

    private fun configMediaRecorder(w: Int, h: Int) {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val path = dir.path + File.separator + "video-${formatTimeNow()}.mp4"
        log("config. video save path $path")
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)

        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP)

        mMediaRecorder.setVideoSize(w, h)
        mMediaRecorder.setVideoFrameRate(30)

//        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P))
        mMediaRecorder.setOutputFile(path)
        mMediaRecorder.setVideoEncodingBitRate(6 * 1000 * 1000)//这个决定清晰度!!
//            int rotation = getWindowManager().getDefaultDisplay().getRotation();
//            int orientation = ORIENTATIONS.get(rotation + 90);
//            mMediaRecorder.setOrientationHint(orientation);
        mMediaRecorder.prepare()
    }

    @Keep
    fun startImage() {
        val dm = context.resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        val dpi = dm.densityDpi
        val imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
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
                val dir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val file = File(dir, "image-" + formatTime(System.currentTimeMillis()) + ".jpg")
                val os = FileOutputStream(file)
                val r = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                log("save bitmap $r :" + file.path)
                bitmap.recycle()
                stop()
            }
        }
    }

    fun stop() {
        if (!mRecording) return
        mediaProjection.unregisterCallback(mMpCallback)
        mediaProjection.stop()
        mMediaRecorder.stop()
        log("stop MediaProjection. stop media recorder!")
    }
}