package com.example.soundpool

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.*
import com.example.kotlindemo.R
import kotlinx.android.synthetic.main.fragment_sound.*

import java.io.File
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * @author dundongfang on 2018/9/28.
 */
@TargetApi(21)
class SoundFragment : Fragment() {

    private var soundId = -1
    private val wheelSound = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setLegacyStreamType(11)
                .build()
        )
        .build()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sound, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        soundId = wheelSound.load(context, R.raw.wheel_sound, 1)
        play.setOnClickListener {
            if (soundId == -1) {
                text.append("load not complete! \n")
            } else {
                val sId = wheelSound.play(soundId, 1f, 1f, 1, 0, 1f)
                text.append("$sId, ")
            }
        }
    }

    private fun array2String(s: Array<String>?): String {
        if (s == null)
            return "null"
        else if (s.size == 0)
            return "空"
        else {
            val sb = StringBuilder()
            for (sub in s) {
                sb.append(sub).append(",")
            }
            return sb.toString()
        }
    }

}
