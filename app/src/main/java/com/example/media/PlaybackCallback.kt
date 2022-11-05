package com.example.media

import android.support.v4.media.session.PlaybackStateCompat

/**
 * @author dundongfang on 2018/4/11.
 */
interface PlaybackCallback {

    fun onPlaybackState(pb: PlaybackStateCompat)
}