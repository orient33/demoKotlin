package com.example.screenrecoder

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.kotlindemo.MainActivity
import com.example.kotlindemo.R
import com.example.toast

const val ACTION_START = "action.start"
const val EXTRA_CODE = "extra.code"
const val EXTRA_DATA = "extra.data"
const val n_id = 1
const val n_channel = "d"

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class RecorderService : Service() {
    lateinit var notification: Notification
    private var recorderTool: RecorderTool? = null
    override fun onCreate() {
        super.onCreate()
        notification = NotificationCompat.Builder(this, n_channel)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setChannelId(n_channel)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.screen_recorder))
            .setContentInfo(getString(R.string.screen_recorder))
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
                    n_id, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                )
            } else {
                startForeground(n_id, notification)
            }
        } else {
            nm.notify(n_id, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (ACTION_START == intent.action) {
                val code = intent.getIntExtra(EXTRA_CODE, -111)
                val data = intent.getParcelableExtra<Intent>(EXTRA_DATA)
                if (data != null && code != -111) {
                    if (!start(code, data)) stopSelf()
                } else {
                    toast(this, "code or data 无效!无法录屏.")
                    stopSelf()
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        recorderTool?.stop()
        super.onDestroy()
    }

    private fun start(resultCode: Int, data: Intent): Boolean {
        val pm = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val mp = pm.getMediaProjection(resultCode, data)
        recorderTool = RecorderTool(this, mp)
        return recorderTool!!.startVideo()
    }
}
