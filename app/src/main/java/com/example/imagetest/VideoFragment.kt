package com.example.imagetest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import com.example.kotlindemo.R

private const val ARG_PARAM1 = "video-url"

class VideoFragment : Fragment() {
    private var param1: String? = null
    private lateinit var player: MyPlayer2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        player = MyPlayer2(requireContext())
        lifecycle.addObserver(player)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val url = param1
        val textureView = view.findViewById<TextureView>(R.id.textureView)
        if (url != null) {
            player.setUrl(url, textureView)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String?) =
            VideoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}