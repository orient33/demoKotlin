package com.example.imagetest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kotlindemo.IActivity
import com.example.kotlindemo.R
import com.example.log
import kotlinx.android.synthetic.main.fragment_image_home.*


class ImageHomeFragment : Fragment(), View.OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ok.setOnClickListener(this)
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
                val lm = layoutManager.isChecked
                val local = localImage.isChecked
                val reset = resetVH.isChecked
                val page = when (spinner.selectedItemPosition) {
                    1 -> "THEME"
                    2 -> "WALLPAPER"
                    3 -> "VIDEO_WALLPAPER"
                    else -> "HYBRID"
                }
                putBoolean(Image1Fragment.KEY_LM, lm)
                putBoolean(Image1Fragment.KEY_LOCAL, local)
                putBoolean(Image1Fragment.KEY_RESET, reset)
                putBoolean(Image1Fragment.KEY_ROUND, rounded.isChecked)
                putBoolean(Image1Fragment.KEY_CARD_VIEW, cardView.isChecked)
                putBoolean(Image1Fragment.KEY_IGNORE_GIF, ignoreGif.isChecked)
                putInt(Image1Fragment.KEY_PAGE_SIZE, spinner2.selectedItemPosition)
                putString(Image1Fragment.KEY_PAGE, page)
            })
        }
    }
}