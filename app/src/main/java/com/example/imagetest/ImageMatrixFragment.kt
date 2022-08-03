package com.example.imagetest

import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import com.example.kotlindemo.databinding.FragmentImage1Binding
import com.example.kotlindemo.databinding.FragmentImageMatrixBinding

//矩阵matrix在imageView上的demo
class ImageMatrixFragment : Fragment(), OnSeekBarChangeListener {
    private var _binding: FragmentImageMatrixBinding? = null
    private val binding get() = _binding!!
    private val matrix = Matrix()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageMatrixBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        matrix.reset()
        binding.imageView.scaleType = ImageView.ScaleType.MATRIX
        binding.imageView.imageMatrix = matrix
        binding.scaleX.setOnSeekBarChangeListener(this)
        binding.scaleY.setOnSeekBarChangeListener(this)
        binding.transX.setOnSeekBarChangeListener(this)
        binding.transY.setOnSeekBarChangeListener(this)
        binding.skewX.setOnSeekBarChangeListener(this)
        binding.skewY.setOnSeekBarChangeListener(this)
        binding.rotate.setOnSeekBarChangeListener(this)
        binding.button3.setOnClickListener {
            matrix.reset()
            binding.imageView.imageMatrix = matrix
        }
    }

    private var scaleX = 1f
    private var scaleY = 1f
    private var transX = 0f
    private var transY = 0f
    private var skewX = 1f
    private var skewY = 1f
    private var rotate = 0f
    override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {
        when (bar) {
            binding.scaleX -> {
                scaleX = calculate(progress, 1.5f, 0.5f)
                matrix.setScale(scaleX, scaleY)
            }
            binding.scaleY -> {
                scaleY = calculate(progress, 1.5f, 0.5f)
                matrix.setScale(scaleX, scaleY)
            }
            binding.transX -> {
                transX = calculate(progress, 200f, -200f)
                matrix.setTranslate(transX, transY)
                matrix.preScale(scaleX, scaleY)
            }
            binding.transY -> {
                transY = calculate(progress, 200f, -200f)
                matrix.setTranslate(transX, transY)
                matrix.preScale(scaleX, scaleY)
            }
            binding.skewX -> {
                skewX = calculate(progress, 2f, -2f)
                matrix.setSkew(skewX, skewY)
            }
            binding.skewY -> {
                skewY = calculate(progress, 2f, -2f)
                matrix.setSkew(skewX, skewY)
            }
            else -> {
                rotate = calculate(progress, 180f, -180f)
                matrix.setRotate(rotate)
            }
        }
        binding.imageView.imageMatrix = matrix
        Log.i("df", "${bar.id}, onProgress: $progress , set matrix $matrix")
    }

    private fun calculate(pro: Int, max: Float, min: Float): Float {
        return min + (max - min) * (pro / 100f)
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }
}