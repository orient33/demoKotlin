package com.example.imagetest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kotlindemo.IActivity
import com.example.kotlindemo.R
import com.example.kotlindemo.databinding.FragmentImageHomeBinding

class ImageHomeFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentImageHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.ok.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ok -> {
                toList()
            }
        }
    }

    private fun toList() {
        val act = requireActivity()
        if (act is IActivity) {
            act.toFragmentWithArgs(Image1Fragment::class.java.name, Bundle().apply {
                val lm = binding.layoutManager.isChecked
                val local = binding.localImage.isChecked
                val reset = binding.resetVH.isChecked
                val page = when (binding.spinner.selectedItemPosition) {
                    1 -> "THEME"
                    2 -> "WALLPAPER"
                    3 -> "VIDEO_WALLPAPER"
                    else -> "HYBRID"
                }
                putBoolean(Image1Fragment.KEY_LM, lm)
                putBoolean(Image1Fragment.KEY_LOCAL, local)
                putBoolean(Image1Fragment.KEY_RESET, reset)
                putBoolean(Image1Fragment.KEY_ROUND, binding.rounded.isChecked)
                putBoolean(Image1Fragment.KEY_CARD_VIEW, binding.cardView.isChecked)
                putBoolean(Image1Fragment.KEY_IGNORE_GIF, binding.ignoreGif.isChecked)
                putInt(Image1Fragment.KEY_PAGE_SIZE, 2 + binding.spinner2.selectedItemPosition)
                putString(Image1Fragment.KEY_PAGE, page)
            })
        }
    }
}