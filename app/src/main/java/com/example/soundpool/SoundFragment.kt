package com.example.soundpool

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.StatusBarTool
import com.example.kotlindemo.R

/**
 * @author dundongfang on 2018/9/28.
 */
@TargetApi(21)
class SoundFragment : androidx.fragment.app.Fragment() {

    private var soundId = -1
    private val wheelSound = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                .setLegacyStreamType(11)
                .build()
        )
        .build()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sound, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        StatusBarTool.adaptSysBar(view)
        soundId = wheelSound.load(context, R.raw.wheel_sound, 1)
        val play = view.findViewById<View>(R.id.play)
        val text = view.findViewById<TextView>(R.id.text)
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
        return when {
            s == null -> "null"
            s.isEmpty() -> "空"
            else -> {
                val sb = StringBuilder()
                for (sub in s) {
                    sb.append(sub).append(",")
                }
                sb.toString()
            }
        }
    }

}
