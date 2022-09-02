package com.example

import android.app.Application
import android.app.WallpaperManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotlindemo.R
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Method

// test Material You , / dynamic color
class MD3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyIfAvailable(this)

//        Resources.getSystem().configuration
        val viewModel = ViewModelProvider(this).get(TestVM::class.java)
        setContentView(R.layout.activity_md3)
        val btn: Button = findViewById(R.id.button2)
        btn.append("$this")
        log("$this , onCreate . $theme")
//        startActivity(Intent().apply {
//            data = Uri.parse(
//                "theme://zhuti.xiaomi.com/page?S.REQUEST_RESOURCE_CODE=theme&S.REQUEST_RELATED_TITLE=2022MI&miref=theme&miback=true&" +
//                        "pageData=%7B'homeIndex'%3A0%2C%20'tabs'%3A%5B%7B'url'%3A'https%3A%2F%2Fs.mi.com%2FJg1eqz7D'%2C%20'title'%3A%7B'en_US'%3A'2022MI'%7D%7D%5D%7D"
//            )
//        })
        btn.setOnClickListener { viewModel.setWallpaper() }

        val assetss = resources.assets
        val tv: TextView = findViewById(R.id.editText)
        tv.text = assetss.toString()
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
        log("obtainRam= ${obtainRam()}")

    }

    val GB = 1024*1024*1024L
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

class TestVM(val app: Application) : AndroidViewModel(app) {
    fun setWallpaper() {
        viewModelScope.launch(Dispatchers.IO) {
            log("start set wallpaper")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val c1 = WallpaperManager.getInstance(app)
                    .getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
                WallpaperManager.getInstance(app)
                    .setResource(R.raw.wallpaper, WallpaperManager.FLAG_SYSTEM)
                val c2 = WallpaperManager.getInstance(app)
                    .getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
                log("set wallpaper complete ! c1=$c1, c2=$c2")
                if (c1 != null && c1 == c2) {
                    WallpaperManager.getInstance(app).clearWallpaper()
                    log("clear wallpaper!")
                }
            }
        }
    }
}