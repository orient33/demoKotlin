package com.example.media

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.example.log

/**
 * @author dundongfang on 2018/4/11.
 */
class PlayerHelper internal constructor(c: Context) : AudioManager.OnAudioFocusChangeListener {

    private val context = c
    private val audioManager: AudioManager? = c.getSystemService(AudioManager::class.java)
    private val afr: AudioFocusRequest
    private var mMediaPlayer: MediaPlayer? = null

    init {
        val b = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        afr = b.setOnAudioFocusChangeListener(this).build()
    }

    internal fun play() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer()
            try {
                val assetFileDescriptor = context.getAssets().openFd("jazz_in_paris.mp3")
                mMediaPlayer?.setDataSource(
                        assetFileDescriptor.getFileDescriptor(),
                        assetFileDescriptor.getStartOffset(),
                        assetFileDescriptor.getLength())
            } catch (e: Exception) {
                Log.w("df", "Failed to open assets file: ", e)
                return
            }
        }
        if (AudioManager.AUDIOFOCUS_GAIN == audioManager!!.requestAudioFocus(afr)) {
            log("request focus. OK")
        }
        //        mMediaPlayer.setDataSource("file://");

        mMediaPlayer?.prepare()
        mMediaPlayer?.start()
    }

    internal fun stop() {
        log("abandon focus.")
        audioManager!!.abandonAudioFocusRequest(afr)
        mMediaPlayer?.stop()
    }

    internal fun setListener() {

    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> log("gain.")
            AudioManager.AUDIOFOCUS_LOSS -> log("loss")
            else ->
                log(" focus change : $focusChange")

        }
    }
}
