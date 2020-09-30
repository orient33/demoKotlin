package com.example.kotlindemo

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.NetAPI
import com.example.TestNet
import com.example.log
import com.example.toast
import kotlinx.android.synthetic.main.test_round_image_view.*
import retrofit2.Retrofit

/**
 * @author dundongfang on 2018/4/26.
 */
@Keep
class TestFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.test_round_image_view, container, false)
    }

    @SuppressLint("StaticFieldLeak")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val wm  = WallpaperManager.getInstance(requireContext())
//        wm.setBitmap(1)

    }

    private fun test() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            Glide.with(requireActivity())
                .load("/storage/emulated/0/MIUI/.videowallpaper/巍峨雪山_&_78637290-1135-411d-b85b-38cb100889dd")
                .placeholder(R.drawable.asset)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        log("onLoadFailed. ")
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        log("onResourceReady..")
                        return true
                    }

                })
                .into(imageV)
        } else {
            toast(requireContext(), "没权限")
        }
    }
}