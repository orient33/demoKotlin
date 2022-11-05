package com.example.media

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kotlindemo.R
import com.example.kotlindemo.databinding.FragmentMediaBinding
import com.example.log

class MediaFragment : Fragment(), View.OnClickListener {
    lateinit var mBrowser: MediaBrowserCompat
    var mController: MediaControllerCompat? = null

    inner class ConnectCb : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            log("onConnected---")
            binding.state.text = "已连接."
            mController = MediaControllerCompat(
                requireContext().applicationContext,
                mBrowser.sessionToken
            )
            buildTransportControls(mController)
        }

        override fun onConnectionFailed() {
            log("onConnectionFailed")
            binding.state.text = "连接失败"
//            disControls()
        }

        override fun onConnectionSuspended() {
            binding.state.text = "连接挂起"
            log("onConnectionSuspended")
        }
    }

    private fun buildTransportControls(c: MediaControllerCompat?) {
        //设置Controller到window/activity
        MediaControllerCompat.setMediaController(requireActivity(), c)

        binding.play.setOnClickListener(this)
        binding.pause.setOnClickListener(this)
        log("build transport control , metadata :" + mController?.metadata)
        mController?.registerCallback(object : MediaControllerCompat.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                log("onMetadataChanged. $metadata")
            }

            override fun onSessionDestroyed() {
                log("onSessionDestroyed.")
            }

            override fun onSessionEvent(event: String?, extras: Bundle?) {
                log("onSessionEvent: $event")
            }

            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                log("onPlaybackStateChanged() $state")
                binding.playState.text = state.toString()
            }

            override fun onAudioInfoChanged(info: MediaControllerCompat.PlaybackInfo?) {
                log("onAudioInfoChanged . $info")
                binding.metadata.text = info.toString()
            }
        })
    }

//    private fun disControls() {
//        binding.play.setOnClickListener(null)
//        binding.pause.setOnClickListener(null)
//    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.pause -> mController?.transportControls?.stop()
            R.id.play -> mController?.transportControls?.play()
        }
    }

    private lateinit var binding: FragmentMediaBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaBinding.inflate(LayoutInflater.from(requireContext()))
        mBrowser = MediaBrowserCompat(
            requireContext(),
            ComponentName(requireContext(), PlayService::class.java),
            ConnectCb(),
            null
        )
        //
        val dualScreen = requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_ACTIVITIES_ON_SECONDARY_DISPLAYS)
        Toast.makeText(requireContext(), "Dual Screen: $dualScreen", Toast.LENGTH_LONG).show()
        log("双屏显示 : $dualScreen")
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mBrowser.connect()
    }

    override fun onStop() {
        super.onStop()
        mBrowser.disconnect()
    }
}
