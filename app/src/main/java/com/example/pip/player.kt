package com.example.pip

import android.net.Uri
import android.view.SurfaceView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.Injector
import com.example.log
import java.io.File

interface IPlayer {
    fun createPlayer(): IPlayer

    fun play(path: String)

    fun setView(sv: SurfaceView)

    fun release()
}

class EPlayerImpl() : IPlayer, Player.Listener {
    private var surfaceView: SurfaceView? = null
    private var player: ExoPlayer = ExoPlayer.Builder(Injector.sContext).build()
    override fun createPlayer(): IPlayer {
        player.addListener(this)
        log("build player $player")
        return this
    }

    override fun play(path: String) {
        log("play $path")
        val uri: Uri =
            when {
                path.startsWith("/") -> {
                    Uri.fromFile(File(path))
                }
                else -> {
                    Uri.parse(path)
                }
            }
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.setVideoSurfaceView(surfaceView)
        player.prepare()
        player.play()
    }

    override fun setView(sv: SurfaceView) {
        surfaceView = sv
    }

    override fun release() {
        log("release $player")
        player.removeListener(this)
        player.release()
    }

}