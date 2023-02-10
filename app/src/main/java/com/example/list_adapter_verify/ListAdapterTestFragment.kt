package com.example.list_adapter_verify

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.kotlindemo.RecognizeBgDrawable
import com.example.kotlindemo.databinding.FragmentListAdapterTestBinding

class ListAdapterTestFragment : Fragment() {
    private var _binding :FragmentListAdapterTestBinding?=null
    private val binding get() = _binding!!
    private var adapter: MyListAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListAdapterTestBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MyListAdapter()
        binding.rv.layoutManager = LinearLayoutManager(
            view.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rv.adapter = adapter

        binding.random.setOnClickListener {
            run {
                doRandom()
            }
        }
        val lottie = binding.lottie
        lottie.setOnClickListener {
            run {
                if (lottie.isAnimating) {
                    lottie.cancelAnimation()
                } else {
                    lottie.playAnimation()
                }
            }
        }

        lottie.setOnLongClickListener {
            lottie.speed = if (lottie.progress == 1f) -1f else 1f
            lottie.playAnimation()
            true
        }
        val bg = RecognizeBgDrawable(Color.RED)
        bg.setBounds(0, 0, 1000, 200)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.imageDemo.setImageDrawable(bg)
        }
        binding.seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                val width = bg.bounds.right
//                bg.setBounds(
//                    0, 0,
//                    if (width == 1000) 1400 else 1000, 200
//                )
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    lottie.progress = progress / 100f
                }
            }
        })
    }

    private fun doRandom() {
        activity?.startActivity(Intent("miui.intent.action.WALLPAPER_PICKER_PAGE")
            .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        )
    }
}
