package com.example.screenrecoder

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Configuration
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.kotlindemo.MainActivity
import com.example.kotlindemo.R

const val n_id = 1
const val n_channel = "d"

/**
 * 参考下面的, 使得Service在bindService时,无需notification,,unbind后,若在录屏才开始startForeground.
 * 而且 无需 context.startForegroundService()
 * https://github.com/android/location-samples/blob/master/LocationUpdatesForegroundService
*/
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class RecorderService : Service(), IRecorder.IMessageInfo {
    private val EXTRA_FROM = "from.notification"
    lateinit var mNotification: Notification
    private var mRecorder: IRecorder? = null
    private var mConfigChanged = false

    override fun onCreate() {
        super.onCreate()
        val start = PendingIntent.getActivity(
            this, 1, Intent(this, MainActivity::class.java)
            , PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stop = PendingIntent.getService(this, 1,
            Intent(this, RecorderService::class.java).apply {
                putExtra(EXTRA_FROM, true)
            }
            , PendingIntent.FLAG_UPDATE_CURRENT)
        mNotification = NotificationCompat.Builder(this, n_channel)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setChannelId(n_channel)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.screen_recorder))
            .addAction(
                R.drawable.ic_launcher_foreground,
                getString(R.string.show_recorder_page),
                start
            )
            .addAction(android.R.drawable.ic_delete, getString(R.string.stop_recorder), stop)
            .setContentIntent(
                PendingIntent.getActivity(
                    this, 1,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()
    }

    private fun showNotification() {
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
            val cancel = intent.getBooleanExtra(EXTRA_FROM, false)
            if (cancel) {
                stopRecording()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        onRebind(intent)
        return binder
    }

    override fun onRebind(intent: Intent?) {
        mConfigChanged = false
        stopForeground(true)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        mConfigChanged = true
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (!mConfigChanged && mRecorder != null) {
            showNotification()// startForeground!
        }
        return true
    }

    override fun onDestroy() {
        stopRecording()
        super.onDestroy()
    }

    override fun onMessage(msg: String) {
        updateMessage(msg)
    }

    fun startRecording(codec: Boolean, resultCode: Int, data: Intent): Boolean {
        val pm = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val mp = pm.getMediaProjection(resultCode, data)
        mRecorder = if (codec) RecorderMediaCodec(this, mp, this)
        else RecorderMediaRecorder(this, mp, this)
        val success = mRecorder!!.startVideo()
        setState(success)
        updateMessage("${mRecorder!!.name()} ${if (success) "recording" else "init fail"} ,")
        return success
    }

    private var recording = false
    private var message = ""

    private var callback: IRecorderCallback? = null

    fun stopRecording() {
        if (mRecorder != null && mRecorder!!.stop()) {
            updateMessage("complete!")
            setState(false)
        }
        stopSelf()
    }

    fun setListener(cb: IRecorderCallback?) {
        callback = cb
        if (cb == null) return
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
