package com.example.screenrecoder

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.kotlindemo.MainActivity
import com.example.kotlindemo.R

const val ACTION_START = "action.start"
const val EXTRA_CODE = "extra.code"
const val EXTRA_DATA = "extra.data"
const val EXTRA_CODEC = "extra.useMediaCodec"
const val n_id = 1
const val n_channel = "d"

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class RecorderService : Service(), IRecorder.IMessageInfo {
    lateinit var mNotification: Notification
    private var mRecorder: IRecorder? = null
    override fun onCreate() {
        super.onCreate()
        mNotification = NotificationCompat.Builder(this, n_channel)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setChannelId(n_channel)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.screen_recorder))
            .setContentIntent(
                PendingIntent.getActivity(
                    this, 1,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                n_channel, getString(R.string.screen_recorder),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nm.createNotificationChannel(channel)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    n_id, mNotification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                )
            } else {
                startForeground(n_id, mNotification)
            }
        } else {
            nm.notify(n_id, mNotification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (ACTION_START == intent.action) {
                val code = intent.getIntExtra(EXTRA_CODE, -111)
                val data = intent.getParcelableExtra<Intent>(EXTRA_DATA)
                val codec = intent.getBooleanExtra(EXTRA_CODEC, true)
                if (data != null && code != -111) {
                    val r = start(codec, code, data)
                    if (!r) stopSelf()
                    setState(r)
                    updateMessage("${mRecorder!!.name()} ${if (r) "recording" else "init fail"} ,")
                } else {
                    updateMessage("code or data 无效!无法录屏.")
                    stopSelf()
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onDestroy() {
        stopRecording()
        super.onDestroy()
    }

    override fun onMessage(msg: String) {
        updateMessage(msg)
    }

    private fun start(codec: Boolean, resultCode: Int, data: Intent): Boolean {
        val pm = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val mp = pm.getMediaProjection(resultCode, data)
        mRecorder = if (codec) RecorderMediaCodec(this, mp, this)
            else RecorderMediaRecorder(this, mp, this)
        return mRecorder!!.startVideo()
    }

    private var recording = false
    private var message = ""

    private var callback: IRecorderCallback? = null

    fun stopRecording() {
        if (mRecorder != null && mRecorder!!.stop()) {
            updateMessage("complete!")
            setState(false)
        }
    }

    fun setListener(cb: IRecorderCallback?) {
        callback = cb
        if(cb == null) return
        if (recording) cb.onStateChange(recording, mRecorder is RecorderMediaCodec)
        if (message.isNotEmpty()) cb.onMessageInfo(message)
    }

    private fun setState(record: Boolean) {
        if (record != recording) {
            recording = record
            callback?.onStateChange(record, mRecorder is RecorderMediaCodec)
        }
    }

    private fun updateMessage(msg: String?) {
        if (msg == null) message = ""
        else message += msg + "\n"

        callback?.onMessageInfo(message)
    }

    inner class LocalBinder : Binder() {
        fun getService(): RecorderService = this@RecorderService
    }

    private val binder = LocalBinder()
}
