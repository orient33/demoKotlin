package com.example.imagetest

import android.content.Context
import android.view.TextureView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

//ExoPlayer
class MyPlayer(context: Context) : DefaultLifecycleObserver {
    private val player: SimpleExoPlayer = SimpleExoPlayer.Builder(context)
        .setAudioAttributes(AudioAttributes.Builder().build(), true)
        .build()
    private val source = DefaultDataSourceFactory(context)

    private var mUrl: String? = null

    fun setUrl(url: String, surface: TextureView) {
        mUrl = url
        player.setVideoTextureView(surface)
        player.setMediaSource(
            ProgressiveMediaSource.Factory(source)
                .createMediaSource(MediaItem.fromUri(url))
        )
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