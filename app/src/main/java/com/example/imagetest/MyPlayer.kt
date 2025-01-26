package com.example.imagetest

import android.content.Context
import android.view.TextureView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

//Media3. androidx
class MyPlayer(context: Context) : DefaultLifecycleObserver {
    private val player  = ExoPlayer.Builder(context)
        .setAudioAttributes(AudioAttributes.Builder().build(), true)
        .build()

    private var mUrl: String? = null

    fun setUrl(url: String, surface: TextureView) {
        mUrl = url
        player.setVideoTextureView(surface)
        val mi = MediaItem.fromUri(url)
        player.setMediaItem(mi)
        player.playWhenReady = true
        player.prepare()
    }

    override fun onStart(owner: LifecycleOwner) {
        player.play()
    }

    override fun onStop(owner: LifecycleOwner) {
        player.pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        player.release()
    }
}