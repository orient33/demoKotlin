package com.example.pip

import android.net.Uri
import android.view.SurfaceView
import com.example.App
import com.example.log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import java.io.File

interface IPlayer {
    fun createPlayer(): IPlayer

    fun play(path: String)

    fun setView(sv: SurfaceView)

    fun release()
}

class EPlayerImpl() : IPlayer, Player.Listener {
    var surfaceView: SurfaceView? = null
    var ePlayer: ExoPlayer? = null
    override fun createPlayer(): IPlayer {
        val player = ExoPlayer.Builder(App.sContext).build()
        player.addListener(this)
        ePlayer = player
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
//            path.startsWith("content://") -> {
//                Uri.parse(path)
//            }
//            path.startsWith("file://") -> {
//                Uri.parse(path)
//            }
                else -> {
                    Uri.parse(path)
                }
            }
        val player = ePlayer ?: return
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
        log("release $ePlayer")
        ePlayer?.removeListener(this)
        ePlayer?.release()
    }

}