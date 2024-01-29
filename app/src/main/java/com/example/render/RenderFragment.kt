package com.example.render

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.ColorFilter
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.kotlindemo.R
import com.example.kotlindemo.databinding.FragmentRenderBinding
import com.example.log

const val KEY_URL = "key-url"

class RenderFragment : Fragment(R.layout.fragment_render) {
    lateinit var binding: FragmentRenderBinding
    var imageUrl = ""
    var bitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentRenderBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            imageUrl = it.getString(KEY_URL) ?: ""
            if (imageUrl == "") {
                binding.image.setImageResource(R.drawable.asset)
                binding.image22.setImageResource(R.drawable.asset)
            } else {
                Glide.with(view).load(imageUrl).into(binding.image)
                Glide.with(view).load(imageUrl).into(binding.image22)
                binding.image.postDelayed({
                    bitmap = (binding.image.drawable as BitmapDrawable).bitmap
                }, 2000)
            }
        }
        binding.seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateBlur(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun updateBlur(progress: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val re = RenderEffect.createBlurEffect(
                progress.toFloat()/8,
                progress.toFloat()/8,
                Shader.TileMode.MIRROR
            )
            val b =  initBlend(progress)
            if (b != null) {
                log("update blur&black for $progress")
                binding.image22.setRenderEffect(RenderEffect.createChainEffect(re, b))
            } else {
                log("update blur for $progress")
                binding.image22.setRenderEffect(re)
            }
        } else {
            log("not support now. for RenderEffect.")
        }
    }

    var source: RenderEffect? = null
    val target = 0x4D

    @RequiresApi(Build.VERSION_CODES.S)
    private fun initBlend(progress: Int): RenderEffect? {
        if (source == null) {
            val lBitmap = bitmap
            if (lBitmap == null) {
                log("bitmap null . delay try.")
                return null
            }
            source = RenderEffect.createBitmapEffect(lBitmap)
        }
//        val w = binding.image.measuredWidth
//        val h = binding.image.measuredHeight
        val dst = RenderEffect.createBitmapEffect(
            Bitmap.createBitmap(400, 600, Bitmap.Config.ARGB_8888).apply {
                val p = (target * (progress / 100f)).toInt() //eg : 0x3d
                val pp = p shl 24 // eg: 0x3d000000
                log("set for blend . 0x${Integer.toHexString(target)} x $progress % =  0x${Integer.toHexString(p)} shl 24 is 0x${Integer.toHexString(pp)}")
                eraseColor(pp)
            }
        )
        return RenderEffect.createBlendModeEffect(dst, source!!, BlendMode.MULTIPLY)
    }
}
