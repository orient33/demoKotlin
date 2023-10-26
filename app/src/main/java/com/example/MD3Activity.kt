package com.example

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlindemo.R
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Method

// test Material You , / dynamic color
class MD3Activity : BaseActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        DynamicColors.applyToActivityIfAvailable(
//            this,
//            DynamicColorsOptions.Builder()
//                .setOnAppliedCallback {
//                    log("dynamic color, onApplied. $it")
//                }.build()
//        )//设置与否 貌似没有区别

        val viewModel = ViewModelProvider(this).get(TestVM::class.java)
        setContentView(R.layout.activity_md3)
        val btn: Button = findViewById(R.id.button2)
        btn.append("$this, isDynamicColorAvailable: ${DynamicColors.isDynamicColorAvailable()}")
        log("onCreate .$this ,theme = $theme")
        btn.setOnClickListener { viewModel.setWallpaper() }
        btn.setOnLongClickListener {
            viewModel.clearWallpaper()
            true
        }

        val tv: TextView = findViewById(R.id.editText)
//        val assetss = resources.assets
//        tv.text = assetss.toString()
        /* ApkAssets如下
        0 = {ApkAssets@21968} "ApkAssets{path=<empty> and /system/framework/framework-res.apk}"
        1 = {ApkAssets@21969} "ApkAssets{path=/system/product/overlay/GoogleConfigOverlay.apk}"
        2 = {ApkAssets@21970} "ApkAssets{path=/system/product/overlay/GoogleWebViewOverlay.apk}"
        3 = {ApkAssets@21971} "ApkAssets{path=/system/product/overlay/PixelConfigOverlayCommon.apk}"
        4 = {ApkAssets@21972} "ApkAssets{path=/system/product/overlay/PixelFwResOverlay.apk}"
        5 = {ApkAssets@21973} "ApkAssets{path=/vendor/overlay/FrameworksResOverlayBegonia.apk}"
        6 = {ApkAssets@21974} "ApkAssets{path=/system/product/overlay/PixelConfigOverlay2018.apk}"
        7 = {ApkAssets@21975} "ApkAssets{path=/system/product/overlay/PixelConfigOverlay2019Midyear.apk}"
        8 = {ApkAssets@21976} "ApkAssets{path=/system/product/overlay/PixelConfigOverlay2019.apk}"
        9 = {ApkAssets@21977} "ApkAssets{path=/system/product/overlay/PixelConfigOverlay2021.apk}"
        10 = {ApkAssets@21978} "ApkAssets{path=<empty> and /data/app/~~8sAsnpYgoosdwZqfR9BdiQ==/com.example.kotlindemo-w-cj0dhxbaipJm3QOPDQHw==/base.apk}"
        11 = {ApkAssets@21979} "ApkAssets{path=/system/product/overlay/NavigationBarModeGestural/NavigationBarModeGesturalOverlay.apk}"
        12 = {ApkAssets@21980} "ApkAssets{path=/data/resource-cache/com.android.systemui-neutral-Lo22.frro}"
        13 = {ApkAssets@21981} "ApkAssets{path=/data/resource-cache/com.android.systemui-accent-Rbl3.frro}"
        */
        viewModel.appendText.observe(this) {
            tv.append(it)
        }
        viewModel.updateAppend("onCreate- ${System.currentTimeMillis()}")

        val color: TextView = findViewById(R.id.wallpaperColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val color1 = ContextCompat.getColor(this, android.R.color.system_accent1_500)
            val color2 = ContextCompat.getColor(this, android.R.color.system_accent2_500)
            val color3 = ContextCompat.getColor(this, android.R.color.system_accent3_500)
            color.text = "0x${Integer.toHexString(color1)}," +
                    "0x${Integer.toHexString(color2)}," +
                    "0x${Integer.toHexString(color3)}"
        }
        val image: ImageView = findViewById(R.id.wallpaper)
        viewModel.wallpaper.observe(this) {
            if (it == null) {
                image.setImageDrawable(ColorDrawable(Color.BLUE))
            } else {
                image.setImageBitmap(it)
            }
        }
    }
}

class TestVM(val app: Application) : AndroidViewModel(app) {
    val appendText = MutableLiveData<String>("")
    var count = 0
    val wallpaper = MutableLiveData<Bitmap?>(null)
    fun setWallpaper() {
        viewModelScope.launch(Dispatchers.IO) {
            log("start set wallpaper")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val wm = WallpaperManager.getInstance(app)
                val c1 = wm.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
                val id = if (count % 2 == 0) R.raw.wallpaper else R.raw.gradient49
                count++
                wm.setResource(id, WallpaperManager.FLAG_SYSTEM)
                val c2 = WallpaperManager.getInstance(app)
                    .getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
                log("set wallpaper complete ! c1=$c1,\nc2=$c2")

                val b = if (ActivityCompat.checkSelfPermission(
                        app,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    log("no permission for get wallpaper bitmap.")
                    null
                } else wm.drawable.toBitmapOrNull()
                wallpaper.postValue(b)
            }
        }
    }

    fun clearWallpaper() {
        viewModelScope.launch(Dispatchers.IO) {
            val wm = WallpaperManager.getInstance(app)
            log("clear wallpaper. reset default!")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                wm.clearWallpaper()
            } else {
                wm.clear()
            }
        }
    }

    fun updateAppend(msg: String) {
        val old = appendText.value
        appendText.value = "$old\n$msg"
//        viewModelScope.launch(Dispatchers.IO) {
//            val gb = obtainRam()
//            log("memory info : $gb GB")
//        }
    }

    val GB = 1024 * 1024 * 1024L
    private fun obtainRam(): Int {
        try {
            val clazz = Class.forName("android.os.Process")
            val method: Method = clazz.getMethod("getTotalMemory")
            val totalMemory = method.invoke(null) as Long
            // 引导和显示卡会出现一些内存，因此物理内存的大小始终小于总内存大小
            val totalPhysicalMemory: Long = totalMemory + GB - 1 and (GB - 1).inv()
            return (totalPhysicalMemory / 1024 / 1024 / 1024).toInt()
        } catch (e: Exception) {
            Log.e("", e.message, e)
        }
        return -1
    }
}