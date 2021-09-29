package com.example.imagetest

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.log

// use MediaPlayer 播放
class MyPlayer2(val context: Context) : DefaultLifecycleObserver,
    TextureView.SurfaceTextureListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener {
    private var mUrl: String? = null
    private var mPlayer: MediaPlayer? = null
    private var mReady = false
    fun setUrl(url: String, v: TextureView) {
        mUrl = url
        val player = MediaPlayer.create(context, Uri.parse(url))
        player.setOnPreparedListener(this)
        v.surfaceTextureListener = this
        mPlayer = player
    }

    override fun onPrepared(mp: MediaPlayer) {
        log("onPrepared.. ")
        if (mReady) {
            log("player.start() ")
            mp.start()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mPlayer?.release()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d("df", "onError: $what,  $extra")
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        log("onSurfaceTextureAvailable. ")
        mPlayer?.setSurface(Surface(surface))
        mPlayer?.start()
        mReady = true
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

        log("onSurfaceTextureSize. $width - $height ")
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }
}