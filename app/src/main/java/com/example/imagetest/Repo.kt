package com.example.imagetest

import android.net.Uri
import com.example.TestNet
import com.example.log
import okhttp3.internal.toImmutableList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*

object Repo {
    //load /sdcard/MIUI/.wallpaper/xxx

    fun loadLocalImage(): List<ImageData> {
        val result = mutableListOf<ImageData>()
        val dirs = listOf(
            "/sdcard/MIUI/.wallpaper",
            "/sdcard/MIUI/.cache/resource/wallpaper/preview"
        )
        for (value in dirs) {
            val dir = File(value)
            dir.listFiles()?.forEach {
                val imageData = ImageData(it.name, Uri.fromFile(it).toString())
                result.add(imageData)
            }
        }
        return result
    }

    fun loadNetImage(page: String?, ignoreGif: Boolean, start: Int = 0): List<ImageData> {
        val r = Retrofit.Builder()
            .baseUrl(RecommendRequestInterface.BASE_URL)
            .client(TestNet.createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = r.create(RecommendRequestInterface::class.java)
        val category = page ?: "HYBRID"
        val call = api.getHomePageList(category, start, 40)
        val response = call.execute()
        if (response.isSuccessful) {
            val list = response.body()?.toImageData(ignoreGif)
            if (list == null || list.isEmpty()) {
                log("fail list null or empty.")
            } else {
                return list
            }
        }
        return Collections.emptyList<ImageData>()
    }
}