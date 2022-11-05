package com.example.media

import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.view.KeyEvent
import androidx.media.MediaBrowserServiceCompat
import com.example.log

/**
 * @author dundongfang on 2018/2/8.
 */
class PlayService : MediaBrowserServiceCompat(), PlaybackCallback {

    val stateBuilder: Builder = Builder()
    lateinit var mSession: MediaSessionCompat
    lateinit var helper: PlayerHelper

    override fun onPlaybackState(pb: PlaybackStateCompat) {
        mSession.setPlaybackState(pb)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {

    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot("root", null)
    }

    override fun onCreate() {
        super.onCreate()
        helper = PlayerHelper(this)
        mSession = MediaSessionCompat(this, "PlayService")
        mSession.setCallback(callback)
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        sessionToken = mSession.sessionToken
        mSession.isActive = true
        log("PlayService. onCreate")
    }

    override fun onBind(intent: Intent?): IBinder? {
        log("PlayService. onBind. $intent")
        return super.onBind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("PlayService. onStartCommand. $intent, $flags, $startId")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSession.release()
        log("PlayService. onDestroy")
    }

    //接收到来自controller 和 system/媒体按键 的控制
    private val callback = object : MediaSessionCompat.Callback() {
        override fun onMediaButtonEvent(mediaButtonIntent: Intent?): Boolean {
            val ke = mediaButtonIntent?.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
            log("KeyEvent ${ke?.keyCode} -- ${ke?.action}")
            return super.onMediaButtonEvent(mediaButtonIntent)
        }

        override fun onPlay() {
            startService(Intent(applicationContext, PlayService::class.java))
            log("PlayService. callback. onPlay(). and start Service.")
            mSession.isActive = true
            // play() player.
            helper.play()
            stateBuilder.setActions(getAvailableActions(true))
            stateBuilder.setState(STATE_PLAYING, 0L, 1f)
            mSession.setPlaybackState(stateBuilder.build())
            mSession.setMetadata(null)
        }

        override fun onStop() {
            log("PlayService. callback. onStop()")
            // pause() player.
            helper.stop()
            stateBuilder.setActions(getAvailableActions(false))
            stateBuilder.setState(STATE_STOPPED, 0L, 1f)
            mSession.setPlaybackState(stateBuilder.build())
            mSession.isActive = false
            stopSelf()
        }
    }

    private fun getAvailableActions(playing: Boolean): Long {
        var actions = ACTION_PLAY_PAUSE or
                ACTION_STOP or
                ACTION_PLAY_FROM_MEDIA_ID or
                ACTION_PLAY_FROM_SEARCH or
                ACTION_SKIP_TO_PREVIOUS or
                ACTION_SKIP_TO_NEXT
        actions = if (playing) {
            actions or ACTION_PAUSE
        } else {
            actions or ACTION_PLAY
        }
        return actions
    }
}